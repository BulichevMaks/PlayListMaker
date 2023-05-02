package com.myproject.playlistmaker

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.internal.ViewUtils.dpToPx
import com.google.gson.Gson
import com.myproject.playlistmaker.SearchActivity.Companion.SEL_ITEM
import com.myproject.playlistmaker.SearchActivity.Companion.SEL_ITEM_URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class PlayerViewActivity : AppCompatActivity() {

    private lateinit var imageAlbum: ImageView
    private lateinit var buttonBack: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var duration: TextView
    private lateinit var collectionName: TextView
    private lateinit var releaseDate: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var country: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_view)

        imageAlbum = findViewById(R.id.imageAlbum)
        buttonBack = findViewById(R.id.buttonBack)
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        duration = findViewById(R.id.duration)
        collectionName = findViewById(R.id.collectionName)
        releaseDate = findViewById(R.id.releaseDate)
        primaryGenreName = findViewById(R.id.primaryGenreName)
        country = findViewById(R.id.country)

        val track = intent.getSerializableExtra(SEL_ITEM) as Track
        trackName.text = track.trackName
        artistName.text = track.artistName
        duration.text = track.trackTimeMillis?.let {
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.toLong())
        }.toString()
        collectionName.text = track.collectionName

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        val date = track.releaseDate.let { dateFormat.parse(it) }
        val calendar = Calendar.getInstance().apply {
            if (date != null) {
                time = date
            }
        }
        releaseDate.text = calendar.get(Calendar.YEAR).toString()
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country

        Glide.with(this)
            .load(intent.getStringExtra(SEL_ITEM_URL)?.replaceAfterLast('/',"512x512bb.jpg")!!)
            .placeholder(R.drawable.plaseholder_player)
            .error(R.drawable.plaseholder_player)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.padding_top_bottom)))
            .into(imageAlbum)

        buttonBack.setOnClickListener {
            finish()
        }
    }
}
