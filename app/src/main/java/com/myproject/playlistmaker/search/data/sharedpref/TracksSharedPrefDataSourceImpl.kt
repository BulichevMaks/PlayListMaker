package com.myproject.playlistmaker.search.data.sharedpref

import android.content.SharedPreferences
import com.google.gson.Gson
import com.myproject.playlistmaker.search.data.api.TracksSharedPrefStorage
import com.myproject.playlistmaker.search.data.model.TrackDataSource
import com.myproject.playlistmaker.search.domain.madel.Track

const val LIST_KEY = "key_for_list"
const val TRACK_KEY = "track_key"

class TracksSharedPrefDataSourceImpl(
    private val sharedPreferences: SharedPreferences,
    private val jsonLab: Gson
    ): TracksSharedPrefStorage {

    override fun save(track: TrackDataSource) {
        writeTrackToPref(sharedPreferences, track = track)
    }

    override fun get(): TrackDataSource {
        return readFromPref(sharedPreferences)
    }

override fun getTracksFromPref(): ArrayList<Track>? {
    return readTracksFromPref(sharedPreferences)
}

    override fun writeTracksToPref(tracks: ArrayList<TrackDataSource>) {
        writeTracksToPref(sharedPreferences, tracks)
    }

    private fun writeTrackToPref(sharedPreferences: SharedPreferences, track: TrackDataSource) {
        val json = jsonLab.toJson(track)
        sharedPreferences.edit()
            .putString(TRACK_KEY, json)
            .apply()
    }

    private fun readFromPref(sharedPreferences: SharedPreferences): TrackDataSource {
        val json = sharedPreferences.getString(TRACK_KEY, null)
        return jsonLab.fromJson(json, TrackDataSource::class.java)
    }

private fun readTracksFromPref(sharedPreferences: SharedPreferences): ArrayList<Track>? {
    val json = sharedPreferences.getString(LIST_KEY, null) ?: return arrayListOf()
    return jsonLab.fromJson(
        json,
        Array<Track>::class.java
    )?.let { ArrayList(it.toList()) }
}

    private fun writeTracksToPref(sharedPreferences: SharedPreferences, tracks: ArrayList<TrackDataSource>) {
        val json = jsonLab.toJson(tracks)
        sharedPreferences.edit()
            .putString(LIST_KEY, json)
            .apply()
    }
}