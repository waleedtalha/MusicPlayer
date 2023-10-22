package com.app.musicplayer.di.factory.livedata

import android.content.Context
import com.app.musicplayer.di.factory.contentresolver.ContentResolverFactory
import com.app.musicplayer.interator.livedata.AlbumsLiveData
import com.app.musicplayer.interator.livedata.ArtistsLiveData
import com.app.musicplayer.interator.livedata.TracksLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LiveDataFactoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val contentResolverFactory: ContentResolverFactory
) : LiveDataFactory {
    override fun getTracksLiveData(trackId: Long?): TracksLiveData =
        TracksLiveData(context, trackId, null,null, contentResolverFactory)

    override fun getAlbumsLiveData(albumId: Long?): AlbumsLiveData =
        AlbumsLiveData(context, albumId, contentResolverFactory)

    override fun getArtistsLiveData(artistId: Long?): ArtistsLiveData =
        ArtistsLiveData(context, artistId, contentResolverFactory)

    override fun getAlbumsTracksLiveData(albumId: Long?) =
        TracksLiveData(context, null, albumId, null, contentResolverFactory)

    override fun getArtistsTracksLiveData(artistId: Long?) =
        TracksLiveData(context, null, null, artistId, contentResolverFactory)
}