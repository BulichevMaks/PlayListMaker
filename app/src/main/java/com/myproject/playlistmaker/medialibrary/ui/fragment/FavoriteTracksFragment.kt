package com.myproject.playlistmaker.medialibrary.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.myproject.playlistmaker.medialibrary.domain.model.Track
import com.myproject.playlistmaker.medialibrary.ui.FavoriteState
import com.myproject.playlistmaker.medialibrary.viewmodel.FavoriteTracksViewModel
import java.util.ArrayList
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteTracksBinding
    private val vm: FavoriteTracksViewModel by viewModel()

    private var tracks: ArrayList<Track> = ArrayList()
    private var trackAdapter = TrackAdapter(tracks)
    private var isClickAllowed = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.getTracksFromDb()

        vm.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        trackAdapter.setOnItemClickListener { position ->
            vm.saveTrackToSharedPref(tracks, position)
            if (clickDebounce()) {
                startPlayer()
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }
    private fun render(state: FavoriteState) {
        when (state) {
            is FavoriteState.Content -> showContent(state.tracks)
            is FavoriteState.Empty -> showEmpty(state.message)
            is FavoriteState.Loading -> showLoading()
        }
    }

    private fun showEmpty(message: String) {
        this.tracks.clear()
        trackAdapter.notifyDataSetChanged()
        binding.apply {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.GONE
            placeholder.visibility = View.VISIBLE
            placeholder.text = message
        }
    }

    private fun showContent(tracks: List<Track>) {
        this.tracks.clear()
        this.tracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
        binding.apply {
            progressBar.visibility = View.GONE
            placeholder.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            recyclerView.layoutManager =
                LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.VERTICAL,
                    true
                )
            binding.recyclerView.adapter = trackAdapter
        }
    }

    private fun showLoading() {
        this.tracks.clear()
        trackAdapter.notifyDataSetChanged()
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun startPlayer() {
        findNavController().navigate(R.id.action_mediaLibraryFragment_to_playerFragment)
    }
    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            lifecycleScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}