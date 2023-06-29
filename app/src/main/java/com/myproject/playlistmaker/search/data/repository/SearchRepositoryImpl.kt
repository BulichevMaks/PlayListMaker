package com.myproject.playlistmaker.search.data.repository

import com.myproject.playlistmaker.search.data.Mapper
import com.myproject.playlistmaker.search.data.api.NetworkClient
import com.myproject.playlistmaker.search.data.api.TracksSharedPrefStorage
import com.myproject.playlistmaker.search.data.network.TrackResponse
import com.myproject.playlistmaker.search.data.network.TracksSearchRequest
import com.myproject.playlistmaker.search.domain.api.SearchRepository
import com.myproject.playlistmaker.search.domain.madel.Track
import com.myproject.playlistmaker.search.domain.madel.SearchResult


class SearchRepositoryImpl(
    private val tracksNetworkStorage: NetworkClient,
    private val tracksSharedPrefStorage: TracksSharedPrefStorage
) : SearchRepository {

    override fun getTracksByName(trackName: String): SearchResult<List<Track>> {
        val response = tracksNetworkStorage.doRequest(TracksSearchRequest(trackName))
        return when (response.resultCode) {

            -1 -> SearchResult.Error(message = "Проверьте подключение к интернету", code = -1)
            200 -> SearchResult.Success(data = (response as TrackResponse).results, code = 200)
            else -> SearchResult.Error(message = "Ошибка сервера", code = response.resultCode)
        }

    }

    override fun saveTrackToSharedPref(track: Track) {
        tracksSharedPrefStorage.save(Mapper.transformTrackDataSourceToDomainModels(track))
    }

    override fun getTrackFromSharedPref(): Track {
        return Mapper.transformTrackDataSourceToDomainModels(tracksSharedPrefStorage.get())
    }

    override fun readTracksFromSharedPref(): ArrayList<Track>? {
        return tracksSharedPrefStorage.getTracksFromPref()
            ?.let { it }
    }

    override fun writeTracksToPref(tracks: ArrayList<Track>) {
        tracksSharedPrefStorage.writeTracksToPref(
            Mapper.transformDomainModelsToTracksDataSource(
                tracks
            )
        )
    }


}