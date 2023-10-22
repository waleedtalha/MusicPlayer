package com.app.musicplayer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.musicplayer.db.entities.PlaylistSongCrossRef
import com.app.musicplayer.models.Track

@Dao
interface PlaylistSongCrossRefDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(crossRef: PlaylistSongCrossRef)

    @Query("SELECT songId from playlist_song_cross_ref WHERE playlistId = :playlistId")
    fun getSongIdsForPlaylist(playlistId: Long): List<Long>

    @Query("SELECT songId from playlist_song_cross_ref WHERE playlistId = :playlistId")
    fun getSongIdsForPlaylistLive(playlistId: Long): LiveData<List<Long>>

    @Query("DELETE FROM playlist_song_cross_ref WHERE playlistId=:playlistId AND songId=:songId")
    fun removeSongFromPlaylist(playlistId: Long, songId: Long)
}