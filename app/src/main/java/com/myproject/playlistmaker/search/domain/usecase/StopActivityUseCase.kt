package com.myproject.playlistmaker.search.domain.usecase

import com.myproject.playlistmaker.search.domain.api.SearchRepository
import com.myproject.playlistmaker.search.domain.madel.Track

class StopActivityUseCase(private val searchRepository: SearchRepository) {
    fun execute(tracks: ArrayList<Track>) {
        searchRepository.writeTracksToPref(tracks)
    }
}