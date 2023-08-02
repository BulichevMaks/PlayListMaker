package com.myproject.playlistmaker.search.ui.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.search.domain.model.Track
import com.myproject.playlistmaker.search.domain.usecase.ClearHistoryUseCase
import com.myproject.playlistmaker.search.domain.usecase.ItemClickUseCase
import com.myproject.playlistmaker.search.domain.usecase.ItemHistoryClickUseCase
import com.myproject.playlistmaker.search.domain.usecase.SearchTracksUseCase
import com.myproject.playlistmaker.search.domain.usecase.ShowHistoryUseCase
import com.myproject.playlistmaker.search.ui.models.SearchState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val itemClickUseCase: ItemClickUseCase,
    private val searchTracksUseCase: SearchTracksUseCase,
    private val itemHistoryClickUseCase: ItemHistoryClickUseCase,
    private val showHistoryUseCase: ShowHistoryUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase,
    private val application: Application
) : ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }


    private var searchJob: Job? = null

    private var latestSearchText: String? = null

    private val historyTracksLiveData = MutableLiveData<ArrayList<Track>>()

    private val stateLiveData = MutableLiveData<SearchState>()

    private val mediatorStateLiveData = MediatorLiveData<SearchState>().also { liveData ->
        liveData.addSource(stateLiveData) { screenState ->
            liveData.value = when (screenState) {
                is SearchState.Content -> SearchState.Content(screenState.tracks)
                is SearchState.Empty -> screenState
                is SearchState.Error -> screenState
                is SearchState.ServerError -> screenState
                is SearchState.Loading -> screenState
            }
        }
    }

    fun observeState(): LiveData<SearchState> = mediatorStateLiveData

    fun observeHistoryTracks(): LiveData<ArrayList<Track>> {
        historyTracksLiveData.value = showHistoryUseCase.execute()
        return historyTracksLiveData
    }

    private fun searchRequest(newSearchText: String? = latestSearchText) {

        if (newSearchText != null) {
            if (newSearchText.isNotEmpty()) {
                renderState(SearchState.Loading)

                viewModelScope.launch {
                    searchTracksUseCase
                        .execute(newSearchText)
                        .collect { pair ->
                            processResult(pair.first, pair.second)
                        }
                }
            }
        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
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

    fun clearHistory() {
        clearHistoryUseCase.execute()
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
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

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        searchDebounceRefresh(changedText)
    }

    fun searchDebounceRefresh(changedText: String) {
        this.latestSearchText = changedText
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(changedText)
        }
    }

    fun isHistoryShouldShow(): Boolean {
        return latestSearchText.isNullOrEmpty()
    }

}