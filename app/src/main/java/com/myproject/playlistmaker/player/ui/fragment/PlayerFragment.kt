package com.myproject.playlistmaker.player.ui.fragment

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.databinding.FragmentPlayerBinding
import com.myproject.playlistmaker.player.ui.viewmodel.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlayerFragment : Fragment() {

    private val vm: PlayerViewModel by viewModel()
    private lateinit var binding: FragmentPlayerBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
            findNavController().navigateUp()
        }

        vm.playerTimingLiveData.observe(viewLifecycleOwner) {
            binding.trackTime.text = it
        }

        vm.playerStateLiveData.observe(viewLifecycleOwner) {
            playButtonControl(it)
        }

        vm.favoriteButtonStateLiveData.observe(viewLifecycleOwner) {
            favoriteButtonStateControl(it)
        }

        binding.playButton.setOnClickListener {
            vm.playHandlerControl()
        }

        binding.favoriteButton.setOnClickListener {
            vm.favoriteButtonControl()
        }
    }

    private fun favoriteButtonStateControl(state: Boolean) {
        if (state) {
            if (isThemeNight()) {
                binding.favoriteButton.setImageResource(R.drawable.ic_favorite_button_enabled_night)
            } else {
                binding.favoriteButton.setImageResource(R.drawable.ic_favorite_button_enabled)
            }
        } else {
            if (isThemeNight()) {
                binding.favoriteButton.setImageResource(R.drawable.ic_favorite_button_night)
            } else {
                binding.favoriteButton.setImageResource(R.drawable.ic_favorite_button)
            }
        }
    }

    private fun playButtonControl(state: Boolean) {
        if (state) {
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