package com.app.musicplayer.ui.adapters.holders

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.R
import com.app.musicplayer.databinding.ListItemBinding
import com.app.musicplayer.extentions.beGone
import com.app.musicplayer.extentions.beInvisible
import com.app.musicplayer.extentions.beVisibleIf
import com.bumptech.glide.Glide

open class ListItemHolder(protected val binding: ListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    protected val context get() = itemView.context

//    var trackName: String?
//        get() = binding.trackName.text.toString()
//        set(value) {
//            binding.trackName.text = value
//        }
//
//    var artist: String?
//        get() = binding.artistName.text.toString()
//        set(value) {
//            binding.artistName.text = value
//        }
//
//    var artistTitle: String?
//        get() = binding.artistTitle.text.toString()
//        set(value) {
//            binding.artistTitle.text = value
//        }
//
//    fun isListItemShown(check: Boolean) {
//        binding.listContainer.beVisibleIf(check)
//    }
//
//    fun isFavoriteIconShown(check: Boolean) {
//        binding.favoriteTrack.beVisibleIf(check)
//        binding.menuTrack.beInvisible()
//    }
//
//    fun isArtistItemShown(check: Boolean) {
//        binding.artistContainer.beVisibleIf(check)
//    }
//
//    fun isMenuIconShown(check: Boolean) {
//        binding.menuTrack.beVisibleIf(check)
//    }
//
//    fun setAlbumThumbnail(albumUri: String) {
//        Glide.with(context).load(Uri.parse(albumUri)).placeholder(R.drawable.ic_album)
//            .into(binding.thumbnail)
//    }
//
//    fun setTrackThumbnail(trackUri: String) {
//        Glide.with(context).load(Uri.parse(trackUri)).placeholder(R.drawable.ic_music)
//            .into(binding.thumbnail)
//    }
//
//    fun setArtistThumbnail(artistUri: String) {
//        Glide.with(context).load(Uri.parse(artistUri)).placeholder(R.drawable.ic_artist)
//            .into(binding.artistThumbnail)
//    }
//
//    fun setOnItemClick(onItemClickListener: View.OnClickListener) {
//        itemView.setOnClickListener(onItemClickListener)
//    }
//    fun setOnTrackMenuClick(onItemClickListener: View.OnClickListener) {
//        binding.menuTrack.setOnClickListener(onItemClickListener)
//    }
//    fun setOnTrackFavoriteClick(onItemClickListener: View.OnClickListener) {
//        binding.favoriteTrack.setOnClickListener(onItemClickListener)
//    }
}