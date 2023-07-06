package com.myproject.playlistmaker.medialibrary.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.myproject.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.myproject.playlistmaker.medialibrary.domain.model.Track
import com.myproject.playlistmaker.medialibrary.ui.FavoriteState
import com.myproject.playlistmaker.medialibrary.viewmodel.FavoriteTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteTracksBinding
    private val vm: FavoriteTracksViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vm.observeState().observe(viewLifecycleOwner) {
            render(it)
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
        }
    }

    private fun showEmpty(message: String) {
        binding.placeholder.text = message
    }

    private fun showContent(tracks: List<Track>) {
        //TODO
    }

    companion object {
        fun newInstance() = FavoriteTracksFragment()
    }
}