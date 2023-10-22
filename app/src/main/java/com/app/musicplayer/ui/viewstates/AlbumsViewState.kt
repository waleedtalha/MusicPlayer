package com.app.musicplayer.ui.viewstates

import androidx.lifecycle.LiveData
import com.app.musicplayer.core.utils.DataLiveEvent
import com.app.musicplayer.interator.livedata.AlbumsLiveData
import com.app.musicplayer.models.Album
import com.app.musicplayer.models.Track
import com.app.musicplayer.models.TrackCombinedData
import com.app.musicplayer.repository.albums.AlbumsRepository
import com.app.musicplayer.ui.list.ListViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlbumsViewState @Inject constructor(
    albumsRepository: AlbumsRepository
) : ListViewState<Album>() {

    private val albumsLiveData = albumsRepository.getAlbums() as AlbumsLiveData
    val showItemEvent = DataLiveEvent<Album>()

    override fun getItemsObservable(callback: (LiveData<List<Album>>) -> Unit) {
        callback.invoke(albumsLiveData)
    }
    override fun setOnItemClickListener(item: Album, position: Int) {
        super.setOnItemClickListener(item, position)
        showItemEvent.call(item)
    }

    override fun onFilterChanged(filter: String?) {
        super.onFilterChanged(filter)
        albumsLiveData.filter = filter
    }
}