package com.myproject.playlistmaker.domain.usecase

import com.myproject.playlistmaker.domain.models.TracksResult
import com.myproject.playlistmaker.domain.api.TrackRepository

class SearchTracksUseCase(private val trackRepository: TrackRepository) {
    fun execute(input: String, callback: (TracksResult) -> Unit) {
        trackRepository.getTracksByName(input) { tracksResult ->
            callback(tracksResult)
        }
    }
}