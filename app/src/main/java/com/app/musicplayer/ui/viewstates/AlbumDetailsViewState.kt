package com.app.musicplayer.ui.viewstates

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.musicplayer.core.utils.DataLiveEvent
import com.app.musicplayer.db.entities.PlaylistEntity
import com.app.musicplayer.db.entities.PlaylistSongCrossRef
import com.app.musicplayer.interator.livedata.TracksLiveData
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.models.Track
import com.app.musicplayer.models.TrackCombinedData
import com.app.musicplayer.repository.tracks.TracksRepository
import com.app.musicplayer.ui.list.ListViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AlbumDetailsViewState @Inject constructor(
    private val tracksRepository: TracksRepository,
    private val tracksInterator: TracksInteractor
) : ListViewState<Track>() {

    var getAlbumLiveList: LiveData<List<Track>>? = null
    val showItemEvent = DataLiveEvent<TrackCombinedData>()
    val showMenuEvent = DataLiveEvent<TrackCombinedData>()
    override fun getItemsObservable(callback: (LiveData<List<Track>>) -> Unit) {
        getAlbumLiveList?.let {
            callback.invoke(it)
        }
    }

    fun queryAlbumDetails(id: Long) {
        getAlbumLiveList = tracksRepository.getAlbumTracks(id) as TracksLiveData
    }

    override fun setOnItemClickListener(item: Track, position: Int) {
        super.setOnItemClickListener(item, position)
        showItemEvent.call(TrackCombinedData(item, position))
    }

    override fun setOnMenuClickListener(item: Track, position: Int, view: View) {
        super.setOnMenuClickListener(item, position, view)
        showMenuEvent.call(TrackCombinedData(item, position, view))
    }

    suspend fun fetchPlaylists(): List<PlaylistEntity>? {
        return withContext(Dispatchers.IO) {
            val playList = tracksRepository.fetchPlaylists()
            playList.ifEmpty {
                null
            }
        }
    }

    fun insertNewPlaylist(playlist: PlaylistEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.insertNewPlaylist(playlist)
        }
    }

    fun insert(crossRef: PlaylistSongCrossRef) {
        return tracksRepository.insert(crossRef)
    }
}