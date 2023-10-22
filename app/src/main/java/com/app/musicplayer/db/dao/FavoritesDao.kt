package com.app.musicplayer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.musicplayer.models.Track

@Dao
interface FavoritesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavoriteTrack(track: Track)

    @Query("select * from Track")
    fun fetchFavoriteList(): LiveData<List<Track>>

    @Query("select * from Track")
    fun fetchFavorites(): List<Track>

    @Query("delete from Track where id =:id")
    fun removeFavoriteTrack(id:Long)
}