package com.myproject.playlistmaker.medialibrary.domain.api

import android.content.Context
import android.net.Uri
import com.myproject.playlistmaker.medialibrary.domain.model.Playlist
import com.myproject.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface PlayListsRepository {

    suspend fun insertPlaylist(playlist: Playlist)

    fun saveImageToPrivateStorage(uri: Uri, context: Context): Uri?

    suspend fun getPlaylists(): Flow<List<Playlist>>

    suspend fun insertPlaylistTrack(playList: Playlist, track: Track)
}