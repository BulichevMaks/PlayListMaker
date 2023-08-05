package com.myproject.playlistmaker.search.domain.usecase

import com.myproject.playlistmaker.search.domain.api.SearchRepository
import com.myproject.playlistmaker.search.domain.model.Track

class ItemClickUseCase(private val searchRepository: SearchRepository) {
    fun execute(
        tracks: ArrayList<Track>,
        historyTracks: ArrayList<Track>,
        position: Int
    ) {

        searchRepository.saveTrackToSharedPref(tracks[position])

        if (!historyTracks.contains(tracks[position])) {
            if (historyTracks.size < 10) {
                historyTracks.add(tracks[position])
            } else {
                historyTracks.removeAt(0)
                historyTracks.add(tracks[position])
            }
        } else {
            historyTracks.remove(tracks[position])
            historyTracks.add(tracks[position])
        }

        searchRepository.writeTracksToPref(historyTracks)
    }
}