package com.app.musicplayer.repository.tracks

import androidx.lifecycle.LiveData
import com.app.musicplayer.db.MusicDB
import com.app.musicplayer.db.entities.PlaylistEntity
import com.app.musicplayer.db.entities.PlaylistSongCrossRef
import com.app.musicplayer.di.factory.contentresolver.ContentResolverFactory
import com.app.musicplayer.di.factory.livedata.LiveDataFactory
import com.app.musicplayer.models.Track
import com.app.musicplayer.db.entities.RecentTrackEntity
import javax.inject.Inject

class TracksRepositoryImpl @Inject constructor(
    private val liveDataFactory: LiveDataFactory,
    private val contentResolverFactory: ContentResolverFactory,
    private val musicDB: MusicDB
) : TracksRepository {
    override fun getTracks(): LiveData<List<Track>> = liveDataFactory.getTracksLiveData()
    override fun getAlbumTracks(albumId: Long?) = liveDataFactory.getAlbumsTracksLiveData(albumId)
    override fun getArtistTracks(artistId:Long?) = liveDataFactory.getArtistsTracksLiveData(artistId)
    override fun insertRecentTrack(track: RecentTrackEntity) {
        musicDB.getTrackDao().insertRecentTrack(track)
        musicDB.getTrackDao().deleteExtraTracks()
    }

    override fun fetchRecentTrack(): LiveData<List<RecentTrackEntity>> {
        return musicDB.getTrackDao().fetchRecentTrackList()
    }
    override fun fetchRecentListOnly(): List<RecentTrackEntity> {
        return musicDB.getTrackDao().fetchRecentListOnly()
    }

    override fun insertFavoriteTrack(track: Track) {
        musicDB.getFavoriteDao().insertFavoriteTrack(track)
    }

    override fun removeFavoriteTrack(trackId: Long) {
        musicDB.getFavoriteDao().removeFavoriteTrack(trackId)
    }

    override fun removeRecentTrack(trackId: Long) {
        musicDB.getTrackDao().removeRecentTrack(trackId)
    }

    override fun fetchFavoriteTrack(): LiveData<List<Track>> {
        return musicDB.getFavoriteDao().fetchFavoriteList()
    }

    override fun fetchFavorites(): List<Track> {
        return musicDB.getFavoriteDao().fetchFavorites()
    }
    override fun insertNewPlaylist(playlist:PlaylistEntity) {
        return musicDB.getPlaylistDao().insertTrackToPlaylist(playlist)
    }
    override fun fetchPlaylistsLiveList(): LiveData<List<PlaylistEntity>> {
        return musicDB.getPlaylistDao().fetchPlaylistsLiveList()
    }
    override fun fetchPlaylists(): List<PlaylistEntity> {
        return musicDB.getPlaylistDao().fetchPlaylists()
    }

    override fun insert(crossRef: PlaylistSongCrossRef) {
        return musicDB.getPlaylistSongCrossRefDao().insert(crossRef)
    }

    override fun getSongIdsForPlaylist(playlistId: Long): List<Long> {
        return musicDB.getPlaylistSongCrossRefDao().getSongIdsForPlaylist(playlistId)
    }

    override fun getSongIdsForPlaylistLive(playlistId: Long): LiveData<List<Long>> {
        return musicDB.getPlaylistSongCrossRefDao().getSongIdsForPlaylistLive(playlistId)
    }

    override fun deletePlaylist(playlistId: Long) {
        return musicDB.getPlaylistDao().deletePlaylist(playlistId)
    }

    override fun removeSongFromPlaylist(playlistId: Long, songId: Long) {
        return musicDB.getPlaylistSongCrossRefDao().removeSongFromPlaylist(playlistId,songId)
    }
}