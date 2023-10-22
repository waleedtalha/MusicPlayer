package com.app.musicplayer.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.musicplayer.databinding.ActivityArtistDetailsBinding
import com.app.musicplayer.db.entities.PlaylistEntity
import com.app.musicplayer.db.entities.PlaylistSongCrossRef
import com.app.musicplayer.extentions.beVisibleIf
import com.app.musicplayer.extentions.deleteTrack
import com.app.musicplayer.extentions.shareTrack
import com.app.musicplayer.extentions.toast
import com.app.musicplayer.interator.tracks.TracksInteractor
import com.app.musicplayer.ui.adapters.TracksAdapter
import com.app.musicplayer.ui.base.BaseActivity
import com.app.musicplayer.ui.viewstates.ArtistDetailsViewState
import com.app.musicplayer.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ArtistDetailsActivity : BaseActivity<ArtistDetailsViewState>() {
    override val viewState: ArtistDetailsViewState by viewModels()
    private val binding by lazy { ActivityArtistDetailsBinding.inflate(layoutInflater) }
    override val contentView: View by lazy { binding.root }

    @Inject
    lateinit var tracksAdapter: TracksAdapter

    @Inject
    lateinit var tracksInteractor: TracksInteractor

    @SuppressLint("SetTextI18n")
    override fun onSetup() {
        onSetupViews()
        viewState.apply {
            showItemEvent.observe(this@ArtistDetailsActivity) { event ->
                event.ifNew?.let { trackCombinedData ->
                    startActivity(
                        Intent(
                            this@ArtistDetailsActivity,
                            MusicPlayerActivity::class.java
                        ).apply {
                            putExtra(TRACK_ID, trackCombinedData.track.id)
                            putExtra(POSITION, trackCombinedData.position)
                            putExtra(PLAYER_LIST, FROM_ARTIST_LIST)
                            putExtra(ARTIST_ID, intent.getLongExtra(ARTIST_ID, 0L))
                        })
                }
            }
            showMenuEvent.observe(this@ArtistDetailsActivity) { event ->
                event.ifNew?.let { trackCombinedData ->
                    trackCombinedData.view?.let {
                        showTrackMenu(it) { callback ->
                            when (callback) {
                                PLAY_TRACK -> {
                                    startActivity(
                                        Intent(
                                            this@ArtistDetailsActivity,
                                            MusicPlayerActivity::class.java
                                        ).apply {
                                            putExtra(TRACK_ID, trackCombinedData.track.id)
                                            putExtra(POSITION, trackCombinedData.position)
                                            putExtra(PLAYER_LIST, FROM_ARTIST_LIST)
                                            putExtra(ARTIST_ID, intent.getLongExtra(ARTIST_ID, 0L))
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
                                    trackCombinedData.track.path?.shareTrack(this@ArtistDetailsActivity)
                                }

                                DELETE_TRACK -> {
                                    if (isRPlus()) {
                                        deleteTrack(
                                            DELETE_TRACK_CODE,
                                            trackCombinedData.track.id ?: 0L
                                        )
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
            itemsChangedEvent.observe(this@ArtistDetailsActivity) { event ->
                event.ifNew?.let { list ->
                    tracksAdapter.items = list
                    showEmpty(tracksAdapter.items.isEmpty())
                    binding.songsAndAlbums.text = "${list.size} Songs"
                }
            }
            queryArtistDetails(intent.getLongExtra(ARTIST_ID, 0L))
            getItemsObservable { it.observe(this@ArtistDetailsActivity, viewState::onItemsChanged) }
        }
        tracksAdapter.apply {
            setOnItemClickListener(viewState::setOnItemClickListener)
            setOnMenuClickListener(viewState::setOnMenuClickListener)
        }
    }

    private fun onSetupViews() {
        binding.artistsRv.apply {
            this.layoutManager = LinearLayoutManager(applicationContext)
            this.adapter = tracksAdapter
        }
        binding.title.text = intent.getStringExtra(ARTIST_TITLE)
//        val builder = SpannableStringBuilder()
//        val songs = intent.getStringExtra(SONGS_IN_ARTIST)
//        val albums = intent.getStringExtra(ALBUMS_IN_ARTIST)
//        binding.songsAndAlbums.text ="${tracksList.size} Songs"
//            builder.append("$songs Songs").append(" • ").append("$albums Albums")
        binding.moveBack.setOnClickListener { finish() }
    }

    private fun showEmpty(isShow: Boolean) {
        binding.apply {
            empty.emptyImage.beVisibleIf(isShow)
            empty.emptyText.beVisibleIf(isShow)
            artistsRv.beVisibleIf(!isShow)
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

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}