package com.app.musicplayer.ui.fragments

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.extentions.isUnknownString
import com.app.musicplayer.models.Artist
import com.app.musicplayer.ui.activities.ArtistDetailsActivity
import com.app.musicplayer.ui.adapters.ArtistsAdapter
import com.app.musicplayer.ui.viewstates.ArtistsViewState
import com.app.musicplayer.utils.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ArtistsFragment : ListFragment<Artist, ArtistsViewState>() {
    override val viewState: ArtistsViewState by activityViewModels()
    override val manager: RecyclerView.LayoutManager =
        GridLayoutManager(activity?.applicationContext, 2)

    @Inject
    override lateinit var listAdapter: ArtistsAdapter

    override fun onSetup() {
        super.onSetup()
        listAdapter.viewHolderType = ARTISTS_VT
        viewState.apply {
            showItemEvent.observe(this@ArtistsFragment) { event ->
                event.ifNew?.let { artist ->
                    startActivity(
                        Intent(requireContext(), ArtistDetailsActivity::class.java).apply {
                            putExtra(ARTIST_ID, artist.id ?: 0L)
                            putExtra(ARTIST_TITLE, artist.artistTitle?.isUnknownString() ?: "")
                            putExtra(SONGS_IN_ARTIST, artist.tracksCount ?: "")
                            putExtra(ALBUMS_IN_ARTIST, artist.albumsCount ?: "")
                        })
                }
            }
        }
    }
}