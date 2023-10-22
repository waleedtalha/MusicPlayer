package com.app.musicplayer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.musicplayer.db.entities.PlaylistEntity

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTrackToPlaylist(playlist:PlaylistEntity)

    @Query("SELECT * from PlaylistEntity")
    fun fetchPlaylistsLiveList():LiveData<List<PlaylistEntity>>

    @Query("SELECT * from PlaylistEntity")
    fun fetchPlaylists():List<PlaylistEntity>

    @Query("DELETE from PlaylistEntity WHERE playlist_id=:playlistId")
    fun deletePlaylist(playlistId:Long)
}