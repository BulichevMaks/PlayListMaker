package com.myproject.playlistmaker.domain.usecase

import com.myproject.playlistmaker.domain.api.TrackRepository
import com.myproject.playlistmaker.domain.models.Track

class ItemHistoryClickUseCase(private val trackRepository: TrackRepository) {
    fun execute(tracks: ArrayList<Track>, position: Int) {
        trackRepository.saveTrackToSharedPref(tracks[position])
    }
}