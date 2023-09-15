package com.myproject.playlistmaker.medialibrary.domain.usecase

import android.content.Context
import android.net.Uri
import com.myproject.playlistmaker.medialibrary.domain.api.PlayListInteractor
import com.myproject.playlistmaker.medialibrary.domain.api.PlayListsRepository
import com.myproject.playlistmaker.medialibrary.domain.model.Playlist
import com.myproject.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlayListInteractorImpl(
    private val playListsRepository: PlayListsRepository,
): PlayListInteractor {

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playListsRepository.getPlaylists()
    }


    override suspend fun insertPlaylistTrack(playList: Playlist, track: Track) {
        playListsRepository.insertPlaylistTrack(playList,track)
    }

    override suspend fun insertPlaylist(playlist: Playlist) {
        playListsRepository.insertPlaylist(playlist)
    }

    override fun saveImageToPrivateStorage(uri: Uri, context: Context): Uri? {
        return playListsRepository.saveImageToPrivateStorage(uri, context)
    }
}
