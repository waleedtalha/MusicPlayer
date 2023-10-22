package com.app.musicplayer.repository.albums

import androidx.lifecycle.LiveData
import com.app.musicplayer.di.factory.contentresolver.ContentResolverFactory
import com.app.musicplayer.di.factory.livedata.LiveDataFactory
import com.app.musicplayer.models.Album
import javax.inject.Inject

class AlbumsRepositoryImpl @Inject constructor(
    private val liveDataFactory: LiveDataFactory,
    private val contentResolverFactory: ContentResolverFactory
) : AlbumsRepository {
    override fun getAlbums(): LiveData<List<Album>> = liveDataFactory.getAlbumsLiveData()
}