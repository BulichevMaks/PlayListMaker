package com.myproject.playlistmaker.medialibrary.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.medialibrary.domain.api.FavoriteTracksInteractor
import com.myproject.playlistmaker.medialibrary.ui.FavoriteState
import com.myproject.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val application: Application,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
) : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoriteState>()

    private val mediatorStateLiveData = MediatorLiveData<FavoriteState>().also { liveData ->
        liveData.addSource(stateLiveData) { screenState ->
            liveData.value = when (screenState) {
                is FavoriteState.Content -> FavoriteState.Content(screenState.tracks)
                is FavoriteState.Empty -> screenState
                is FavoriteState.Loading -> FavoriteState.Loading
            }
        }
    }
    fun observeState(): LiveData<FavoriteState> = mediatorStateLiveData

    fun getTracksFromDb() {
        renderState(FavoriteState.Loading)
        viewModelScope.launch {
            favoriteTracksInteractor
                .getTracks()
                .collect { tracks ->
                    processResult(tracks)
                }
        }
    }

    private fun processResult(foundTracks: List<Track>?) {
        val tracks = mutableListOf<Track>()
        if (foundTracks != null) {
            tracks.addAll(foundTracks)
        }
        when {
            tracks.isEmpty() -> {
                renderState(FavoriteState.Empty(application.getString(R.string.favorite_tracks_empty)))
            }
            else -> {
                renderState(FavoriteState.Content(tracks = tracks))
            }
        }
    }

    private fun renderState(state: FavoriteState) {
        stateLiveData.postValue(state)
    }

    fun saveTrackToSharedPref(tracks: ArrayList<Track>, position: Int) {
        favoriteTracksInteractor.saveTrackToSharedPref(tracks[position])
    }
}