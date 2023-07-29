package com.myproject.playlistmaker.medialibrary.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.databinding.FragmentMediaLibraryBinding
import com.myproject.playlistmaker.medialibrary.ui.ViewPagerAdapter


class MediaLibraryFragment : Fragment() {

    private lateinit var binding: FragmentMediaLibraryBinding
    private lateinit var tabsMediator: TabLayoutMediator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentMediaLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = ViewPagerAdapter(
            fragmentManager = childFragmentManager,
            lifecycle = lifecycle,
        )

        tabsMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = resources.getString(R.string.favorite_tracks)
                1 -> tab.text = resources.getString(R.string.playlists)
            }
        }
        tabsMediator.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tabsMediator.detach()
    }

}