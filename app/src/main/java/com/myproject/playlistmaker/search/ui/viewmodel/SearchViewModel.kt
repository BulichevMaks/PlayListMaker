package com.myproject.playlistmaker.search.ui.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.search.domain.madel.Track
import com.myproject.playlistmaker.search.domain.usecase.ItemClickUseCase
import com.myproject.playlistmaker.search.domain.usecase.ItemHistoryClickUseCase
import com.myproject.playlistmaker.search.domain.usecase.SearchTracksUseCase
import com.myproject.playlistmaker.search.domain.usecase.ShowHistoryUseCase
import com.myproject.playlistmaker.search.domain.usecase.StopActivityUseCase
import com.myproject.playlistmaker.search.ui.models.SearchState

class SearchViewModel(
    private val itemClickUseCase: ItemClickUseCase,
    private val searchTracksUseCase: SearchTracksUseCase,
    private val itemHistoryClickUseCase: ItemHistoryClickUseCase,
    private val showHistoryUseCase: ShowHistoryUseCase,
    private val stopActivityUseCase: StopActivityUseCase,
    private val application: Application
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }

    private val handler = Handler(Looper.getMainLooper())

    private var latestSearchText: String? = null


    private val historyTracksLiveData = MediatorLiveData<ArrayList<Track>>()

    fun observeHistoryTracks(): LiveData<ArrayList<Track>> {
        historyTracksLiveData.value = showHistoryUseCase.execute()
        return historyTracksLiveData
    }

    private val stateLiveData = MutableLiveData<SearchState>()

    private val mediatorStateLiveData = MediatorLiveData<SearchState>().also { liveData ->
        liveData.addSource(stateLiveData) { movieState ->
            liveData.value = when (movieState) {
                is SearchState.Content -> SearchState.Content(movieState.tracks)
                is SearchState.Empty -> movieState
                is SearchState.Error -> movieState
                is SearchState.ServerError -> movieState
                is SearchState.Loading -> movieState
            }
        }
    }

    fun observeState(): LiveData<SearchState> = mediatorStateLiveData

    private fun searchRequest(newSearchText: String? = latestSearchText) {
        if (newSearchText != null) {
            if (newSearchText.isNotEmpty()) {
                renderState(SearchState.Loading)

                searchTracksUseCase.execute(newSearchText, object : SearchTracksUseCase.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?, errorMessage: String?, code: Int) {
                        val tracks = mutableListOf<Track>()
                        if (foundTracks != null) {
                            tracks.addAll(foundTracks)
                        }

                        when {
                            errorMessage != null -> {
                                renderState(
                                    SearchState.Error(
                                        errorMessage = application.getString(R.string.error_message),
                                    )
                                )
                            }

                            tracks.isEmpty() -> {
                                renderState(
                                    SearchState.Empty(
                                        message = application.getString(R.string.nothing_found),
                                    )
                                )
                            }

                            else -> {
                                renderState(
                                    SearchState.Content(
                                        tracks = tracks,
                                    )
                                )
                            }
                        }
                    }
                })
            }
        }
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun saveTracksToHistory(
        tracks: ArrayList<Track>,
        historyTracks: ArrayList<Track>,
        position: Int
    ) {
        itemClickUseCase.execute(tracks, historyTracks, position)
    }

    fun saveTrackToSharedPref(historyTracks: ArrayList<Track>, position: Int) {
        itemHistoryClickUseCase.execute(historyTracks, position)
    }

    fun writeTracksToPref(tracks: ArrayList<Track>) {
        stopActivityUseCase.execute(tracks)
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    fun searchDebounceRefresh(changedText: String) {

        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

}