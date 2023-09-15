package com.myproject.playlistmaker.medialibrary.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.databinding.FragmentPlaylistsBinding
import com.myproject.playlistmaker.medialibrary.domain.model.Playlist
import com.myproject.playlistmaker.medialibrary.ui.PlaylistState
import com.myproject.playlistmaker.medialibrary.viewmodel.PlaylistsViewModel
import java.util.ArrayList
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistsBinding
    private val vm: PlaylistsViewModel by viewModel()

    private var playlists: ArrayList<Playlist> = ArrayList()
    private var playlistsAdapter = PlaylistAdapter(playlists)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.getPlaylists()

        vm.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        binding.newPlaylistButton.setOnClickListener {
            startNewPlaylistFragment()
        }

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(),2)

            //  binding.recyclerView.addItemDecoration(GridItemDecoration(2,8))


    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Content -> showContent(state.playlists)
            is PlaylistState.Empty -> showEmpty(state.message)
        }
    }

    private fun showEmpty(message: String) {
        binding.placeholder.text = message
        binding.recyclerView.visibility = View.GONE
    }

    private fun showContent(playlists: List<Playlist>) {
        this.playlists.clear()
        this.playlists.addAll(playlists)
        playlistsAdapter.notifyDataSetChanged()
        binding.apply {

            placeholder.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            binding.recyclerView.adapter = playlistsAdapter
        }
    }

    private fun startNewPlaylistFragment() {
        findNavController().navigate(R.id.action_mediaLibraryFragment_to_addNewPlayListFragment)
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