package com.app.musicplayer.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.musicplayer.db.dao.FavoritesDao
import com.app.musicplayer.db.dao.PlaylistDao
import com.app.musicplayer.db.dao.PlaylistSongCrossRefDao
import com.app.musicplayer.db.dao.RecentTrackDao
import com.app.musicplayer.db.entities.PlaylistEntity
import com.app.musicplayer.db.entities.PlaylistSongCrossRef
import com.app.musicplayer.models.Track
import com.app.musicplayer.db.entities.RecentTrackEntity
import com.app.musicplayer.utils.ROOM_DB_VERSION

@Database(entities = [Track::class, RecentTrackEntity::class, PlaylistEntity::class,PlaylistSongCrossRef::class], version = ROOM_DB_VERSION, autoMigrations =[AutoMigration(from = 22, to = 23)],exportSchema = true)
abstract class MusicDB : RoomDatabase() {
    abstract fun getTrackDao(): RecentTrackDao
    abstract fun getFavoriteDao(): FavoritesDao
    abstract fun getPlaylistDao(): PlaylistDao
    abstract fun getPlaylistSongCrossRefDao():PlaylistSongCrossRefDao
}