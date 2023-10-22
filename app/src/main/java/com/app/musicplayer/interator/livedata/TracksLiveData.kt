package com.app.musicplayer.interator.livedata

import android.content.Context
import com.app.musicplayer.contentresolver.TracksContentResolver
import com.app.musicplayer.di.factory.contentresolver.ContentResolverFactory
import com.app.musicplayer.models.Track
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Singleton
class TracksLiveData(
    @ApplicationContext context: Context,
    private val trackId: Long? = null,
    private val albumId: Long? = null,
    private val artistId: Long? = null,
    private val contentResolverFactory: ContentResolverFactory,
) : ContentProviderLiveData<TracksContentResolver, Track>(context) {
    override val contentResolver by lazy { contentResolverFactory.getTracksContentResolver(trackId,albumId,artistId) }
}