package com.app.musicplayer.di.factory.contentresolver

import android.content.Context
import com.app.musicplayer.contentresolver.AlbumsContentResolver
import com.app.musicplayer.contentresolver.ArtistsContentResolver
import com.app.musicplayer.contentresolver.TracksContentResolver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ContentResolverFactoryImpl @Inject constructor(@ApplicationContext private val context: Context) :
    ContentResolverFactory {
    override fun getTracksContentResolver(trackId: Long?, albumId: Long?, artistId:Long?): TracksContentResolver =
        TracksContentResolver(context, trackId, albumId, artistId)

    override fun getAlbumsContentResolver(albumId: Long?): AlbumsContentResolver =
        AlbumsContentResolver(context, albumId)

    override fun getArtistsContentResolver(artistId: Long?): ArtistsContentResolver =
        ArtistsContentResolver(context, artistId)
}