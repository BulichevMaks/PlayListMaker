package com.myproject.playlistmaker.player.data.datasource

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.myproject.playlistmaker.player.data.api.TracksSharedPrefStorage
import com.myproject.playlistmaker.player.data.model.TrackDataSource

const val LIST_KEY = "key_for_list"
const val PREFERENCES = "preferences"
const val TRACK_KEY = "track_key"

class PlayerSharedPrefDataSourceImpl(context: Context): TracksSharedPrefStorage {

    private val sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    override fun save(track: TrackDataSource) {
        writeTrackToPref(sharedPreferences, track = track)
    }

    override fun get(): TrackDataSource {
        return readFromPref(sharedPreferences)
    }

    override fun getTracksFromPref(): ArrayList<TrackDataSource>? {
        return readTracksFromPref(sharedPreferences)
    }

    override fun writeTracksToPref(tracks: ArrayList<TrackDataSource>) {
        writeTracksToPref(sharedPreferences, tracks)
    }

    private fun writeTrackToPref(sharedPreferences: SharedPreferences, track: TrackDataSource) {
        val json = Gson().toJson(track)
        sharedPreferences.edit()
            .putString(TRACK_KEY, json)
            .apply()
    }

    private fun readFromPref(sharedPreferences: SharedPreferences): TrackDataSource {
        val json = sharedPreferences.getString(TRACK_KEY, null)
        return Gson().fromJson(json, TrackDataSource::class.java)
    }

    private fun readTracksFromPref(sharedPreferences: SharedPreferences): ArrayList<TrackDataSource>? {
        val json = sharedPreferences.getString(LIST_KEY, null) ?: return arrayListOf()
        return Gson().fromJson(
            json,
            Array<TrackDataSource>::class.java
        )?.let { ArrayList(it.toList()) }
    }

    private fun writeTracksToPref(sharedPreferences: SharedPreferences, tracks: ArrayList<TrackDataSource>) {
        val json = Gson().toJson(tracks)
        sharedPreferences.edit()
            .putString(LIST_KEY, json)
            .apply()
    }
}