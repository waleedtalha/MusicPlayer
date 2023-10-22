package com.app.musicplayer.interator.livedata

import android.content.Context
import com.app.musicplayer.contentresolver.AlbumsContentResolver
import com.app.musicplayer.contentresolver.ArtistsContentResolver
import com.app.musicplayer.di.factory.contentresolver.ContentResolverFactory
import com.app.musicplayer.models.Album
import com.app.musicplayer.models.Artist
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Singleton
class ArtistsLiveData(
    @ApplicationContext context: Context,
    private val artistId: Long? = null,
    private val contentResolverFactory: ContentResolverFactory,
) : ContentProviderLiveData<ArtistsContentResolver, Artist>(context) {
    override val contentResolver by lazy { contentResolverFactory.getArtistsContentResolver(artistId) }
}