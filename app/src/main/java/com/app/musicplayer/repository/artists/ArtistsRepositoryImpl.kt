package com.app.musicplayer.repository.artists

import androidx.lifecycle.LiveData
import com.app.musicplayer.di.factory.contentresolver.ContentResolverFactory
import com.app.musicplayer.di.factory.livedata.LiveDataFactory
import com.app.musicplayer.models.Artist
import javax.inject.Inject

class ArtistsRepositoryImpl @Inject constructor(
    private val liveDataFactory: LiveDataFactory,
    private val contentResolverFactory: ContentResolverFactory
) : ArtistsRepository {
    override fun getArtists(): LiveData<List<Artist>> = liveDataFactory.getArtistsLiveData()
}