package com.myproject.playlistmaker.medialibrary.ui.fragment

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.medialibrary.domain.model.Playlist

class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val title: TextView = itemView.findViewById(R.id.namePlaylist)
    private val count: TextView = itemView.findViewById(R.id.count)
    private val img: ImageView = itemView.findViewById(R.id.playlistImage)
    private val placeholder: ImageView = itemView.findViewById(R.id.placeholder)

    fun bind(playlist: Playlist) {
        title.text = playlist.name
        count.text = setTextCount(playlist.tracks.count())

        if (playlist.image != null) {
            Glide.with(img).load(playlist.image)
                .transform(RoundedCorners(15))
                .into(img)
            placeholder.visibility = View.GONE
            img.visibility = View.VISIBLE
        } else {
            img.visibility = View.GONE
            placeholder.visibility = View.VISIBLE
        }
    }
    private fun setTextCount(count: Int): String {
        val tracks: String = when {
            count % 100 in 11..19 -> "треков"
            count % 10 == 1 -> "трек"
            count % 10 in 2..4 -> "трека"
            else -> "треков"
        }
        return "$count $tracks"
    }
}