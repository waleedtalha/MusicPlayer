package com.app.musicplayer.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Track(
    @PrimaryKey(autoGenerate = true)
    val id: Long?=null,
    val title: String? = null,
    val artist: String? = null,
    val duration: Long? = null,
    val path: String? = null,
    @ColumnInfo(name = "album_id")
    var albumId: String? = null
) : java.io.Serializable
