package com.myproject.playlistmaker.search.domain.usecase


import com.myproject.playlistmaker.search.domain.api.SearchRepository
import com.myproject.playlistmaker.search.domain.madel.SearchResult
import com.myproject.playlistmaker.search.domain.madel.Track
import java.util.concurrent.Executors

class SearchTracksUseCase(private val searchRepository: SearchRepository) {

    private val executor = Executors.newCachedThreadPool()

    fun execute(input: String, consumer: TracksConsumer) {
        executor.execute {
            when (val resource = searchRepository.getTracksByName(input)){
                is SearchResult.Success -> {consumer.consume(resource.data, null, resource.code)}
                is SearchResult.Error -> {consumer.consume(null, resource.message, resource.code)}
            }
        }
    }
    interface TracksConsumer {
        fun consume(tracks: List<Track>?, errorMessage: String?, code: Int)
    }
}