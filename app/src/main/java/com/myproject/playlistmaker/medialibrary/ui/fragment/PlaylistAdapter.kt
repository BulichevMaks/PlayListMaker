package com.myproject.playlistmaker.medialibrary.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.medialibrary.domain.model.Playlist

class PlaylistAdapter(private val playlist: List<Playlist>, var itemClickListener: ((Int, Playlist) -> Unit)? = null): RecyclerView.Adapter<PlaylistViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_view,parent,false)
        return PlaylistViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlist.size
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlist[position])
        holder.itemView.setOnClickListener { itemClickListener?.invoke(position,playlist[position]) }
    }

}