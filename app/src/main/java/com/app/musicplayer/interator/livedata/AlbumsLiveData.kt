package com.app.musicplayer.interator.livedata

import android.content.Context
import com.app.musicplayer.contentresolver.AlbumsContentResolver
import com.app.musicplayer.di.factory.contentresolver.ContentResolverFactory
import com.app.musicplayer.models.Album
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Singleton
class AlbumsLiveData(
    @ApplicationContext context: Context,
    private val albumId: Long? = null,
    private val contentResolverFactory: ContentResolverFactory,
) : ContentProviderLiveData<AlbumsContentResolver, Album>(context) {
    override val contentResolver by lazy { contentResolverFactory.getAlbumsContentResolver(albumId) }
}