package com.app.musicplayer.ui.fragments

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.db.entities.PlaylistEntity
import com.app.musicplayer.db.entities.PlaylistSongCrossRef
import com.app.musicplayer.extentions.deleteTrack
import com.app.musicplayer.extentions.shareTrack
import com.app.musicplayer.extentions.toast
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.models.Track
import com.app.musicplayer.services.MusicService.Companion.tracksList
import com.app.musicplayer.ui.activities.MusicPlayerActivity
import com.app.musicplayer.ui.adapters.TracksAdapter
import com.app.musicplayer.ui.viewstates.TracksViewState
import com.app.musicplayer.utils.ADD_TO_PLAYLIST
import com.app.musicplayer.utils.ALL_TRACKS_VT
import com.app.musicplayer.utils.CREATE_PLAYLIST
import com.app.musicplayer.utils.DELETE_TRACK
import com.app.musicplayer.utils.DELETE_TRACK_CODE
import com.app.musicplayer.utils.FROM_ALL_SONG
import com.app.musicplayer.utils.PLAYER_LIST
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
class AllMusicFragment : ListFragment<Track, TracksViewState>() {
    override val viewState: TracksViewState by activityViewModels()
    override val manager: RecyclerView.LayoutManager =
        LinearLayoutManager(activity?.applicationContext)

    @Inject
    override lateinit var listAdapter: TracksAdapter

    @Inject
    lateinit var tracksInteractor: TracksInteractor

    override fun onSetup() {
        super.onSetup()
        listAdapter.viewHolderType = ALL_TRACKS_VT
        viewState.apply {
            showItemEvent.observe(this@AllMusicFragment) { event ->
                event.ifNew?.let { trackCombinedData ->
                    startActivity(
                        Intent(requireContext(), MusicPlayerActivity::class.java).apply {
                            putExtra(TRACK_ID, trackCombinedData.track.id)
                            putExtra(POSITION, trackCombinedData.position)
                            putExtra(PLAYER_LIST, FROM_ALL_SONG)
                        })
                }
            }
            showMenuEvent.observe(this@AllMusicFragment) { event ->
                event.ifNew?.let { trackCombinedData ->
                    trackCombinedData.view?.let {
                        baseActivity.showTrackMenu(it) { callback ->
                            when (callback) {
                                PLAY_TRACK -> {
                                    startActivity(
                                        Intent(
                                            requireContext(),
                                            MusicPlayerActivity::class.java
                                        ).apply {
                                            putExtra(TRACK_ID, trackCombinedData.track.id)
                                            putExtra(POSITION, trackCombinedData.position)
                                            putExtra(PLAYER_LIST, FROM_ALL_SONG)
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
                                    if (isRPlus()) {
                                        activity?.deleteTrack(
                                            DELETE_TRACK_CODE,
                                            trackCombinedData.track.id ?: 0L
                                        )
                                    }
                                }

                                RENAME_TRACK -> {
                                    baseActivity.bsRenameTrack(
                                        trackCombinedData.track.title ?: ""
                                    ) { renamedText ->
                                        tracksInteractor.renameTrack(trackCombinedData, renamedText)
                                        viewState.getTrackList { trList ->
                                            tracksList = trList as ArrayList<Track>
                                        }
                                    }
                                }

                                PROPERTIES_TRACK -> {
                                    baseActivity.showTrackPropertiesDialog(trackCombinedData)
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