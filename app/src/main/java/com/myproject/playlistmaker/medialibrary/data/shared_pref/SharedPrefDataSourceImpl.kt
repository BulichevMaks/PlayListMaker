package com.myproject.playlistmaker.medialibrary.data.shared_pref

import android.content.SharedPreferences
import com.google.gson.Gson
import com.myproject.playlistmaker.medialibrary.data.api.FavoriteSharedPrefDataSource
import com.myproject.playlistmaker.medialibrary.data.model.TrackDataSource


const val TRACK_KEY = "track_key"

class SharedPrefDataSourceImpl(
    private val sharedPreferences: SharedPreferences,
    private val jsonLab: Gson
): FavoriteSharedPrefDataSource {

    override fun save(track: TrackDataSource) {
        writeTrackToPref(sharedPreferences, track = track)
    }

    private fun writeTrackToPref(sharedPreferences: SharedPreferences, track: TrackDataSource) {
        val json = jsonLab.toJson(track)
        sharedPreferences.edit()
            .putString(TRACK_KEY, json)
            .apply()
    }
}