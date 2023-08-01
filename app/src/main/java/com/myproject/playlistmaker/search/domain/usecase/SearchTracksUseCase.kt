package com.myproject.playlistmaker.search.domain.usecase

import com.myproject.playlistmaker.search.domain.api.SearchRepository
import com.myproject.playlistmaker.search.domain.model.SearchResult
import com.myproject.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchTracksUseCase(
    private val searchRepository: SearchRepository
) {

    fun execute(input: String): Flow<Pair<List<Track>?, String?>> {
        return searchRepository.getTracksByName(input).map { result ->
            when(result) {
                is SearchResult.Success -> {
                    Pair(result.data, null)
                }
                is SearchResult.Error -> {
                    Pair(null, result.message)
                }
            }
        }

    }

}