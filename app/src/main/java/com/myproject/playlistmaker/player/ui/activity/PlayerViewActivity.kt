package com.myproject.playlistmaker.player.ui.activity

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.databinding.PlayerViewBinding
import com.myproject.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.myproject.playlistmaker.player.ui.viewmodel.PlayerViewModelFactory

class PlayerViewActivity : AppCompatActivity() {

    private lateinit var vm: PlayerViewModel
    private lateinit var binding: PlayerViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PlayerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vm = ViewModelProvider(this, PlayerViewModelFactory())[PlayerViewModel::class.java]

        val track = vm.getTrack()
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.duration.text = track.trackTimeMillis
        binding.collectionName.text = track.collectionName
        binding. releaseDate.text = track.releaseDate
        binding.primaryGenreName.text = track.primaryGenreName
        binding.country.text = track.country

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
                if(it) {
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
        vm.onDestroy()
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
