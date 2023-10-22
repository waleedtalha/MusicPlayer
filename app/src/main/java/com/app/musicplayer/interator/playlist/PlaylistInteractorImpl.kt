package com.app.musicplayer.interator.playlist

import androidx.lifecycle.LiveData
import com.app.musicplayer.db.MusicDB
import javax.inject.Inject

class PlaylistInteractorImpl @Inject constructor(
    private val musicDB: MusicDB
) : PlaylistInteractor {
    override fun getSongsCount(playlistId: Long): LiveData<List<Long>> {
        return musicDB.getPlaylistSongCrossRefDao().getSongIdsForPlaylistLive(playlistId)
    }
}