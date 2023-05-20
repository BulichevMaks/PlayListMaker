package com.myproject.playlistmaker.presentation.ui

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.creator.Creator
import com.myproject.playlistmaker.data.datasource.sharedpref.TracksSharedPrefDataSourceImpl
import com.myproject.playlistmaker.domain.models.Track
import com.myproject.playlistmaker.presentation.ui.SearchActivity.Companion.SEL_ITEM
import com.myproject.playlistmaker.presentation.ui.SearchActivity.Companion.SEL_ITEM_URL
import java.text.SimpleDateFormat
import java.util.*

class PlayerViewActivity : AppCompatActivity() {
    private val playerApi by lazy {
        Creator.getPlayer()
    }
    private val trackRepository by lazy {
        Creator.getTrackRepository()
    }
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

        val track = trackRepository.getTrackFromSharedPref()
      //  val track = intent.getSerializableExtra(SEL_ITEM) as Track
        trackName.text = track.trackName
        artistName.text = track.artistName
        url = track.previewUrl.toString()
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
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")!!)
            .placeholder(R.drawable.plaseholder_player)
            .error(R.drawable.plaseholder_player)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.padding_top_bottom)))
            .into(imageAlbum)

        playerApi.preparePlayer(track)

        buttonBack.setOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            mainThreadHandler?.postDelayed(
                object : Runnable {
                    override fun run() {

                        if (playerApi.getCurrentState() == 2) {
                            trackTime.text = SimpleDateFormat(
                                "mm:ss", Locale.getDefault()
                            ).format(
                                playerApi.getCurrentPosition()

                            )
                            mainThreadHandler?.postDelayed(
                                this,
                                REFRESH_LIST_DELAY_MILLIS,
                            )
                        }
                    }
                },
                REFRESH_LIST_DELAY_MILLIS
            )
            playerApi.playbackControl({startPlayer()}, {pausePlayer()})
        }
        playerApi.setOnCompletionListener {
            trackTime.text = "00:00"
            if (isThemeNight()) {
                playButton.setImageResource(R.drawable.ic_play_button_night)
            } else {
                playButton.setImageResource(R.drawable.ic_play_button)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        playerApi.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        playerApi.onDestroy()
    }


    private fun startPlayer() {
        if (isThemeNight()) {
            playButton.setImageResource(R.drawable.ic_pause_button_night)
        } else {
            playButton.setImageResource(R.drawable.ic_pause_button)
        }
    }

    private fun pausePlayer() {
        if (isThemeNight()) {
            playButton.setImageResource(R.drawable.ic_play_button_night)
        } else {
            playButton.setImageResource(R.drawable.ic_play_button)
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
    }
}
