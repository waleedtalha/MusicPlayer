package com.app.musicplayer.helpers

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.DeadObjectException
import android.util.Log
import com.app.musicplayer.utils.COMPLETE_CALLBACK
import com.app.musicplayer.utils.PLAY_SPEED_0_5x
import com.app.musicplayer.utils.PLAY_SPEED_0_75x
import com.app.musicplayer.utils.PLAY_SPEED_1_25x
import com.app.musicplayer.utils.PLAY_SPEED_1_5x
import com.app.musicplayer.utils.PLAY_SPEED_1x
import com.app.musicplayer.utils.PLAY_SPEED_2x
import com.app.musicplayer.utils.isMPlus
import java.io.File

object MediaPlayer :
    MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
    MediaPlayer.OnCompletionListener {
    private var player: MediaPlayer? = null

    private var trackCompleteCallback: (String) -> Unit = {}
    private var playPauseCallback: (Boolean) -> Unit = {}
    private var setUpTrackCallback: (Boolean) -> Unit = {}
    private fun initMediaPlayerIfNeeded() {
        if (player != null) {
            return
        }
        player = MediaPlayer()
    }

    fun mPlayer(): MediaPlayer? {
        return player
    }

    fun setupTrack(context: Context, path: String, playSpeed: Float,eqPitch:Float,setUpTrackCallback:(Boolean) -> Unit) {
        this.setUpTrackCallback = setUpTrackCallback
        initMediaPlayerIfNeeded()
        player?.reset() ?: return
        try {
            if (isMPlus()) {
                player?.apply {
                    setDataSource(context, Uri.fromFile(File(path)))
                    playbackParams = this.playbackParams.setSpeed(playSpeed)
                    playbackParams = this.playbackParams.setPitch(eqPitch)
                    prepare()
                    start()
                }
                setUpTrackCallback(true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCurrentPosition(): Int {
        return player?.currentPosition ?: 0
    }

    fun getTrackDuration(): Int? {
        return player?.duration
    }

    fun playTrack(playPauseCallback: (Boolean) -> Unit) {
        this.playPauseCallback = playPauseCallback
        initMediaPlayerIfNeeded()
        player?.start()
        playPauseCallback(true)
    }

    fun pauseTrackk(playPauseCallback: (Boolean) -> Unit) {
        this.playPauseCallback = playPauseCallback
        player?.pause()
        playPauseCallback(false)
    }

    fun isPlaying(): Boolean {
        return try {
            player?.isPlaying == true
        } catch (e: Exception) {
            false
        }
    }

    fun seekTo(position: Int) {
        if (isPlaying()) {
            player?.seekTo(position)
        }
    }

    fun abRepeat(position: Int) {
        if (isPlaying()) {
            player?.seekTo(position)
            player?.start()
        }
    }

    fun setPlayBackSpeed(speed: String) {
        when (speed) {
            PLAY_SPEED_0_5x -> setSpeedParam(0.5f)
            PLAY_SPEED_0_75x -> setSpeedParam(0.75f)
            PLAY_SPEED_1x -> setSpeedParam(1f)
            PLAY_SPEED_1_25x -> setSpeedParam(1.25f)
            PLAY_SPEED_1_5x -> setSpeedParam(1.5f)
            PLAY_SPEED_2x -> setSpeedParam(2f)
        }
    }

    private fun setSpeedParam(speed: Float) {
        if (isMPlus()) {
            player?.playbackParams = player?.playbackParams?.setSpeed(speed)!!
            player?.start()
        }
    }

    fun setEqualizerPitch(pitch:Float) {
        if (isMPlus()) {
            player?.playbackParams = player?.playbackParams?.setPitch(pitch)!!
            player?.start()
        }
    }
    fun completePlayer(trackCompleteCallback: (String) -> Unit) {
        this.trackCompleteCallback = trackCompleteCallback
        player?.setOnCompletionListener {
            trackCompleteCallback(COMPLETE_CALLBACK)
        }
    }

    fun releasePlayer() {
        player?.stop()
        player?.release()
        player = null
    }

    override fun onError(p0: MediaPlayer?, p1: Int, p2: Int): Boolean {
        player?.reset()
        return false
    }

    override fun onSeekComplete(p0: MediaPlayer?) {

    }

    override fun onCompletion(mediaPlayer: MediaPlayer?) {
    }

}