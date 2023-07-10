package com.myproject.playlistmaker.medialibrary.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.myproject.playlistmaker.databinding.FragmentPlaylistsBinding
import com.myproject.playlistmaker.medialibrary.domain.model.Playlist
import com.myproject.playlistmaker.medialibrary.ui.PlaylistState
import com.myproject.playlistmaker.medialibrary.viewmodel.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistsBinding
    private val vm: PlaylistsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Content -> showContent(state.playlists)
            is PlaylistState.Empty -> showEmpty(state.message)
        }
    }

    private fun showEmpty(message: String) {
        binding.placeholder.text = message
    }

    private fun showContent(playlists: List<Playlist>) {
        //TODO
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        fun newInstance() = PlaylistsFragment()
    }
}