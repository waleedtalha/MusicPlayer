package com.app.musicplayer.ui.adapters

import android.content.Context
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.R
import com.app.musicplayer.db.entities.PlaylistEntity
import com.app.musicplayer.interator.playlist.PlaylistInteractor
import com.app.musicplayer.models.ListData
import com.app.musicplayer.ui.adapters.holders.PlaylistsViewHolder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PlaylistsAdapter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val playlistInteractor: PlaylistInteractor) :
    ListAdapter<PlaylistEntity>() {
    var isListOnly: Boolean? = false

    override fun onBindListItem(holder: RecyclerView.ViewHolder, item: PlaylistEntity) {
        (holder as PlaylistsViewHolder).apply {
            playlistInteractor.getSongsCount(item.playlistId).observeForever {songIdsList->
                bindData(context, item,songIdsList.size)
            }
            if (selected_position == holder.adapterPosition) {
                holder.itemView.findViewById<RadioButton>(R.id.select_track).isChecked = true
            } else {
                if (isChecked) {
                    holder.itemView.findViewById<RadioButton>(R.id.select_track).isChecked =
                        false
                }
            }
            if (isListOnly == true) {
                showPlaylistNames()
            }
        }
    }

    override fun convertDataToListData(items: List<PlaylistEntity>) =
        ListData.fromPlaylists(items)

}