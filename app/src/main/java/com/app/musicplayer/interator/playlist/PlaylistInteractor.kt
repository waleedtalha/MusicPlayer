package com.app.musicplayer.interator.playlist

import androidx.lifecycle.LiveData

interface PlaylistInteractor {
    fun getSongsCount(playlistId: Long): LiveData<List<Long>>
}