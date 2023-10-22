package com.app.musicplayer.ui.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.app.musicplayer.models.Album
import com.app.musicplayer.models.ListData
import com.app.musicplayer.ui.adapters.holders.AlbumViewHolder
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlbumsAdapter @Inject constructor(@ApplicationContext private val context: Context) :
    ListAdapter<Album>() {

    override fun onBindListItem(holder: ViewHolder, album: Album) {
        (holder as AlbumViewHolder).apply {
            bindData(context,album)
        }
    }

    override fun convertDataToListData(items: List<Album>) =
        ListData.fromAlbums(items)
}