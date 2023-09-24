package com.myproject.playlistmaker.player.ui.fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.medialibrary.domain.model.Playlist

class PlayerAdapter(
    private val clickListener: PlayerViewHolder.ClickListener,
) :
    RecyclerView.Adapter<PlayerViewHolder>() {

    var playLists = ArrayList<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bottom_sheet_playlists, parent, false)
        return PlayerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playLists.size
    }

    override fun onBindViewHolder(holder: PlayerViewHolder, position: Int) {
        holder.bind(playLists[position], clickListener)
    }
}

class PlayerViewHolder(item: View) : RecyclerView.ViewHolder(item) {

    private val playlistCover: ImageView = item.findViewById(R.id.playlistCover)
    private val titlePlayList: TextView = item.findViewById(R.id.titlePlayList)
    private val trackCount: TextView = item.findViewById(R.id.trackCount)

    fun bind(model: Playlist, clickListener: ClickListener) {

        titlePlayList.text = model.name
        trackCount.text = setTracksCount(model.tracks.count())
        val imageUri = model.image

        Glide.with(itemView)
            .load(imageUri)
            .placeholder(R.drawable.ic_playlist_playsholder)
            .into(playlistCover)
        itemView.setOnClickListener {
            clickListener.onClick(model)
        }
    }

    private fun setTracksCount(count: Int): String {
        val tracksWord: String = when {
            count % 100 in 11..19 -> "треков"
            count % 10 == 1 -> "трек"
            count % 10 in 2..4 -> "трека"
            else -> "треков"
        }
        return "$count $tracksWord"
    }

    fun interface ClickListener {
        fun onClick(playlistModel: Playlist)
    }
}







