package com.app.musicplayer.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.musicplayer.databinding.ActivityPlaylistDetailsBinding
import com.app.musicplayer.db.entities.PlaylistEntity
import com.app.musicplayer.db.entities.PlaylistSongCrossRef
import com.app.musicplayer.extentions.beVisibleIf
import com.app.musicplayer.extentions.deleteTrack
import com.app.musicplayer.extentions.shareTrack
import com.app.musicplayer.extentions.toast
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.models.Track
import com.app.musicplayer.ui.adapters.TracksAdapter
import com.app.musicplayer.ui.base.BaseActivity
import com.app.musicplayer.ui.viewstates.PlaylistDetailsViewState
import com.app.musicplayer.utils.ADD_TO_PLAYLIST
import com.app.musicplayer.utils.CREATE_PLAYLIST
import com.app.musicplayer.utils.DELETE_TRACK
import com.app.musicplayer.utils.DELETE_TRACK_CODE
import com.app.musicplayer.utils.FROM_PLAYLIST
import com.app.musicplayer.utils.PLAYER_LIST
import com.app.musicplayer.utils.PLAYLIST_ID
import com.app.musicplayer.utils.PLAYLIST_NAME
import com.app.musicplayer.utils.PLAY_TRACK
import com.app.musicplayer.utils.POSITION
import com.app.musicplayer.utils.PROPERTIES_TRACK
import com.app.musicplayer.utils.RENAME_TRACK
import com.app.musicplayer.utils.SHARE_TRACK
import com.app.musicplayer.utils.TRACK_ID
import com.app.musicplayer.utils.isRPlus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PlaylistDetailsActivity : BaseActivity<PlaylistDetailsViewState>() {
    override val viewState: PlaylistDetailsViewState by viewModels()
    private val binding by lazy { ActivityPlaylistDetailsBinding.inflate(layoutInflater) }
    override val contentView: View by lazy { binding.root }

    @Inject
    lateinit var tracksAdapter: TracksAdapter

    @Inject
    lateinit var tracksInteractor: TracksInteractor

    @SuppressLint("SetTextI18n")
    override fun onSetup() {
        onSetupViews()
        viewState.apply {
            lifecycleScope.launch {
                fetchSongIdsForPlaylistLive(
                    intent.getLongExtra(
                        PLAYLIST_ID,
                        0L
                    )
                ).observe(this@PlaylistDetailsActivity) { songIdsList ->
                    returnList(songIdsList) { trackList ->
                        tracksAdapter.items = trackList
                        showEmpty(tracksAdapter.items.isEmpty())
                    }
                }
            }
            showItemEvent.observe(this@PlaylistDetailsActivity) { event ->
                event.ifNew?.let { trackCombinedData ->
                    startActivity(
                        Intent(
                            this@PlaylistDetailsActivity,
                            MusicPlayerActivity::class.java
                        ).apply {
                            putExtra(TRACK_ID, trackCombinedData.track.id)
                            putExtra(POSITION, trackCombinedData.position)
                            putExtra(PLAYER_LIST, FROM_PLAYLIST)
                            putExtra(PLAYLIST_ID, intent.getLongExtra(PLAYLIST_ID, 0L))
                        })
                }
            }
            showMenuEvent.observe(this@PlaylistDetailsActivity) { event ->
                event.ifNew?.let { trackCombinedData ->
                    trackCombinedData.view?.let {
                        showTrackMenu(it, isPlaylistSong = true) { callback ->
                            when (callback) {
                                PLAY_TRACK -> {
                                    startActivity(
                                        Intent(
                                            this@PlaylistDetailsActivity,
                                            MusicPlayerActivity::class.java
                                        ).apply {
                                            putExtra(TRACK_ID, trackCombinedData.track.id)
                                            putExtra(POSITION, trackCombinedData.position)
                                            putExtra(PLAYER_LIST, FROM_PLAYLIST)
                                            putExtra(
                                                PLAYLIST_ID,
                                                intent.getLongExtra(PLAYLIST_ID, 0L)
                                            )
                                        })
                                }

                                ADD_TO_PLAYLIST -> {
                                    lifecycleScope.launch {
                                        fetchPlaylists()?.let { playlist ->
                                            bsAddToPlaylist(playlist) { callback ->
                                                when (callback) {
                                                    CREATE_PLAYLIST -> {
                                                        bsCreatePlaylist { playlistName ->
                                                            createPlaylistAndSaveSong(
                                                                playlistName,
                                                                trackCombinedData.track.id ?: 0L
                                                            )
                                                        }
                                                    }

                                                    else -> {
                                                        //clicked on playlist to add song
                                                        addSongToPlaylist(
                                                            callback.toLong(),
                                                            trackCombinedData.track.id
                                                                ?: 0L
                                                        )
                                                    }
                                                }
                                            }
                                        } ?: run {
                                            //first time open create playlist sheet by default
                                            bsCreatePlaylist { playlistName ->
                                                createPlaylistAndSaveSong(
                                                    playlistName,
                                                    trackCombinedData.track.id ?: 0L
                                                )
                                            }
                                        }
                                    }
                                }

                                SHARE_TRACK -> {
                                    trackCombinedData.track.path?.shareTrack(this@PlaylistDetailsActivity)
                                }

                                DELETE_TRACK -> {
                                    lifecycleScope.launch {
                                        fetchSongIdsForPlaylist(
                                            intent.getLongExtra(
                                                PLAYLIST_ID,
                                                0L
                                            )
                                        )?.let { songIdsList ->
                                            songIdsList.forEach { id ->
                                                if (id == trackCombinedData.track.id) {
                                                    deleteConfirmationDialog(true) {
                                                        removeSongFromPlaylist(
                                                            intent.getLongExtra(
                                                                PLAYLIST_ID,
                                                                0L
                                                            ), id
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }

                                }

                                RENAME_TRACK -> {
                                    bsRenameTrack(
                                        trackCombinedData.track.title ?: ""
                                    ) { renamedText ->
                                        tracksInteractor.renameTrack(trackCombinedData, renamedText)
                                    }
                                }

                                PROPERTIES_TRACK -> {
                                    showTrackPropertiesDialog(trackCombinedData)
                                }
                            }
                        }
                    }
                }
            }
        }
        tracksAdapter.apply {
            setOnItemClickListener(viewState::setOnItemClickListener)
            setOnMenuClickListener(viewState::setOnMenuClickListener)
        }
    }

    private fun returnList(list: List<Long>, callback: (List<Track>) -> Unit) {
        val trackList = mutableListOf<Track>()
        var count = 0
        if (list.isNotEmpty()) {
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
        } else {
            callback(trackList)
            finish()
        }
    }


    private fun onSetupViews() {
        binding.playlistSongsRv.apply {
            this.layoutManager = LinearLayoutManager(applicationContext)
            this.adapter = tracksAdapter
        }
        binding.title.text = intent.getStringExtra(PLAYLIST_NAME)
        binding.moveBack.setOnClickListener { finish() }
    }

    private fun showEmpty(isShow: Boolean) {
        binding.apply {
            empty.emptyImage.beVisibleIf(isShow)
            empty.emptyText.beVisibleIf(isShow)
            playlistSongsRv.beVisibleIf(!isShow)
        }
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
}