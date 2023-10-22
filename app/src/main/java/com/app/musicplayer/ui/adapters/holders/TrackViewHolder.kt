package com.app.musicplayer.ui.adapters.holders

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.R
import com.app.musicplayer.databinding.TrackItemBinding
import com.app.musicplayer.extentions.getThumbnailUri
import com.app.musicplayer.extentions.isUnknownString
import com.app.musicplayer.models.Track
import com.bumptech.glide.Glide

open class TrackViewHolder(protected val binding: TrackItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindData(context: Context, track: Track) {
        Glide.with(context).load(Uri.parse(track.albumId?.getThumbnailUri() ?: ""))
            .placeholder(R.drawable.ic_music)
            .into(binding.thumbnail)
        binding.trackName.text = track.title ?: ""
        binding.artistName.text = track.artist?.isUnknownString() ?: ""
    }
    fun setOnItemClick(onItemClickListener: View.OnClickListener) {
        binding.root.setOnClickListener(onItemClickListener)
    }
    fun setOnMenuClick(onMenuClickListener: View.OnClickListener) {
        binding.menuTrack.setOnClickListener(onMenuClickListener)
    }
}