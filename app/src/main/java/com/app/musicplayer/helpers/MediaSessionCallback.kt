package com.app.musicplayer.helpers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.session.MediaSessionCompat
import com.app.musicplayer.extentions.sendIntent
import com.app.musicplayer.helpers.MediaPlayer.seekTo
import com.app.musicplayer.services.MusicService
import com.app.musicplayer.utils.PLAYPAUSE
import com.app.musicplayer.utils.SHUFFLE_TRACK_OFF

class MediaSessionCallback(private val service: MusicService, private val context: Context) :
    MediaSessionCompat.Callback() {

    override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
        return super.onMediaButtonEvent(mediaButtonEvent)
    }

    override fun onPlay() {
        context.sendIntent(PLAYPAUSE)
    }

    override fun onPause() {
        context.sendIntent(PLAYPAUSE)
    }

    override fun onSkipToNext() {
        service.handleNextPrevious(isNext = true, isShuffle = SHUFFLE_TRACK_OFF)
    }

    override fun onSkipToPrevious() {
        service.handleNextPrevious(isNext = false, isShuffle = SHUFFLE_TRACK_OFF)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onSeekTo(pos: Long) {
        seekTo(pos.toInt())
    }

    override fun onCustomAction(action: String?, extras: Bundle?) {
        super.onCustomAction(action, extras)
    }
}