package com.app.musicplayer.ui.fragments

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.db.entities.PlaylistEntity
import com.app.musicplayer.ui.activities.PlaylistDetailsActivity
import com.app.musicplayer.ui.adapters.PlaylistsAdapter
import com.app.musicplayer.ui.viewstates.PlaylistViewState
import com.app.musicplayer.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlaylistFragment : ListFragment<PlaylistEntity, PlaylistViewState>() {
    override val viewState: PlaylistViewState by activityViewModels()
    override val manager: RecyclerView.LayoutManager =
        LinearLayoutManager(activity?.applicationContext)

    @Inject
    override lateinit var listAdapter: PlaylistsAdapter
    override fun onSetup() {
        super.onSetup()
        listAdapter.viewHolderType = PLAYLISTS_VT
        viewState.apply {
            fetchPlaylists().observe(this@PlaylistFragment) {
                listAdapter.items = it
                showEmpty(listAdapter.items.isEmpty())
            }
            showMenuEvent.observe(this@PlaylistFragment) { event ->
                event.ifNew?.let { playlistCombinedData ->
                    playlistCombinedData.view?.let {
                        baseActivity.showTrackMenu(
                            it, isPlaylist = true
                        ) { callback ->
                            when (callback) {
                                DELETE_TRACK -> {
                                    baseActivity.deleteConfirmationDialog { isDelete ->
                                        if (isDelete) {
                                            deletePlaylist(playlistCombinedData.playlist.playlistId)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            showItemEvent.observe(this@PlaylistFragment) { event ->
                event.ifNew?.let { playlistEntity ->
                    startActivity(
                        Intent(requireContext(), PlaylistDetailsActivity::class.java).apply {
                            putExtra(PLAYLIST_ID, playlistEntity.playlist.playlistId)
                            putExtra(PLAYLIST_NAME, playlistEntity.playlist.playlistName)
                        })
                }
            }
        }
    }
}