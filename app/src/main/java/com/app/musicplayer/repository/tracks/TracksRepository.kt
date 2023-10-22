package com.app.musicplayer.repository.tracks

import androidx.lifecycle.LiveData
import com.app.musicplayer.db.entities.PlaylistEntity
import com.app.musicplayer.db.entities.PlaylistSongCrossRef
import com.app.musicplayer.models.Track
import com.app.musicplayer.db.entities.RecentTrackEntity

interface TracksRepository {
    fun getTracks(): LiveData<List<Track>>
    fun getAlbumTracks(albumId: Long? = null): LiveData<List<Track>>
    fun getArtistTracks(artistId: Long? = null): LiveData<List<Track>>
    fun insertRecentTrack(track: RecentTrackEntity)
    fun fetchRecentTrack(): LiveData<List<RecentTrackEntity>>
    fun fetchRecentListOnly(): List<RecentTrackEntity>
    fun insertFavoriteTrack(track: Track)
    fun removeFavoriteTrack(trackId: Long)
    fun removeRecentTrack(trackId: Long)
    fun fetchFavoriteTrack(): LiveData<List<Track>>
    fun fetchFavorites(): List<Track>
    fun insertNewPlaylist(playlist: PlaylistEntity)
    fun fetchPlaylistsLiveList(): LiveData<List<PlaylistEntity>>
    fun fetchPlaylists(): List<PlaylistEntity>
    fun insert(crossRef:PlaylistSongCrossRef)
    fun getSongIdsForPlaylist(playlistId: Long):List<Long>
    fun getSongIdsForPlaylistLive(playlistId: Long):LiveData<List<Long>>
    fun deletePlaylist(playlistId: Long)
    fun removeSongFromPlaylist(playlistId: Long,songId:Long)
}