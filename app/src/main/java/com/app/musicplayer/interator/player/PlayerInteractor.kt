package com.app.musicplayer.interator.player

import android.content.Context
import com.app.musicplayer.models.Track

interface PlayerInteractor {
    interface Listener

    fun deleteTrack(trackId: Long)
    fun queryTrackList(callback: (List<Track?>) -> Unit)
    fun setPhoneRingtone(context: Context, trackId: Long)
    fun setAlarmTone(context: Context, trackPath: String)
}