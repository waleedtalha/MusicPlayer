package com.app.musicplayer.ui.adapters.holders

import android.content.Context
import android.view.View
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.R
import com.app.musicplayer.databinding.TrackItemBinding
import com.app.musicplayer.db.entities.PlaylistEntity
import com.app.musicplayer.extentions.beGone
import com.app.musicplayer.extentions.beInvisible
import com.app.musicplayer.extentions.beVisible
import com.bumptech.glide.Glide

open class PlaylistsViewHolder (protected val binding: TrackItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindData(context: Context, playlist: PlaylistEntity,songsCount:Int) {
        Glide.with(context).load("")
            .placeholder(R.drawable.ic_playlist)
            .into(binding.thumbnail)
        binding.trackName.text = playlist.playlistName
        binding.artistName.text = " $songsCount Songs"
    }
    fun showPlaylistNames() {
        binding.menuTrack.beInvisible()
        binding.selectTrack.beVisible()
    }
    fun setOnItemSelect(onItemSelectListener: View.OnClickListener) {
        binding.selectTrack.setOnClickListener(onItemSelectListener)
    }
    fun setOnMenuClick(onMenuClickListener: View.OnClickListener) {
        binding.menuTrack.setOnClickListener(onMenuClickListener)
    }
    fun setOnItemClick(onItemClickListener: View.OnClickListener) {
        binding.root.setOnClickListener(onItemClickListener)
    }
}