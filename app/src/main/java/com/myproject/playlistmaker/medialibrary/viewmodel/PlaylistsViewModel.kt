package com.myproject.playlistmaker.medialibrary.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.medialibrary.domain.api.PlayListInteractor
import com.myproject.playlistmaker.medialibrary.domain.model.Playlist
import com.myproject.playlistmaker.medialibrary.ui.PlaylistState
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val application: Application,
    private val playListInteractor: PlayListInteractor
) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistState>()

    private val mediatorStateLiveData = MediatorLiveData<PlaylistState>().also { liveData ->
        liveData.addSource(stateLiveData) { screenState ->
            liveData.value = when (screenState) {
                is PlaylistState.Content -> PlaylistState.Content(screenState.playlists)
                is PlaylistState.Empty -> screenState

            }
        }
    }
    fun observeState(): LiveData<PlaylistState> = mediatorStateLiveData

    fun getPlaylists() {
        viewModelScope.launch {
            playListInteractor
                .getPlaylists()
                .collect { playlists ->
                    processResult(playlists)
                }
        }
    }
    private fun processResult(foundPlaylists: List<Playlist>?) {
        val playlists = mutableListOf<Playlist>()
        if (foundPlaylists != null) {
            playlists.addAll(foundPlaylists)
        }

        when {
            playlists.isEmpty() -> {
                renderState(
                    PlaylistState.Empty(application.getString(R.string.playlist_empty))
                )
            }
            else -> {
                renderState(
                    PlaylistState.Content(
                        playlists = playlists,
                    )
                )
            }
        }
    }
    private fun renderState(state: PlaylistState) {
        stateLiveData.postValue(state)
    }
}