package com.myproject.playlistmaker.medialibrary.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.medialibrary.ui.FavoriteState

class FavoriteTracksViewModel(private val application: Application) : ViewModel() {

    private val stateLiveData = MutableLiveData<FavoriteState>()
    fun observeState(): LiveData<FavoriteState> = stateLiveData

    init {

        stateLiveData.postValue(FavoriteState.Empty(application.getString(R.string.favorite_tracks_empty)))

    }
}