package com.app.musicplayer.ui.fragments

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.R
import com.app.musicplayer.extentions.toast
import com.app.musicplayer.models.Track
import com.app.musicplayer.ui.activities.MusicPlayerActivity
import com.app.musicplayer.ui.adapters.FavoritesAdapter
import com.app.musicplayer.ui.viewstates.FavoritesViewState
import com.app.musicplayer.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesFragment : ListFragment<Track, FavoritesViewState>() {
    override val viewState: FavoritesViewState by activityViewModels()
    override val manager: RecyclerView.LayoutManager =
        LinearLayoutManager(activity?.applicationContext)

    @Inject
    override lateinit var listAdapter: FavoritesAdapter
    override fun onSetup() {
        super.onSetup()
        listAdapter.viewHolderType = FAVORITES_VT
        viewState.apply {
            fetchFavoriteTrackList().observe(this@FavoritesFragment) {
                listAdapter.items = it
                showEmpty(listAdapter.items.isEmpty())
            }
            showItemEvent.observe(this@FavoritesFragment) { event ->
                event.ifNew?.let { trackCombinedData ->
                    startActivity(Intent(requireContext(), MusicPlayerActivity::class.java).apply {
                        putExtra(TRACK_ID, trackCombinedData.track.id)
                        putExtra(POSITION, trackCombinedData.position)
                        putExtra(PLAYER_LIST, FROM_FAVORITE)
                    })
                }
            }
            showFavoriteEvent.observe(this@FavoritesFragment) { event ->
                event.ifNew?.let { track ->
                    context?.toast(getString(R.string.remove_favorites))
                    viewState.removeFavoriteTrack(track.id ?: 0L)
                }
            }
        }
    }
}