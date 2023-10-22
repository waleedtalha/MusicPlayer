package com.app.musicplayer.extentions

import com.app.musicplayer.db.entities.RecentTrackEntity
import com.app.musicplayer.models.Track

fun RecentTrackEntity.toTrack(): Track {
    return Track(
        id = id,
        title = title,
        artist = artist,
        duration = duration,
        path = path,
        albumId = albumId
    )
}

fun Track.toRecentTrackEntity(): RecentTrackEntity {
    return RecentTrackEntity(
        id = id,
        title = title,
        artist = artist,
        duration = duration,
        path = path,
        albumId = albumId
    )
}