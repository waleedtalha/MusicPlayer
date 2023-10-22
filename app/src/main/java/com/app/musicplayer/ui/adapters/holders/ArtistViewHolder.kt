package com.app.musicplayer.ui.adapters.holders

import android.content.Context
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.R
import com.app.musicplayer.databinding.ArtistItemBinding
import com.app.musicplayer.extentions.getThumbnailUri
import com.app.musicplayer.extentions.isUnknownString
import com.app.musicplayer.models.Artist
import com.bumptech.glide.Glide

open class ArtistViewHolder(protected val binding: ArtistItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindData(context: Context, artist: Artist) {
        Glide.with(context).load(Uri.parse(artist.albumId.toString().getThumbnailUri()))
            .placeholder(R.drawable.ic_artist)
            .into(binding.artistThumbnail)
        binding.artistTitle.text = artist.artistTitle?.isUnknownString() ?: ""
    }
    fun setOnItemClick(onItemClickListener: View.OnClickListener) {
        binding.root.setOnClickListener(onItemClickListener)
    }
}