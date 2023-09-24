package com.myproject.playlistmaker.medialibrary.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.myproject.playlistmaker.medialibrary.domain.api.PlayListInteractor
import com.myproject.playlistmaker.medialibrary.domain.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddNewPlayListViewModel(
    private val playListInteractor: PlayListInteractor
) : ViewModel() {

    private var _uriLiveData = MutableLiveData<Uri?>()
    val uriLiveData: LiveData<Uri?> = _uriLiveData
    fun insertPlaylist(name: String, image: Uri?, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            playListInteractor.insertPlaylist(
                Playlist(
                    name = name,
                    image = image,
                    description = description
                )
            )
        }
    }

    fun saveToStorage(uri: Uri, context: Context) {
        _uriLiveData.value = playListInteractor.saveImageToPrivateStorage(uri, context)
    }
}