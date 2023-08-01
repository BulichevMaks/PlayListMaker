package com.myproject.playlistmaker.search.domain.usecase

import com.myproject.playlistmaker.search.domain.api.SearchRepository
import com.myproject.playlistmaker.search.domain.model.Track

class ItemHistoryClickUseCase(private val searchRepository: SearchRepository) {
    fun execute(tracks: ArrayList<Track>, position: Int) {
        searchRepository.saveTrackToSharedPref(tracks[position])
    }
}