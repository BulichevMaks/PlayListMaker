package com.myproject.playlistmaker.search.data.network

import com.myproject.playlistmaker.search.domain.model.Track


class TrackResponse(val results: List<Track>): Response() {
}