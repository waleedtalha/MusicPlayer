package com.app.musicplayer.di.factory.contentresolver

import com.app.musicplayer.contentresolver.AlbumsContentResolver
import com.app.musicplayer.contentresolver.ArtistsContentResolver
import com.app.musicplayer.contentresolver.TracksContentResolver

interface ContentResolverFactory {
    fun getTracksContentResolver(trackId: Long? = null, albumId: Long? = null, artistId: Long? = null) : TracksContentResolver
    fun getAlbumsContentResolver(albumId: Long? = null) : AlbumsContentResolver
    fun getArtistsContentResolver(artistId: Long? = null) : ArtistsContentResolver
}