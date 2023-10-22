package com.app.musicplayer.interator.tracks

import android.content.ContentResolver
import com.app.musicplayer.interator.base.BaseInterator
import com.app.musicplayer.models.Track
import com.app.musicplayer.models.TrackCombinedData

interface TracksInteractor:BaseInterator<TracksInteractor.Listener> {
    interface Listener

    fun deleteTrack(trackId: Long)

    fun queryTrack(trackId: Long, callback: (Track?) -> Unit)
    fun queryTrackList(callback: (List<Track?>) -> Unit)
    fun renameTrack(track: TrackCombinedData, newTitle: String)
}