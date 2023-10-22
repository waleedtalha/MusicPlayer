package com.app.musicplayer.repository.albums

import androidx.lifecycle.LiveData
import com.app.musicplayer.models.Album
import com.app.musicplayer.models.Track

interface AlbumsRepository {
    fun getAlbums(): LiveData<List<Album>>
}