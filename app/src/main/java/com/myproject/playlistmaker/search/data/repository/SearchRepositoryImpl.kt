package com.myproject.playlistmaker.search.data.repository

import com.myproject.playlistmaker.search.data.Mapper
import com.myproject.playlistmaker.search.data.api.NetworkClient
import com.myproject.playlistmaker.search.data.api.TracksSharedPrefStorage
import com.myproject.playlistmaker.search.data.network.TrackResponse
import com.myproject.playlistmaker.search.data.network.TracksSearchRequest
import com.myproject.playlistmaker.search.domain.api.SearchRepository
import com.myproject.playlistmaker.search.domain.model.Track
import com.myproject.playlistmaker.search.domain.model.SearchResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class SearchRepositoryImpl(
    private val tracksNetworkStorage: NetworkClient,
    private val tracksSharedPrefStorage: TracksSharedPrefStorage
) : SearchRepository {

    override fun getTracksByName(trackName: String): Flow<SearchResult<List<Track>>> = flow {
        val response = tracksNetworkStorage.doRequest(TracksSearchRequest(trackName))
         when (response.resultCode) {

            -1 -> {
                emit(SearchResult.Error(message = "Проверьте подключение к интернету", data = null))
            }
            200 -> {
                emit(SearchResult.Success(data = (response as TrackResponse).results, message = null))
            }
            else -> {
                emit(SearchResult.Error(message = "Ошибка сервера", data = null))
            }
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

    override fun clearHistory() {
        tracksSharedPrefStorage.clearHistory()
    }


}