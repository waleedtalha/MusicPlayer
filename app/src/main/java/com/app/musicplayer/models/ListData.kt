package com.app.musicplayer.models

import com.app.musicplayer.db.entities.PlaylistEntity
import com.app.musicplayer.db.entities.RecentTrackEntity

data class ListData<DataType>(
    var items: List<DataType> = arrayListOf()
) {
    companion object {
        fun fromPlaylists(
            tracks: List<PlaylistEntity>
        ): ListData<PlaylistEntity> {
            return ListData(tracks)
        }
        fun fromFavoriteTracks(
            tracks: List<Track>
        ): ListData<Track> {
            return ListData(tracks)
        }
        fun fromRecentTracks(
            tracks: List<RecentTrackEntity>
        ): ListData<RecentTrackEntity> {
            return ListData(tracks)
        }
        fun fromTracks(
            tracks: List<Track>
        ): ListData<Track> {
            return ListData(tracks)
        }

        fun fromAlbums(
            albums: List<Album>
        ): ListData<Album> {
            return ListData(albums)
        }
    }
}