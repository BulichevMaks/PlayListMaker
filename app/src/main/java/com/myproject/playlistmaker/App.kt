package com.myproject.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

const val PREFERENCES = "practicum_example_preferences"
const val EDIT_TEXT_KEY = "key_for_edit_text"

class App : Application() {

    var darkTheme = false
    override fun onCreate() {
        super.onCreate()
        instance = this@App

        val sharedPrefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        sharedPrefs.edit()
            .putBoolean(EDIT_TEXT_KEY, darkTheme)
            .apply()

        switchTheme(sharedPrefs.getBoolean(EDIT_TEXT_KEY, true))
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
    companion object {
        private lateinit var instance: App
        fun getInstance(): App {
            return instance
        }
    }
}