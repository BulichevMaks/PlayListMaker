package com.myproject.playlistmaker.medialibrary.data.repository

import android.net.Uri
import com.myproject.playlistmaker.db.room.AppDatabase
import com.myproject.playlistmaker.medialibrary.data.api.InternalStorageClient
import com.myproject.playlistmaker.medialibrary.data.converters.PlayListDbConvertor
import com.myproject.playlistmaker.medialibrary.data.converters.PlaylistTrackConverter
import com.myproject.playlistmaker.medialibrary.domain.api.PlayListsRepository
import com.myproject.playlistmaker.medialibrary.domain.model.Playlist
import com.myproject.playlistmaker.search.domain.model.Track
import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class PlayListsRepositoryImpl(
    private val internalStorageClient: InternalStorageClient,
    private val appDatabase: AppDatabase,
    private val playListDbConvertor: PlayListDbConvertor,
    private val playlistTrackConverter: PlaylistTrackConverter,
): PlayListsRepository {

    override suspend fun insertPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().insertPlaylist(playListDbConvertor.toPlaylistEntity(playlist))
    }

    override fun saveImageToPrivateStorage(uri: Uri, context: Context): Uri?  {
        return internalStorageClient.saveImageToPrivateStorage(uri, context)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getPlaylists()
        emit(playListDbConvertor.toPlaylist(playlists))
    }

    override suspend fun insertPlaylistTrack(playList: Playlist, track: Track) {
        playList.tracks.add(track)
        insertPlaylist(playList)
        appDatabase.playlistDao()
            .insertPlaylistTrack(playlistTrackConverter.map(track, playList.id))
    }

}