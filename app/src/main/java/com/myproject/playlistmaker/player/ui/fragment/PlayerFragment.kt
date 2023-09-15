package com.myproject.playlistmaker.player.ui.fragment

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.databinding.FragmentPlayerBinding
import com.myproject.playlistmaker.medialibrary.domain.model.Playlist
import com.myproject.playlistmaker.medialibrary.ui.PlaylistState
import com.myproject.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.myproject.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlayerFragment : Fragment(), PlayerViewHolder.ClickListener {

    private val vm: PlayerViewModel by viewModel()
    private lateinit var binding: FragmentPlayerBinding
    private lateinit var adapter: PlayerAdapter
    private lateinit var track: Track

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.getAllPlayLists()

        adapter = PlayerAdapter(this)

        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        adapter.notifyDataSetChanged()
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })


        track = vm.getTrack()
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

        vm.statePlayListsLiveData.onEach { render(it) }
            .launchIn(viewLifecycleOwner.lifecycleScope)


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

        binding.addButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            adapter.notifyDataSetChanged()
        }
        binding.buttonAddNewOlayList.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_addNewPlayListFragment)
            val bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet).apply {
                state = BottomSheetBehavior.STATE_HIDDEN

            }
        }
    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Empty -> {
                binding.recyclerView.visibility = View.GONE
            }
            is PlaylistState.Content -> {
                binding.recyclerView.visibility = View.VISIBLE
                adapter.playLists = state.playlists as ArrayList<Playlist>
                adapter.notifyDataSetChanged()
            }
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

    override fun onResume() {
        super.onResume()
        val bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    override fun onClick(playlist: Playlist) {
        if (vm.clickDebounce()) {
            if (!vm.isInPlaylist(
                    playlist = playlist,
                    trackId = track.trackId
                )
            ) {
                vm.addToPlaylist(playlist = playlist, track = track)
                Toast.makeText(
                    requireContext().applicationContext,
                    "${getString(R.string.track_added_in)} ${playlist.name}",
                    Toast.LENGTH_SHORT
                )
                    .show()
                playlist.trackCount = playlist.tracks.size
                val bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet).apply {
                    state = BottomSheetBehavior.STATE_HIDDEN
                }
            } else {
                Toast.makeText(
                    requireContext().applicationContext,
                    "${getString(R.string.track_is_already_in)} ${playlist.name}",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

        adapter.notifyDataSetChanged()
    }
}