package com.myproject.playlistmaker.medialibrary.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.medialibrary.ui.PlaylistState

class PlaylistsViewModel(private val application: Application) : ViewModel() {

    private val stateLiveData = MutableLiveData<PlaylistState>()
    fun observeState(): LiveData<PlaylistState> = stateLiveData

    init {

        stateLiveData.postValue(PlaylistState.Empty(application.getString(R.string.playlist_empty)))

    }
}