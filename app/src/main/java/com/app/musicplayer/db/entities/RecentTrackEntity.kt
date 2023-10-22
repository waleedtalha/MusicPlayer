package com.app.musicplayer.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecentTrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long?=null,
    val title: String? = null,
    val artist: String? = null,
    val duration: Long? = null,
    val path: String? = null,
    @ColumnInfo(name = "album_id")
    var albumId: String? = null,
    @ColumnInfo(name = "time_stamp")
    val timestamp: Long = System.currentTimeMillis()
) : java.io.Serializable