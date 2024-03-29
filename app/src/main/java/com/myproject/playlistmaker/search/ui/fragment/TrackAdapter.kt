package com.myproject.playlistmaker.search.ui.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myproject.playlistmaker.R
import com.myproject.playlistmaker.search.domain.model.Track


class TrackAdapter(var tracks: ArrayList<Track>) : RecyclerView.Adapter<TrackViewHolder>() {

    private var listener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])

        holder.itemView.setOnClickListener {
            listener?.invoke(position)
        }
    }
    fun setOnItemClickListener(listener: (Int) -> Unit) {
        this.listener = listener
    }
}