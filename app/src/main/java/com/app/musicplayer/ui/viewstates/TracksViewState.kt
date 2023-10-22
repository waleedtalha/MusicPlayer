package com.app.musicplayer.ui.viewstates

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.musicplayer.core.utils.DataLiveEvent
import com.app.musicplayer.db.entities.PlaylistEntity
import com.app.musicplayer.db.entities.PlaylistSongCrossRef
import com.app.musicplayer.interator.livedata.TracksLiveData
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.models.TrackCombinedData
import com.app.musicplayer.models.Track
import com.app.musicplayer.db.entities.RecentTrackEntity
import com.app.musicplayer.repository.tracks.TracksRepository
import com.app.musicplayer.ui.list.ListViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class TracksViewState @Inject constructor(
    private val tracksRepository: TracksRepository,
    private val tracksInterator: TracksInteractor
) : ListViewState<Track>() {

    val showItemEvent = DataLiveEvent<TrackCombinedData>()
    val showMenuEvent = DataLiveEvent<TrackCombinedData>()
    private val tracksLiveData = tracksRepository.getTracks() as TracksLiveData
    override fun getItemsObservable(callback: (LiveData<List<Track>>) -> Unit) {
        callback.invoke(tracksLiveData)
    }

    override fun setOnItemClickListener(item: Track, position: Int) {
        super.setOnItemClickListener(item, position)
        showItemEvent.call(TrackCombinedData(item, position))
    }

    override fun setOnMenuClickListener(item: Track, position: Int, view: View) {
        super.setOnMenuClickListener(item, position, view)
        showMenuEvent.call(TrackCombinedData(item, position, view))
    }

    override fun onFilterChanged(filter: String?) {
        super.onFilterChanged(filter)
        tracksLiveData.filter = filter
    }

    fun getTrackList(trackList: (List<Track>) -> Unit) {
        tracksInterator.queryTrackList {
            trackList.invoke(it as List<Track>)
        }
    }

    fun insertNewPlaylist(playlist: PlaylistEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.insertNewPlaylist(playlist)
        }
    }

    suspend fun fetchPlaylists(): List<PlaylistEntity>? {
        return withContext(Dispatchers.IO) {
            val playList = tracksRepository.fetchPlaylists()
            playList.ifEmpty {
                null
            }
        }
    }

    fun insert(crossRef: PlaylistSongCrossRef) {
        return tracksRepository.insert(crossRef)
    }
    suspend fun fetchPlaylistSongCrossRef(playlistId: Long): List<Long>? {
        return withContext(Dispatchers.IO) {
            val trackList = tracksRepository.getSongIdsForPlaylist(playlistId)
            trackList.ifEmpty {
                null
            }
        }
    }
}