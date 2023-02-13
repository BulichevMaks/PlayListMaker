package com.prakticum.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val url = Uri.parse("https://ya.ru")
        val intent = Intent(Intent.ACTION_VIEW, url)
        startActivity(intent)
    }
}