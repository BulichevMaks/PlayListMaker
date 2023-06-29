package com.myproject.playlistmaker.player.ui.activity

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.databinding.PlayerViewBinding
import com.myproject.playlistmaker.player.ui.viewmodel.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {

    private val vm: PlayerViewModel by viewModel()
    private lateinit var binding: PlayerViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PlayerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = vm.getTrack()
        binding.apply {
            trackName.text = track.trackName
            artistName.text = track.artistName
            duration.text = track.trackTimeMillis
            collectionName.text = track.collectionName
            releaseDate.text = track.releaseDate
            primaryGenreName.text = track.primaryGenreName
            country.text = track.country
        }

        Glide.with(this)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.plaseholder_player)
            .error(R.drawable.plaseholder_player)
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.padding_top_bottom)))
            .into(binding.imageAlbum)

        binding.buttonBack.setOnClickListener {
            finish()
        }
        vm.observeTimingLiveData().observe(this) {
            binding.trackTime.text = it
        }
        binding.playButton.setOnClickListener {
            vm.playHandlerControl()
            vm.observeStateLiveData().observe(this) {
                if (it) {
                    if (isThemeNight()) {
                        binding.playButton.setImageResource(R.drawable.ic_pause_button_night)
                    } else {
                        binding.playButton.setImageResource(R.drawable.ic_pause_button)
                    }
                } else {
                    if (isThemeNight()) {
                        binding.playButton.setImageResource(R.drawable.ic_play_button_night)
                    } else {
                        binding.playButton.setImageResource(R.drawable.ic_play_button)
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        vm.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.onCleared()

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
}
