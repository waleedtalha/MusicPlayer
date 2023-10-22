package com.app.musicplayer.ui.activities

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.app.musicplayer.R
import com.app.musicplayer.databinding.ActivityMusicPlayerBinding
import com.app.musicplayer.db.entities.PlaylistEntity
import com.app.musicplayer.db.entities.PlaylistSongCrossRef
import com.app.musicplayer.extentions.*
import com.app.musicplayer.helpers.MediaPlayer.setPlayBackSpeed
import com.app.musicplayer.helpers.OnSwipeTouchListener
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.models.Track
import com.app.musicplayer.services.MusicService
import com.app.musicplayer.services.MusicService.Companion.positionTrack
import com.app.musicplayer.services.MusicService.Companion.tracksList
import com.app.musicplayer.ui.base.BaseActivity
import com.app.musicplayer.ui.viewstates.MusicPlayerViewState
import com.app.musicplayer.utils.*
import com.bumptech.glide.Glide
import com.realpacific.clickshrinkeffect.applyClickShrink
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicPlayerActivity : BaseActivity<MusicPlayerViewState>() {

    @Inject
    lateinit var tracksInteractor: TracksInteractor

    private val binding by lazy { ActivityMusicPlayerBinding.inflate(layoutInflater) }
    override val viewState: MusicPlayerViewState by viewModels()
    override val contentView: View by lazy { binding.root }
    private val intentProgressDurationFilter = IntentFilter(PROGRESS_CONTROLS_ACTION)
    private val intentNextPrevious = IntentFilter(NEXT_PREVIOUS_ACTION)
    private val intentPlayPause = IntentFilter(PLAY_PAUSE_ACTION)
    private val intentDismiss = IntentFilter(DISMISS_PLAYER_ACTION)
    private val intentComplete = IntentFilter(TRACK_COMPLETE_ACTION)
    private var timerHandler = Handler()
    private val timerRunnable = Runnable {
        //pause the player when sleep time reached
        sendIntent(PLAYPAUSE)
        timerHandler.removeCallbacksAndMessages(null)
    }

    var position: Int = 0
    var isA: Boolean = false
    var isB: Boolean = false

    companion object {
        var aPosition: Int = 0
        var bPosition: Int = 0
    }

    var playerReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            when (intent?.action) {
                PROGRESS_CONTROLS_ACTION -> {
                    position = intent.getIntExtra(GET_CURRENT_POSITION, 0)
                    val duration = intent.getIntExtra(GET_TRACK_DURATION, 0)
                    binding.seekBar.max = duration
                    binding.seekBar.progress = position
                    setUpSeekbar()
                    binding.playedDuration.text = formatMillisToHMS(position.toLong())
                }

                NEXT_PREVIOUS_ACTION -> {
                    if ((intent.getLongExtra(NEXT_PREVIOUS_TRACK_ID, 0L)) != 0L) {
                        prefs.currentTrackId = intent.getLongExtra(NEXT_PREVIOUS_TRACK_ID, 0L)
                        updateTrackInfo(prefs.currentTrackId ?: 0L)
                    }
                }

                DISMISS_PLAYER_ACTION -> {
                    if (intent.getBooleanExtra(DISMISS_PLAYER, false)) {
                        finish()
                    }
                }

                TRACK_COMPLETE_ACTION -> {
                    if (intent.getBooleanExtra(TRACK_COMPLETED, false)) {
                        binding.nextTrack.performClick()
                    }
                }

                PLAY_PAUSE_ACTION -> {
                    when (intent.getBooleanExtra(PLAY_PAUSE_ICON, true)) {
                        true -> binding.playPauseTrack.updatePlayIcon(
                            this@MusicPlayerActivity,
                            false
                        )

                        false -> binding.playPauseTrack.updatePlayIcon(
                            this@MusicPlayerActivity,
                            true
                        )
                    }
                }
            }
        }
    }

    override fun onSetup() {
        handleNotificationPermission { granted ->
            if (granted) {
                setUpButtons()
                registerReceivers()
                if (!intent.getBooleanExtra(FROM_MINI_PLAYER, false)) {
                    when (intent.getStringExtra(PLAYER_LIST)) {
                        FROM_ALL_SONG -> {
                            viewState.getAllTrackList { trList ->
                                setPlayerList(trList)
                                initPlayer()
                            }
                        }

                        FROM_RECENT -> {
                            viewState.fetchRecentTrackList().observe(this) {
                                val recentTrackList: ArrayList<Track> =
                                    ArrayList(it.map { recentTrack ->
                                        recentTrack.toTrack()
                                    })
                                setPlayerList(recentTrackList)
                                initPlayer()
                            }
                        }

                        FROM_FAVORITE -> {
                            lifecycleScope.launch {
                                viewState.fetchFavorites().let {
                                    it?.let { it1 -> setPlayerList(it1) }
                                    initPlayer()
                                }
                            }
                        }

                        FROM_ALBUM_LIST -> {
                            viewState.getAlbumTracksOnlyList(intent.getLongExtra(ALBUM_ID, 0L))
                                .observe(this) { albumList ->
                                    setPlayerList(albumList)
                                    initPlayer()
                                }
                        }

                        FROM_ARTIST_LIST -> {
                            viewState.getArtistTracksOnlyList(intent.getLongExtra(ARTIST_ID, 0L))
                                .observe(this) { artistList ->
                                    setPlayerList(artistList)
                                    initPlayer()
                                }
                        }

                        FROM_PLAYLIST -> {
                            lifecycleScope.launch {
                                viewState.fetchSongIdsForPlaylist(
                                    intent.getLongExtra(
                                        PLAYLIST_ID,
                                        0L
                                    )
                                )?.let { songIdsList ->
                                    returnList(songIdsList) { trackList ->
                                        setPlayerList(trackList)
                                        initPlayer()
                                    }
                                }
                            }
                        }
                    }
                } else {
                    updatePlayPauseDrawable(binding.playPauseTrack, this@MusicPlayerActivity)
                }
                updateTrackInfo(intent.getLongExtra(TRACK_ID, 0L))
            }
        }
    }

    private fun initPlayer() {
        Intent(this, MusicService::class.java).apply {
            putExtra(TRACK_ID_SERVICE, intent.getLongExtra(TRACK_ID, 0L))
            putExtra(POSITION, intent.getIntExtra(POSITION, 0))
            action = INIT
            startService(this)
        }
    }

    override fun onPause() {
        super.onPause()
        if (!intent.getStringExtra(PLAYER_LIST).equals(FROM_RECENT)) {
            tracksInteractor.queryTrack(prefs.currentTrackId ?: 0L) { track ->
                track?.toRecentTrackEntity()?.let { viewState.insertRecentTrack(it) }
            }
        }
    }

    private fun updateTrackInfo(id: Long) {
        lifecycleScope.launch {
            viewState.fetchFavorites().let { list ->
                tracksInteractor.queryTrack(id) { track ->
                    if (list?.contains(track) == true) {
                        binding.favouriteTrack.updateFavoriteIcon(this@MusicPlayerActivity, true)
                    } else {
                        binding.favouriteTrack.updateFavoriteIcon(this@MusicPlayerActivity, false)
                    }
                    binding.trackName.isSelected = true
                    binding.trackName.text = track?.title ?: ""
                    binding.artistName.text = track?.artist?.isUnknownString() ?: ""
                    binding.totalDuration.text = formatMillisToHMS(track?.duration ?: 0L)
//                    updatePlayPauseDrawable(binding.playPauseTrack, this@MusicPlayerActivity)
                    Glide.with(this@MusicPlayerActivity)
                        .load(track?.albumId?.getThumbnailUri() ?: "")
                        .placeholder(R.drawable.ic_music).into(binding.thumbnail)
                }
            }
        }
    }

    private fun setUpSeekbar() {
        binding.seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(seekbar: SeekBar) {}
            override fun onStopTrackingTouch(seekbar: SeekBar) {
                Intent(this@MusicPlayerActivity, MusicService::class.java).apply {
                    putExtra(PROGRESS, seekbar.progress)
                    action = SET_PROGRESS
                    startService(this)
                }
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ClickableViewAccessibility")
    private fun setUpButtons() {
        binding.apply {
            playPauseTrack.applyClickShrink()
            nextTrack.applyClickShrink()
            previousTrack.applyClickShrink()
            repeatTrack.applyClickShrink()
            shuffleTrack.applyClickShrink()
            favouriteTrack.applyClickShrink()
            playerMenuMore.applyClickShrink()
            back.setOnClickListener { finish() }
            playPauseTrack.setOnClickListener { sendIntent(PLAYPAUSE) }
            nextTrack.setOnClickListener { sendIntent(NEXT) }
            previousTrack.setOnClickListener { sendIntent(PREVIOUS) }
            repeatTrack.setOnClickListener { repeatTrack() }
            shuffleTrack.setOnClickListener { shuffleTrack() }
            playerMenuMore.setOnClickListener { playerMenus() }
            favouriteTrack.setOnClickListener { favoriteTrack() }
            a.setOnClickListener { setAPosition() }
            b.setOnClickListener { setBPosition() }
            abClose.setOnClickListener { clearAbRepeat() }
            root.setOnTouchListener(object : OnSwipeTouchListener(this@MusicPlayerActivity) {
                override fun onSwipeDown() {
                    super.onSwipeDown()
                    finish()
                    overridePendingTransition(0, R.anim.slide_down)
                }
            })
        }
        setUpPreferences()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun playerMenus() {
        playerMenu(binding.playerMenuMore) { menuCallBack ->
            when (menuCallBack) {
                AB_REPEAT_TRACK -> {
                    binding.abContainer.beVisible()
                }

                PLAY_SPEED_TRACK -> {
                    bsPlaySpeed { speedCallBack ->
                        when (speedCallBack) {
                            DONE -> {
                                when (prefs.setPlaySpeed) {
                                    PLAY_SPEED_0_5x -> setPlayBackSpeed(PLAY_SPEED_0_5x)
                                    PLAY_SPEED_0_75x -> setPlayBackSpeed(PLAY_SPEED_0_75x)
                                    PLAY_SPEED_1x -> setPlayBackSpeed(PLAY_SPEED_1x)
                                    PLAY_SPEED_1_25x -> setPlayBackSpeed(PLAY_SPEED_1_25x)
                                    PLAY_SPEED_1_5x -> setPlayBackSpeed(PLAY_SPEED_1_5x)
                                    PLAY_SPEED_2x -> setPlayBackSpeed(PLAY_SPEED_2x)
                                }
                            }
                        }
                    }
                }

                SLEEP_TIMER -> {
                    bsSleepTimer {
                        cancelTimer()
                        val delayInMillis = it.getTimerMinutes() * 60 * 1000L
                        timerHandler.postDelayed(timerRunnable, delayInMillis)
                        toast("Player will stop in ${it.getTimerMinutes()} minutes")
                    }
                }

                ADD_TO_PLAYLIST -> {
                    lifecycleScope.launch {
                        viewState.fetchPlaylists()?.let { playlist ->
                            bsAddToPlaylist(playlist) { callback ->
                                when (callback) {
                                    CREATE_PLAYLIST -> {
                                        bsCreatePlaylist { playlistName ->
                                            createPlaylistAndSaveSong(
                                                playlistName,
                                                intent.getLongExtra(TRACK_ID, 0L)
                                            )
                                        }
                                    }

                                    else -> {
                                        //clicked on playlist to add song
                                        addSongToPlaylist(
                                            callback.toLong(),
                                            intent.getLongExtra(TRACK_ID, 0L)
                                        )
                                    }
                                }
                            }
                        } ?: run {
                            //first time open create playlist sheet by default
                            bsCreatePlaylist { playlistName ->
                                createPlaylistAndSaveSong(
                                    playlistName,
                                    intent.getLongExtra(TRACK_ID, 0L)
                                )
                            }
                        }
                    }
                }

                SHARE_TRACK -> {
                    tracksList[positionTrack].path?.shareTrack(this@MusicPlayerActivity) ?: ""
                }

                DELETE_TRACK -> {
                    if (isRPlus()) {
                        deleteTrack(DELETE_PLAYING_TRACK, tracksList[positionTrack].id ?: 0L)
                    }
                }

                SETTINGS -> {
                    startActivity(Intent(this@MusicPlayerActivity, SettingsActivity::class.java))
                }

                SET_TRACK_AS -> {
                    bsSetRingtone {
                        when (it) {
                            DONE -> {
                                when (prefs.setRingtone) {
                                    PHONE_RINGTONE -> viewState.setRingtone(
                                        context = this@MusicPlayerActivity,
                                        trackId = tracksList[positionTrack].id ?: 0L
                                    )

                                    ALARM_RINGTONE -> viewState.setAlarmTone(
                                        context = this@MusicPlayerActivity,
                                        trackPath = tracksList[positionTrack].path ?: ""
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun cancelTimer() {
        timerHandler.removeCallbacks(timerRunnable)
    }

    private fun setPlayerList(list: List<Track>) {
        tracksList.clear()
        tracksList.addAll(list)
    }

    private fun setUpPreferences() {
        when (prefs.repeatTrack) {
            REPEAT_TRACK_ON -> binding.repeatTrack.setSelectedTint(context = this)
        }
        when (prefs.shuffleTrack) {
            SHUFFLE_TRACK_ON -> binding.shuffleTrack.setSelectedTint(context = this)
        }
        binding.seekBar.max = prefs.currentTrackTotalDuration ?: 0
        binding.seekBar.progress = prefs.currentTrackProgress ?: 0
        setUpSeekbar()
        binding.playedDuration.text =
            prefs.currentTrackProgress?.let { formatMillisToHMS(it.toLong()) }
    }

    private fun favoriteTrack() {
        lifecycleScope.launch {
            viewState.fetchFavorites().let { list ->
                tracksInteractor.queryTrack(prefs.currentTrackId ?: 0L) {
                    if (list?.contains(it) == true) {
                        toast(getString(R.string.remove_favorites))
                        binding.favouriteTrack.updateFavoriteIcon(this@MusicPlayerActivity, false)
                        viewState.removeFavoriteTrack(prefs.currentTrackId ?: 0L)
                    } else {
                        toast(getString(R.string.add_favorites))
                        binding.favouriteTrack.updateFavoriteIcon(this@MusicPlayerActivity, true)
                        tracksInteractor.queryTrack(prefs.currentTrackId ?: 0L) { track ->
                            track?.let { it1 -> viewState.insertFavoriteTrack(it1) }
                        }
                    }
                }
            }
        }
    }

    private fun repeatTrack() {
        when {
            prefs.shuffleTrack == SHUFFLE_TRACK_ON -> {
                prefs.shuffleTrack = SHUFFLE_TRACK_OFF
                binding.shuffleTrack.setUnSelectedTint(context = this)
            }

            prefs.repeatTrack == REPEAT_TRACK_OFF -> {
                prefs.repeatTrack = REPEAT_TRACK_ON
                binding.repeatTrack.setSelectedTint(context = this)
            }

            prefs.repeatTrack == REPEAT_TRACK_ON -> {
                prefs.repeatTrack = REPEAT_TRACK_OFF
                binding.repeatTrack.setUnSelectedTint(context = this)
            }
        }
    }

    private fun shuffleTrack() {
        when {
            prefs.repeatTrack == REPEAT_TRACK_ON -> {
                prefs.repeatTrack = REPEAT_TRACK_OFF
                binding.repeatTrack.setUnSelectedTint(context = this)
            }

            prefs.shuffleTrack == SHUFFLE_TRACK_OFF -> {
                prefs.shuffleTrack = SHUFFLE_TRACK_ON
                binding.shuffleTrack.setSelectedTint(context = this)
            }

            prefs.shuffleTrack == SHUFFLE_TRACK_ON -> {
                prefs.shuffleTrack = SHUFFLE_TRACK_OFF
                binding.shuffleTrack.setUnSelectedTint(context = this)
            }
        }
    }

    private fun setAPosition() {
        if (isA) {
            aPosition = 0
            binding.a.text = "A"
            isA = false
        } else {
            aPosition = position
            binding.a.text = formatMillisToHMS(aPosition.toLong())
            isA = true
        }
    }

    private fun setBPosition() {
        if (isB) {
            bPosition = 0
            binding.b.text = "B"
            isB = false
        } else {
            bPosition = position
            binding.b.text = formatMillisToHMS(bPosition.toLong())
            isB = true
        }
    }

    private fun clearAbRepeat() {
        isA = false
        isB = false
        aPosition = 0
        bPosition = 0
        binding.a.text = "A"
        binding.b.text = "B"
        binding.abContainer.beGone()
    }

    private fun returnList(list: List<Long>, callback: (List<Track>) -> Unit) {
        val trackList = mutableListOf<Track>()
        var count = 0
        list.forEach { id ->
            tracksInteractor.queryTrack(id) { track ->
                track?.let {
                    trackList.add(it)
                }
                count++
                if (count == list.size) {
                    callback(trackList)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(playerReceiver)
        sendIntent(FINISH_IF_NOT_PLAYING)
    }

    private fun registerReceivers() {
        registerReceiver(playerReceiver, intentProgressDurationFilter)
        registerReceiver(playerReceiver, intentNextPrevious)
        registerReceiver(playerReceiver, intentPlayPause)
        registerReceiver(playerReceiver, intentDismiss)
        registerReceiver(playerReceiver, intentComplete)
    }

    private fun addSongToPlaylist(playlistId: Long, songId: Long) {
        val crossRef =
            PlaylistSongCrossRef(
                playlistId,
                songId
            )
        lifecycleScope.launch(
            Dispatchers.IO
        ) {
            viewState.insert(crossRef)
        }
        toast("Song added to playlist")
    }

    private fun createPlaylistAndSaveSong(playlistName: String, songId: Long) {
        val playlistModel = PlaylistEntity(
            playlistId = 0,
            playlistName = playlistName
        )
        viewState.insertNewPlaylist(playlistModel)
        toast("${playlistModel.playlistName} created successfully")

        //add song to newly created playlist
        lifecycleScope.launch {
            viewState.fetchPlaylists()?.let { playlist ->
                playlist.forEach { playlistEntity ->
                    if (playlistName == playlistEntity.playlistName) {
                        addSongToPlaylist(
                            playlistEntity.playlistId,
                            songId
                        )
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}