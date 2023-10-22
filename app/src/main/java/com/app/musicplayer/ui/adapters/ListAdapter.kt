package com.app.musicplayer.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding
import com.app.musicplayer.databinding.ArtistItemBinding
import com.app.musicplayer.databinding.ListItemBinding
import com.app.musicplayer.databinding.TrackItemBinding
import com.app.musicplayer.models.ListData
import com.app.musicplayer.ui.adapters.holders.AlbumViewHolder
import com.app.musicplayer.ui.adapters.holders.ArtistViewHolder
import com.app.musicplayer.ui.adapters.holders.FavoritesViewHolder
import com.app.musicplayer.ui.adapters.holders.ListItemHolder
import com.app.musicplayer.ui.adapters.holders.PlaylistsViewHolder
import com.app.musicplayer.ui.adapters.holders.RecentTrackViewHolder
import com.app.musicplayer.ui.adapters.holders.TrackViewHolder
import com.app.musicplayer.utils.*

abstract class ListAdapter<ItemType> : RecyclerView.Adapter<ViewHolder>() {

    private var _data: ListData<ItemType> = ListData()
    private var _onItemClickListener: (ItemType, Int) -> Unit = { _, _ -> }
    private var _onItemSelectListener: (ItemType) -> Unit = { _ -> }
    private var _onItemMenuClickListener: (ItemType, Int, View) -> Unit = { _, _, _ -> }
    private var _onItemFavoriteClickListener: (ItemType) -> Unit = {}
    private var setViewType: Int = ALL_TRACKS_VT
    var selected_position = -1
    var isChecked = false
    var items: List<ItemType>
        get() = _data.items
        set(value) {
            _data = convertDataToListData(value)
            notifyDataSetChanged()
        }
    var viewHolderType: Int
        get() = setViewType
        set(value) {
            setViewType = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: ViewBinding? = null
        when (setViewType) {
            ALL_TRACKS_VT -> {
                view = TrackItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return TrackViewHolder(view)
            }

            ALBUMS_VT -> {
                view = TrackItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return AlbumViewHolder(view)
            }

            ARTISTS_VT -> {
                view = ArtistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ArtistViewHolder(view)
            }

            RECENT_TRACK_VT -> {
                view = TrackItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return RecentTrackViewHolder(view)
            }

            FAVORITES_VT -> {
                view = TrackItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return FavoritesViewHolder(view)
            }
            PLAYLISTS_VT -> {
                view = TrackItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return PlaylistsViewHolder(view)
            }

            else -> return ListItemHolder(view as ListItemBinding)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataItem = getItem(position)
        when (setViewType) {
            ALL_TRACKS_VT -> {
                (holder as TrackViewHolder).apply {
                    setOnItemClick {
                        _onItemClickListener.invoke(dataItem, holder.adapterPosition)
                    }
                    setOnMenuClick {
                        _onItemMenuClickListener.invoke(dataItem, holder.adapterPosition, it)
                    }
                    onBindListItem(this, dataItem)
                }
            }

            ALBUMS_VT -> {
                (holder as AlbumViewHolder).apply {
                    setOnItemClick {
                        _onItemClickListener.invoke(dataItem, holder.adapterPosition)
                    }
                    onBindListItem(this, dataItem)
                }
            }

            ARTISTS_VT -> {
                (holder as ArtistViewHolder).apply {
                    setOnItemClick {
                        _onItemClickListener.invoke(dataItem, holder.adapterPosition)
                    }
                    onBindListItem(this, dataItem)
                }
            }

            RECENT_TRACK_VT -> {
                (holder as RecentTrackViewHolder).apply {
                    setOnItemClick {
                        _onItemClickListener.invoke(dataItem, holder.adapterPosition)
                    }
                    setOnMenuClick {
                        _onItemMenuClickListener.invoke(dataItem, holder.adapterPosition, it)
                    }
                    onBindListItem(this, dataItem)
                }
            }

            FAVORITES_VT -> {
                (holder as FavoritesViewHolder).apply {
                    setOnItemClick {
                        _onItemClickListener.invoke(dataItem, holder.adapterPosition)
                    }
                    setOnMenuClick {
                        _onItemFavoriteClickListener.invoke(dataItem)
                    }
                    onBindListItem(this, dataItem)
                }
            }
            PLAYLISTS_VT -> {
                (holder as PlaylistsViewHolder).apply {
                    setOnItemClick {
                        _onItemClickListener.invoke(dataItem, holder.adapterPosition)
                    }
                    setOnMenuClick {
                        _onItemMenuClickListener.invoke(dataItem, holder.adapterPosition, it)
                    }
                    setOnItemSelect{
                        isChecked = true
                        selected_position = holder.adapterPosition
                        _onItemSelectListener.invoke(dataItem)
                    }
                    onBindListItem(this, dataItem)
                }
            }
        }
    }

    override fun getItemCount(): Int = _data.items.size

    private fun getItem(position: Int) = _data.items[position]

    open fun convertDataToListData(items: List<ItemType>) = ListData(items)

    abstract fun onBindListItem(holder: ViewHolder, item: ItemType)

    fun setOnItemClickListener(onItemClickListener: (ItemType, Int) -> Unit) {
        _onItemClickListener = onItemClickListener
    }

    fun setOnMenuClickListener(onItemMenuClickListener: (ItemType, Int, View) -> Unit) {
        _onItemMenuClickListener = onItemMenuClickListener
    }

    fun setOnFavoriteClickListener(onItemFavoriteClickListener: (ItemType) -> Unit) {
        _onItemFavoriteClickListener = onItemFavoriteClickListener
    }
    fun setOnPlaylistSelectListener(onItemPlaylistSelectListener: (ItemType) -> Unit) {
        _onItemSelectListener = onItemPlaylistSelectListener
    }
}