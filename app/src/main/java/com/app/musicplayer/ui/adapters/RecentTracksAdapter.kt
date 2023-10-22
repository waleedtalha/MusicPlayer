package com.app.musicplayer.ui.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.app.musicplayer.models.ListData
import com.app.musicplayer.db.entities.RecentTrackEntity
import com.app.musicplayer.ui.adapters.holders.RecentTrackViewHolder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class RecentTracksAdapter @Inject constructor(@ApplicationContext private val context: Context):
    ListAdapter<RecentTrackEntity>() {
    override fun onBindListItem(holder: ViewHolder, item: RecentTrackEntity) {
        (holder as RecentTrackViewHolder).apply {
            bindData(context,item)
        }
    }

    override fun convertDataToListData(items: List<RecentTrackEntity>) =
        ListData.fromRecentTracks(items)

}