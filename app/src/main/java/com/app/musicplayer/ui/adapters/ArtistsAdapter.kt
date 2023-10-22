package com.app.musicplayer.ui.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.app.musicplayer.models.Artist
import com.app.musicplayer.ui.adapters.holders.ArtistViewHolder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ArtistsAdapter @Inject constructor(@ApplicationContext private val context: Context) :
    ListAdapter<Artist>() {
    override fun onBindListItem(holder: ViewHolder, item: Artist) {
        (holder as ArtistViewHolder).apply {
            bindData(context,item)
        }
    }
}