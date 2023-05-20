package com.myproject.playlistmaker.data.datasource.sharedpref

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.myproject.playlistmaker.data.datasource.api.TracksSharedPrefStorage
import com.myproject.playlistmaker.data.datasource.models.TrackDataSource


const val PREFERENCES = "preferences"
const val TRACK_KEY = "track_key"

class TracksSharedPrefDataSourceImpl(context: Context): TracksSharedPrefStorage {

    private val sharedPreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)

    override fun save(track: TrackDataSource) {
        writeToPref(sharedPreferences, track = track)
    }

    override fun get(): TrackDataSource {
        return readFromPref(sharedPreferences)
    }

    private fun writeToPref(sharedPreferences: SharedPreferences, track: TrackDataSource) {
        val json = Gson().toJson(track)
        sharedPreferences.edit()
            .putString(TRACK_KEY, json)
            .apply()
    }

    private fun readFromPref(sharedPreferences: SharedPreferences): TrackDataSource {
        val json = sharedPreferences.getString(TRACK_KEY, null)
        return Gson().fromJson(json, TrackDataSource::class.java)
    }
}