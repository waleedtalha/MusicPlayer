package com.app.musicplayer.repository.artists

import androidx.lifecycle.LiveData
import com.app.musicplayer.models.Artist

interface ArtistsRepository {
    fun getArtists(): LiveData<List<Artist>>
}