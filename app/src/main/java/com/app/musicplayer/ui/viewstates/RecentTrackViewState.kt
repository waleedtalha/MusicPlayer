package com.app.musicplayer.ui.viewstates

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.musicplayer.core.utils.DataLiveEvent
import com.app.musicplayer.db.entities.PlaylistEntity
import com.app.musicplayer.db.entities.PlaylistSongCrossRef
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.models.RecentTrackCombinedData
import com.app.musicplayer.db.entities.RecentTrackEntity
import com.app.musicplayer.repository.tracks.TracksRepository
import com.app.musicplayer.ui.list.ListViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RecentTrackViewState @Inject constructor(
    private val tracksRepository: TracksRepository,
    private val tracksInterator: TracksInteractor
) : ListViewState<RecentTrackEntity>() {

    val showItemEvent = DataLiveEvent<RecentTrackCombinedData>()
    val showMenuEvent = DataLiveEvent<RecentTrackCombinedData>()
    override fun getItemsObservable(callback: (LiveData<List<RecentTrackEntity>>) -> Unit) {
    }

    override fun setOnItemClickListener(item: RecentTrackEntity, position: Int) {
        super.setOnItemClickListener(item, position)
        showItemEvent.call(RecentTrackCombinedData(item, position))
    }

    override fun setOnMenuClickListener(item: RecentTrackEntity, position: Int, view: View) {
        super.setOnMenuClickListener(item, position, view)
        showMenuEvent.call(RecentTrackCombinedData(item,position,view))
    }
    fun fetchRecentTrackList(): LiveData<List<RecentTrackEntity>> {
        return tracksRepository.fetchRecentTrack()
    }
    suspend fun fetchRecentListOnly(): List<RecentTrackEntity>? {
        return withContext(Dispatchers.IO) {
            val trackList = tracksRepository.fetchRecentListOnly()
            trackList.ifEmpty {
                null
            }
        }
    }
    fun removeRecentTrack(trackId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.removeRecentTrack(trackId)
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

    fun insertNewPlaylist(playlist: PlaylistEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.insertNewPlaylist(playlist)
        }
    }

    fun insert(crossRef: PlaylistSongCrossRef) {
        return tracksRepository.insert(crossRef)
    }
}