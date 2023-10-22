package com.app.musicplayer.ui.viewstates

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.app.musicplayer.db.entities.PlaylistEntity
import com.app.musicplayer.db.entities.PlaylistSongCrossRef
import com.app.musicplayer.interator.livedata.TracksLiveData
import com.app.musicplayer.interator.player.PlayerInteractor
import com.app.musicplayer.models.Track
import com.app.musicplayer.db.entities.RecentTrackEntity
import com.app.musicplayer.repository.tracks.TracksRepository
import com.app.musicplayer.ui.list.ListViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MusicPlayerViewState @Inject constructor(
    private val tracksRepository: TracksRepository,
    private val playerInteractor: PlayerInteractor
) : ListViewState<Track>() {

    private val tracksLiveData = tracksRepository.getTracks() as TracksLiveData
    override fun getItemsObservable(callback: (LiveData<List<Track>>) -> Unit) {
        callback.invoke(tracksLiveData)
    }

    fun insertRecentTrack(track: RecentTrackEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.insertRecentTrack(track)
        }
    }

    fun removeFavoriteTrack(trackId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.removeFavoriteTrack(trackId)
        }
    }

    fun insertFavoriteTrack(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.insertFavoriteTrack(track)
        }
    }

    fun setRingtone(context: Context, trackId: Long) {
        playerInteractor.setPhoneRingtone(context, trackId)
    }

    fun setAlarmTone(context: Context, trackPath: String) {
        playerInteractor.setAlarmTone(context, trackPath)
    }

    fun getAllTrackList(trackList:(List<Track>)->Unit) {
        playerInteractor.queryTrackList {
            trackList.invoke(it as List<Track>)
        }
    }
    fun fetchRecentTrackList(): LiveData<List<RecentTrackEntity>> {
        return tracksRepository.fetchRecentTrack()
    }

    fun getAlbumTracksOnlyList(albumId: Long):LiveData<List<Track>> {
        return tracksRepository.getAlbumTracks(albumId) as TracksLiveData
    }
    fun getArtistTracksOnlyList(artistId: Long):LiveData<List<Track>> {
        return tracksRepository.getArtistTracks(artistId) as TracksLiveData
    }

    suspend fun fetchFavorites(): List<Track>? {
        return withContext(Dispatchers.IO) {
            val trackList = tracksRepository.fetchFavorites()
            trackList.ifEmpty {
                null
            }
        }
    }
    suspend fun fetchSongIdsForPlaylist(playlistId: Long): List<Long>? {
        return withContext(Dispatchers.IO) {
            val trackList = tracksRepository.getSongIdsForPlaylist(playlistId)
            trackList.ifEmpty {
                null
            }
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