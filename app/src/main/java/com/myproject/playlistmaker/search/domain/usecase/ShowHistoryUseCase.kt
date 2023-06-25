package com.myproject.playlistmaker.search.domain.usecase

import com.myproject.playlistmaker.search.domain.api.SearchRepository
import com.myproject.playlistmaker.search.domain.madel.Track

class ShowHistoryUseCase(private val searchRepository: SearchRepository) {
    fun execute() : ArrayList<Track>? {
        return searchRepository.readTracksFromSharedPref()
    }
}