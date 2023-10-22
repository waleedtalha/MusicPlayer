package com.app.musicplayer.ui.adapters.holders

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.R
import com.app.musicplayer.databinding.TrackItemBinding
import com.app.musicplayer.db.entities.RecentTrackEntity
import com.app.musicplayer.extentions.getThumbnailUri
import com.app.musicplayer.extentions.isUnknownString
import com.bumptech.glide.Glide

open class RecentTrackViewHolder(protected val binding: TrackItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindData(context: Context, recentTrack: RecentTrackEntity) {
        Glide.with(context).load(Uri.parse(recentTrack.albumId?.getThumbnailUri() ?: ""))
            .placeholder(R.drawable.ic_music)
            .into(binding.thumbnail)
        binding.trackName.text = recentTrack.title ?: ""
        binding.artistName.text = recentTrack.artist?.isUnknownString() ?: ""
    }
    fun setOnItemClick(onItemClickListener: View.OnClickListener) {
        binding.root.setOnClickListener(onItemClickListener)
    }
    fun setOnMenuClick(onMenuClickListener: View.OnClickListener) {
        binding.menuTrack.setOnClickListener(onMenuClickListener)
    }
}