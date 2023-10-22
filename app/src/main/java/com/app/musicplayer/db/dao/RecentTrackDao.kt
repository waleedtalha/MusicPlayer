package com.app.musicplayer.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.musicplayer.db.entities.RecentTrackEntity

@Dao
interface RecentTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRecentTrack(track: RecentTrackEntity)
    @Query("select * from RecentTrackEntity")
    fun fetchRecentListOnly():List<RecentTrackEntity>
    @Query("select * from RecentTrackEntity")
    fun fetchRecentTrackList():LiveData<List<RecentTrackEntity>>
    @Query("delete from RecentTrackEntity where id =:id")
    fun removeRecentTrack(id:Long)
    @Query("DELETE FROM RecentTrackEntity WHERE id NOT IN (SELECT id FROM RecentTrackEntity ORDER BY time_stamp DESC LIMIT 5)")
    fun deleteExtraTracks()
}