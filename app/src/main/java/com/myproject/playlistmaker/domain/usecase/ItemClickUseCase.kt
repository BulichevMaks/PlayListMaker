package com.myproject.playlistmaker.domain.usecase


import com.myproject.playlistmaker.domain.models.Track
import com.myproject.playlistmaker.domain.api.TrackRepository

class ItemClickUseCase(private val trackRepository: TrackRepository) {
    fun execute(
        tracks: ArrayList<Track>,
        historyTracks: ArrayList<Track>,
        position: Int
    ) {
        trackRepository.saveTrackToSharedPref(tracks[position])

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
    }
}