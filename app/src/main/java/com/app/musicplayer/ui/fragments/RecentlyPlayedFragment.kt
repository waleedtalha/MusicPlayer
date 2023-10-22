package com.app.musicplayer.ui.fragments

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.db.entities.PlaylistEntity
import com.app.musicplayer.db.entities.PlaylistSongCrossRef
import com.app.musicplayer.db.entities.RecentTrackEntity
import com.app.musicplayer.extentions.shareTrack
import com.app.musicplayer.extentions.toTrack
import com.app.musicplayer.extentions.toast
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.models.TrackCombinedData
import com.app.musicplayer.ui.activities.MusicPlayerActivity
import com.app.musicplayer.ui.adapters.RecentTracksAdapter
import com.app.musicplayer.ui.viewstates.RecentTrackViewState
import com.app.musicplayer.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RecentlyPlayedFragment : ListFragment<RecentTrackEntity, RecentTrackViewState>() {
    override val viewState: RecentTrackViewState by activityViewModels()
    override val manager: RecyclerView.LayoutManager =
        LinearLayoutManager(activity?.applicationContext)

    @Inject
    override lateinit var listAdapter: RecentTracksAdapter

    @Inject
    lateinit var tracksInteractor: TracksInteractor
    override fun onSetup() {
        super.onSetup()
        listAdapter.viewHolderType = RECENT_TRACK_VT
        viewState.apply {
            fetchRecentTrackList().observe(this@RecentlyPlayedFragment) {
                listAdapter.items = it
                showEmpty(listAdapter.items.isEmpty())
            }
            showItemEvent.observe(this@RecentlyPlayedFragment) { event ->
                event.ifNew?.let { trackCombinedData ->
                    startActivity(Intent(requireContext(), MusicPlayerActivity::class.java).apply {
                        putExtra(TRACK_ID, trackCombinedData.track.id)
                        putExtra(POSITION, trackCombinedData.position)
                        putExtra(PLAYER_LIST, FROM_RECENT)
                    })
                }
            }
            showMenuEvent.observe(this@RecentlyPlayedFragment) { event ->
                event.ifNew?.let { trackCombinedData ->
                    trackCombinedData.view?.let {
                        baseActivity.showTrackMenu(it, true) { callback ->
                            when (callback) {
                                PLAY_TRACK -> {
                                    startActivity(
                                        Intent(
                                            requireContext(),
                                            MusicPlayerActivity::class.java
                                        ).apply {
                                            putExtra(TRACK_ID, trackCombinedData.track.id)
                                            putExtra(POSITION, trackCombinedData.position)
                                            putExtra(PLAYER_LIST, FROM_RECENT)
                                        })
                                }

                                ADD_TO_PLAYLIST -> {
                                    lifecycleScope.launch {
                                        fetchPlaylists()?.let { playlist ->
                                            baseActivity.bsAddToPlaylist(playlist) { callback ->
                                                when (callback) {
                                                    CREATE_PLAYLIST -> {
                                                        baseActivity.bsCreatePlaylist { playlistName ->
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
                                            baseActivity.bsCreatePlaylist { playlistName ->
                                                createPlaylistAndSaveSong(
                                                    playlistName,
                                                    trackCombinedData.track.id ?: 0L
                                                )
                                            }
                                        }
                                    }
                                }

                                SHARE_TRACK -> {
                                    context?.let { it2 ->
                                        trackCombinedData.track.path?.shareTrack(it2)
                                    }
                                }

                                DELETE_TRACK -> {
                                    baseActivity.deleteConfirmationDialog(isSong = true) { isDelete ->
                                        if (isDelete) {
                                            viewState.removeRecentTrack(
                                                trackCombinedData.track.id ?: 0L
                                            )
                                        }
                                    }
                                }

                                RENAME_TRACK -> {
                                }

                                PROPERTIES_TRACK -> {
                                    val recentTrackCombined = TrackCombinedData(
                                        trackCombinedData.track.toTrack(),
                                        trackCombinedData.position,
                                        trackCombinedData.view
                                    )
                                    baseActivity.showTrackPropertiesDialog(recentTrackCombined)
                                }
                            }
                        }
                    }
                }
            }
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
        context?.toast("Song added to playlist")
    }

    private fun createPlaylistAndSaveSong(playlistName: String, songId: Long) {
        val playlistModel = PlaylistEntity(
            playlistId = 0,
            playlistName = playlistName
        )
        viewState.insertNewPlaylist(playlistModel)
        context?.toast("${playlistModel.playlistName} created successfully")

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