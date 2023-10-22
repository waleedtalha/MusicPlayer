package com.app.musicplayer.ui.adapters.holders

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.R
import com.app.musicplayer.databinding.TrackItemBinding
import com.app.musicplayer.extentions.beGone
import com.app.musicplayer.extentions.getThumbnailUri
import com.app.musicplayer.extentions.isUnknownString
import com.app.musicplayer.models.Album
import com.bumptech.glide.Glide

open class AlbumViewHolder(protected val binding: TrackItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindData(context: Context, album: Album) {
        Glide.with(context).load(Uri.parse(album.albumId.toString().getThumbnailUri()))
            .placeholder(R.drawable.ic_album)
            .into(binding.thumbnail)
        binding.trackName.text = album.albumTitle ?: ""
        binding.artistName.text = album.artist?.isUnknownString() ?: ""
        binding.menuTrack.beGone()
    }
    fun setOnItemClick(onItemClickListener: View.OnClickListener) {
        binding.root.setOnClickListener(onItemClickListener)
    }
}