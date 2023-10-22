package com.app.musicplayer.ui.fragments

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.models.Album
import com.app.musicplayer.ui.activities.AlbumDetailsActivity
import com.app.musicplayer.ui.adapters.AlbumsAdapter
import com.app.musicplayer.ui.viewstates.AlbumsViewState
import com.app.musicplayer.utils.ALBUMS_VT
import com.app.musicplayer.utils.ALBUM_ID
import com.app.musicplayer.utils.ALBUM_TITLE
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlbumsFragment : ListFragment<Album, AlbumsViewState>() {
    override val viewState: AlbumsViewState by activityViewModels()
    override val manager: RecyclerView.LayoutManager =
        LinearLayoutManager(activity?.applicationContext)

    @Inject
    override lateinit var listAdapter: AlbumsAdapter

    override fun onSetup() {
        super.onSetup()
        listAdapter.viewHolderType = ALBUMS_VT
        viewState.apply {
            showItemEvent.observe(this@AlbumsFragment) { event ->
                event.ifNew?.let { album ->
                    startActivity(Intent(requireContext(), AlbumDetailsActivity::class.java).apply {
                        putExtra(ALBUM_ID, album.albumId)
                        putExtra(ALBUM_TITLE, album.albumTitle)
                    })
                }
            }
        }
    }

}