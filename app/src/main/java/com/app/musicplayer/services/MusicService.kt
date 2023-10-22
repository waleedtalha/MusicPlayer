package com.app.musicplayer.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.media.session.MediaButtonReceiver
import com.app.musicplayer.R
import com.app.musicplayer.extentions.getColoredBitmap
import com.app.musicplayer.extentions.getThumbnailUri
import com.app.musicplayer.extentions.hasPermission
import com.app.musicplayer.extentions.isUnknownString
import com.app.musicplayer.extentions.playBackSpeed
import com.app.musicplayer.extentions.shuffleTrack
import com.app.musicplayer.extentions.toast
import com.app.musicplayer.helpers.MediaPlayer.abRepeat
import com.app.musicplayer.helpers.MediaPlayer.completePlayer
import com.app.musicplayer.helpers.MediaPlayer.getCurrentPosition
import com.app.musicplayer.helpers.MediaPlayer.getTrackDuration
import com.app.musicplayer.helpers.MediaPlayer.isPlaying
import com.app.musicplayer.helpers.MediaPlayer.mPlayer
import com.app.musicplayer.helpers.MediaPlayer.pauseTrackk
import com.app.musicplayer.helpers.MediaPlayer.playTrack
import com.app.musicplayer.helpers.MediaPlayer.seekTo
import com.app.musicplayer.helpers.MediaPlayer.setupTrack
import com.app.musicplayer.helpers.MediaSessionCallback
import com.app.musicplayer.helpers.NotificationHelper
import com.app.musicplayer.helpers.NotificationHelper.Companion.NOTIFICATION_ID
import com.app.musicplayer.helpers.OreoAudioFocusHandler
import com.app.musicplayer.helpers.PreferenceHelper
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.models.Track
import com.app.musicplayer.ui.activities.MusicPlayerActivity.Companion.aPosition
import com.app.musicplayer.ui.activities.MusicPlayerActivity.Companion.bPosition
import com.app.musicplayer.utils.*
import com.app.musicplayer.utils.getPermissionToRequest
import com.app.musicplayer.utils.isQPlus
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ScheduledExecutorService
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : Service(), OnAudioFocusChangeListener {

    @Inject
    lateinit var tracksInteractor: TracksInteractor

    @Inject
    lateinit var pref: PreferenceHelper

    var timer: ScheduledExecutorService? = null
    private var isPausedByTransientLossOfFocus = false
    private var mProgressHandler = Handler()
    private val PROGRESS_UPDATE_INTERVAL = 1000L
    private var mPlaybackSpeed = 1f
    private var mOreoFocusHandler: OreoAudioFocusHandler? = null
    private var mAudioManager: AudioManager? = null
    val intentControl = Intent(PROGRESS_CONTROLS_ACTION)
    private val mMediaSessionActions =
        PlaybackStateCompat.ACTION_STOP or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
                PlaybackStateCompat.ACTION_SEEK_TO or
                PlaybackStateCompat.ACTION_PLAY_PAUSE

    companion object {
        private var mCurrTrackCover: Bitmap? = null
        private var mMediaSession: MediaSessionCompat? = null
        var tracksList = ArrayList<Track>()
        var positionTrack: Int = 0
    }

    //    private var currentTrackId: Long = 0L
    private val notificationHandler = Handler()
    private var notificationHelper: NotificationHelper? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate() {
        super.onCreate()
        createMediaSession()

        notificationHelper = NotificationHelper.createInstance(context = this, mMediaSession!!)
        startForegroundAndNotify()

        mAudioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (isOreoPlus()) {
            mOreoFocusHandler = OreoAudioFocusHandler(app = application)
        }
    }

    private fun createMediaSession() {
        mMediaSession = MediaSessionCompat(this, "MusicService")
        mMediaSession!!.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS)
        val mediaSessionCallback = MediaSessionCallback(this, context = applicationContext)
        mMediaSession!!.setCallback(mediaSessionCallback)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isQPlus() && !hasPermission(getPermissionToRequest())) {
            return START_NOT_STICKY
        }

        val action = intent?.action
        when (action) {
            INIT -> handleInit(intent)
            PLAYPAUSE -> handlePlayPause()
            NEXT -> handleNextPrevious(isNext = true, isShuffle = SHUFFLE_TRACK_OFF)
            PREVIOUS -> handleNextPrevious(isNext = false, isShuffle = SHUFFLE_TRACK_OFF)
            SET_PROGRESS -> handleSetProgress(intent)
            DISMISS -> dismissNotification()
        }
        if (tracksList.size > positionTrack && tracksList.size != positionTrack) {
            //check if index is in the list
            pref.currentTrackId = tracksList[positionTrack].id ?: 0L
            pref.currentTrackPosition = positionTrack
        }

        MediaButtonReceiver.handleIntent(mMediaSession!!, intent)
        if (action != DISMISS && action != FINISH) {
            startForegroundAndNotify()
        }
        return START_NOT_STICKY
    }

    private fun dismissNotification() {
        if (isPlaying()) {
            pauseTrackk {
                if (!it) {
                    abandonAudioFocus()
                }
            }
        }
        stopForegroundAndNotification()
        val dismissIntent = Intent(DISMISS_PLAYER_ACTION)
        dismissIntent.putExtra(DISMISS_PLAYER, true)
        sendBroadcast(dismissIntent)
    }

    private fun handleSetProgress(intent: Intent) {
        seekTo(intent.getIntExtra(PROGRESS, getCurrentPosition()))
    }

    fun handleNextPrevious(isNext: Boolean, isShuffle: String) {
        if (isNext && positionTrack != tracksList.size.minus(1)) {
            positionTrack++
        } else if (!isNext && positionTrack != 0) {
            positionTrack--
        } else if (isShuffle == SHUFFLE_TRACK_ON) {
            positionTrack = tracksList.size.shuffleTrack()
        }
        if (tracksList.size <= positionTrack) return
        pref.currentTrackId = tracksList[positionTrack].id ?: 0L
        val nextPreviousIntent = Intent(NEXT_PREVIOUS_ACTION)
        nextPreviousIntent.putExtra(NEXT_PREVIOUS_TRACK_ID, pref.currentTrackId)
        sendBroadcast(nextPreviousIntent)
        setupTrack(
            applicationContext,
            tracksList[positionTrack].path ?: "",
            pref.setPlaySpeed?.playBackSpeed() ?: 1f,
            pref.setEqPitch?.toFloat() ?: 1f
        ) { if (it) requestAudioFocus() }
        handleProgressHandler(isPlaying())
        updateMediaSession()
        updateMediaSessionState()
    }

    private fun handlePlayPause() {
        if (isPlaying()) {
            pauseTrack()
        } else {
            resumeTrack()
        }
        updateMediaSessionState()
    }

    private fun pauseTrack() {
        pauseTrackk {
            if (!it) {
                val playPauseIntent = Intent(PLAY_PAUSE_ACTION)
                playPauseIntent.putExtra(PLAY_PAUSE_ICON, false)
                sendBroadcast(playPauseIntent)
//                abandonAudioFocus()
            }
        }
    }

    private fun resumeTrack() {
        playTrack {
            if (it) {
                val playPauseIntent = Intent(PLAY_PAUSE_ACTION)
                playPauseIntent.putExtra(PLAY_PAUSE_ICON, true)
                sendBroadcast(playPauseIntent)
                requestAudioFocus()
            }
        }
    }

    private fun handleInit(intent: Intent) {
        positionTrack = intent.getIntExtra(POSITION, 0)
        pref.currentTrackId = tracksList[positionTrack].id ?: 0L
        tracksList.forEach { track ->
            if (track.id == intent.getLongExtra(TRACK_ID_SERVICE, 0L)) {
                val pos = tracksList.indexOf(track)
                positionTrack = pos
            }
        }
        setupTrack(
            applicationContext,
            tracksList[positionTrack].path ?: "",
            pref.setPlaySpeed?.playBackSpeed() ?: 1f,
            pref.setEqPitch?.toFloat() ?: 1f
        ) { if (it) requestAudioFocus() }
        handleProgressHandler(isPlaying())
        updateMediaSession()
        updateMediaSessionState()
        completePlayer { completed ->
            if (completed == COMPLETE_CALLBACK) {
                if (pref.repeatTrack == REPEAT_TRACK_ON) {
                    setupTrack(
                        applicationContext,
                        tracksList[positionTrack].path ?: "",
                        pref.setPlaySpeed?.playBackSpeed() ?: 1f,
                        pref.setEqPitch?.toFloat() ?: 1f
                    ) { if (it) requestAudioFocus() }
                } else if (pref.shuffleTrack == SHUFFLE_TRACK_ON) {
                    handleNextPrevious(isNext = false, isShuffle = SHUFFLE_TRACK_ON)
                    startForegroundAndNotify()
                } else if (positionTrack == tracksList.size.minus(1)) {
                    dismissNotification()
                } else {
                    handleNextPrevious(isNext = true, isShuffle = SHUFFLE_TRACK_OFF)
                    startForegroundAndNotify()
                }
            }

        }
    }

    private fun startForegroundAndNotify() {
        notificationHandler.removeCallbacksAndMessages(null)
        notificationHandler.postDelayed({
            mCurrTrackCover = resources.getColoredBitmap(R.drawable.ic_music, R.color.purple)
            tracksInteractor.queryTrack(pref.currentTrackId ?: 0L) { track ->
                notificationHelper?.createPlayerNotification(
                    trackTitle = track?.title ?: "Track",
                    trackArtist = track?.artist?.isUnknownString() ?: "Artist",
                    isPlaying = isPlaying(),
                    largeIcon = mCurrTrackCover,
                ) {
                    notificationHelper?.notify(NOTIFICATION_ID, it)
                    try {
                        if (isQPlus()) {
                            startForeground(
                                NOTIFICATION_ID,
                                it,
                                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                            )
                        } else {
                            startForeground(NOTIFICATION_ID, it)
                        }
                    } catch (ignored: IllegalStateException) {
                    }
                }
            }
        }, 200L)
    }

    private fun handleProgressHandler(isPlaying: Boolean) {
        if (isPlaying) {
            mProgressHandler.post(object : Runnable {
                override fun run() {
                    if (isPlaying()) {
                        val position = getCurrentPosition()
                        val duration = getTrackDuration()
                        intentControl.putExtra(GET_TRACK_DURATION, duration)
                        intentControl.putExtra(GET_CURRENT_POSITION, position)
                        sendBroadcast(intentControl)
                        pref.currentTrackTotalDuration = duration
                        pref.currentTrackProgress = position
                        updateMediaSessionState()
                    }
                    mProgressHandler.removeCallbacksAndMessages(null)
                    mProgressHandler.postDelayed(
                        this,
                        (PROGRESS_UPDATE_INTERVAL / mPlaybackSpeed).toLong()
                    )
                    if (bPosition != 0 && mPlayer()?.currentPosition!! >= bPosition) {
                        abRepeat(aPosition)
                    }
                }
            })
        } else {
            mProgressHandler.removeCallbacksAndMessages(null)
        }
//        if (isPlaying) {
//            timer = Executors.newScheduledThreadPool(1)
//            timer?.scheduleAtFixedRate({
//                if (isPlaying()) {
//                    val position = getCurrentPosition()
//                    val duration = getTrackDuration()
//                    intentControl.putExtra(GET_TRACK_DURATION, duration)
//                    intentControl.putExtra(GET_CURRENT_POSITION, position)
//                    sendBroadcast(intentControl)
//
//                    pref.currentTrackTotalDuration = duration
//                    pref.currentTrackProgress = position
//                }
//            }, 10, 10, TimeUnit.MILLISECONDS)
//        }
    }

    private fun updateMediaSession() {
        val metadata = MediaMetadataCompat.Builder()
            .putString(
                MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                tracksList[positionTrack].albumId?.getThumbnailUri() ?: ""
            )
            .putString(
                MediaMetadataCompat.METADATA_KEY_ARTIST,
                tracksList[positionTrack].artist?.isUnknownString() ?: ""
            )
            .putString(
                MediaMetadataCompat.METADATA_KEY_TITLE,
                tracksList[positionTrack].title?.substringBeforeLast(".") ?: ""
            )
            .putString(
                MediaMetadataCompat.METADATA_KEY_MEDIA_ID,
                tracksList[positionTrack].id?.toString()
            )
            .putLong(
                MediaMetadataCompat.METADATA_KEY_DURATION,
                tracksList[positionTrack].duration ?: 0L
            )
            .putLong(
                MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER,
                tracksList.indexOf(tracksList[positionTrack]).toLong() + 1
            )
            .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, tracksList.size.toLong())
            .build()
        mMediaSession?.setMetadata(metadata)
    }

    fun updateMediaSessionState() {
        val builder = PlaybackStateCompat.Builder()
        val playbackState = if (isPlaying()) {
            PlaybackStateCompat.STATE_PLAYING
        } else {
            PlaybackStateCompat.STATE_PAUSED
        }

        val dismissAction = PlaybackStateCompat.CustomAction.Builder(
            DISMISS,
            getString(R.string.dismiss),
            R.drawable.ic_cross
        ).build()

        builder
            .setActions(mMediaSessionActions)
            .setState(
                playbackState,
                getCurrentPosition().toLong(),
                pref.setPlaySpeed?.playBackSpeed() ?: 1f
            )
            .addCustomAction(dismissAction)
        try {
            mMediaSession?.setPlaybackState(builder.build())
        } catch (ignored: Exception) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mMediaSession?.isActive = false
        mMediaSession = null
        stopForeground(true)
        stopSelf()
        abandonAudioFocus()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun stopForegroundAndNotification() {
        notificationHandler.removeCallbacksAndMessages(null)
        stopForeground(true)
        notificationHelper?.cancel(NOTIFICATION_ID)
    }

    private fun requestAudioFocus() {
        if (isOreoPlus()) {
            mOreoFocusHandler?.requestAudioFocus(this)
        } else {
            mAudioManager?.requestAudioFocus(
                this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
    }

    private fun abandonAudioFocus() {
        if (isOreoPlus()) {
            mOreoFocusHandler?.abandonAudioFocus()
        } else {
            mAudioManager?.abandonAudioFocus(this)
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                // Regain audio focus, resume playback
                if (!isPlaying() && isPausedByTransientLossOfFocus) {
                    resumeTrack()
                    startForegroundAndNotify()
                    updateMediaSession()
                    updateMediaSessionState()
                    isPausedByTransientLossOfFocus = false
                }
                // Restore the volume to normal
                mPlayer()?.setVolume(1.0f, 1.0f)
            }

            AudioManager.AUDIOFOCUS_LOSS -> {
                // Permanent loss of audio focus, stop playback and release resources
                mPlayer()?.stop()
                mPlayer()?.release()
                // You may also choose to stop the service here if desired
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                // Temporary loss of audio focus, pause playback
                val wasPlaying = isPlaying()
                pauseTrack()
                isPausedByTransientLossOfFocus = wasPlaying
                startForegroundAndNotify()
                updateMediaSession()
                updateMediaSessionState()
            }

            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // Lower the volume when another app gains focus temporarily
                // (e.g., navigation instructions)
                if (isPlaying()) {
                    mPlayer()?.setVolume(0.2f, 0.2f)
                }
            }
        }
    }
}