package com.myproject.playlistmaker

import android.content.res.Configuration
import android.content.res.Resources
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private lateinit var url: String
    private var mainThreadHandler: Handler? = null
    private lateinit var imageAlbum: ImageView
    private lateinit var buttonBack: ImageView
    private lateinit var playButton: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
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
        playButton = findViewById(R.id.playButton)
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        trackTime = findViewById(R.id.trackTime)
        duration = findViewById(R.id.duration)
        collectionName = findViewById(R.id.collectionName)
        releaseDate = findViewById(R.id.releaseDate)
        primaryGenreName = findViewById(R.id.primaryGenreName)
        country = findViewById(R.id.country)
        mainThreadHandler = Handler(Looper.getMainLooper())

        val track = intent.getSerializableExtra(SEL_ITEM) as Track
        trackName.text = track.trackName
        artistName.text = track.artistName
        url = track.previewUrl
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
        preparePlayer()
        buttonBack.setOnClickListener {
            finish()
        }
        playButton.setOnClickListener {
            startTimer()
            playbackControl()
        }
    }
    override fun onPause() {
        super.onPause()
        pausePlayer()
    }
    override fun onDestroy() {
        super.onDestroy()
        playerState = STATE_PREPARED
        mediaPlayer.release()
    }
    private fun startTimer() {

        mainThreadHandler?.postDelayed(
            object : Runnable {
                override fun run() {

                    if (playerState == STATE_PLAYING) {
                        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)

                            mainThreadHandler?.postDelayed(
                                this,
                                REFRESH_LIST_DELAY_MILLIS,
                            )
                    }
                }
            },
            REFRESH_LIST_DELAY_MILLIS
        )
    }
    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            trackTime.text = "00:00"
            if(isThemeNight()) {
                playButton.setImageResource(R.drawable.ic_play_button_night)
            } else {
                playButton.setImageResource(R.drawable.ic_play_button)
            }
            playerState = STATE_PREPARED
        }
    }
    private fun startPlayer() {
        mediaPlayer.start()
        if(isThemeNight()) {
            playButton.setImageResource(R.drawable.ic_pause_button_night)
        } else {
            playButton.setImageResource(R.drawable.ic_pause_button)
        }
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        if(isThemeNight()) {
            playButton.setImageResource(R.drawable.ic_play_button_night)
        } else {
            playButton.setImageResource(R.drawable.ic_play_button)
        }
        playerState = STATE_PAUSED
    }
    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }
    private fun isThemeNight(): Boolean {
        return when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                false
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                true
            }
            else -> false
        }
    }
    companion object {
        private const val REFRESH_LIST_DELAY_MILLIS = 500L
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}
