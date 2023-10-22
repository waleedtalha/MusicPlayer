package com.app.musicplayer.ui.viewstates

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.musicplayer.core.utils.DataLiveEvent
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.models.Track
import com.app.musicplayer.models.TrackCombinedData
import com.app.musicplayer.db.entities.RecentTrackEntity
import com.app.musicplayer.repository.tracks.TracksRepository
import com.app.musicplayer.ui.list.ListViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewState @Inject constructor(
    private val tracksRepository: TracksRepository,
    private val tracksInterator: TracksInteractor
) : ListViewState<Track>() {

    val showItemEvent = DataLiveEvent<TrackCombinedData>()
    val showFavoriteEvent = DataLiveEvent<Track>()
//    private val tracksLiveData = tracksRepository.getTracks() as TracksLiveData
    override fun getItemsObservable(callback: (LiveData<List<Track>>) -> Unit) {
    }

    override fun setOnItemClickListener(item: Track, position: Int) {
        super.setOnItemClickListener(item, position)
        showItemEvent.call(TrackCombinedData(item, position))
    }

    override fun setOnFavoriteClickListener(item: Track) {
        super.setOnFavoriteClickListener(item)
        showFavoriteEvent.call(item)
    }

    fun removeFavoriteTrack(trackId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.removeFavoriteTrack(trackId)
        }
    }

    fun fetchFavoriteTrackList(): LiveData<List<Track>> {
        return tracksRepository.fetchFavoriteTrack()
    }
}