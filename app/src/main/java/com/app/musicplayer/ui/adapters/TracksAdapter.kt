package com.app.musicplayer.ui.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.app.musicplayer.models.ListData
import com.app.musicplayer.models.Track
import com.app.musicplayer.ui.adapters.holders.TrackViewHolder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TracksAdapter @Inject constructor(@ApplicationContext private val context: Context) :
    ListAdapter<Track>() {
    override fun onBindListItem(holder: ViewHolder, item: Track) {
        (holder as TrackViewHolder).apply {
            bindData(context,item)
        }
    }

    override fun convertDataToListData(items: List<Track>) =
        ListData.fromTracks(items)

}