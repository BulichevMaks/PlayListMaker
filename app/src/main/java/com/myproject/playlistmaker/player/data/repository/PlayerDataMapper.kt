package com.myproject.playlistmaker.player.data.repository

import com.myproject.playlistmaker.player.data.model.TrackDataSource
import com.myproject.playlistmaker.player.domain.model.Track
import java.text.SimpleDateFormat
import java.util.*

object PlayerDataMapper {

    fun transformTrackDataSourceToDomainModels(track: TrackDataSource): Track {
        return Track(
            id = track.id,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = track.trackTimeMillis?.let {
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.toLong())
                }.toString(),
            artworkUrl100 = track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"),
            previewUrl = track.previewUrl.toString(),
            collectionName = track.collectionName,
            releaseDate = calendar(track = track) ,
            primaryGenreName = track.primaryGenreName,
            country = track.country
        )
    }
    private fun calendar(track: TrackDataSource): String? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        val date = track.releaseDate.let {
            if (it != null) {
                dateFormat.parse(it)
            }  else {
                return ""
            }
        }
        val calendar = Calendar.getInstance().apply {
            if (date != null) {
                time = date
            } else {
                return ""
            }
        }
        return calendar.get(Calendar.YEAR).toString()
    }
}