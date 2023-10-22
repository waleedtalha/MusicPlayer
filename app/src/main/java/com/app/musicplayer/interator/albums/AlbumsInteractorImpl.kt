package com.app.musicplayer.interator.albums

import android.content.Context
import com.app.musicplayer.contentresolver.AlbumsContentResolver
import com.app.musicplayer.interator.base.BaseInteratorImpl
import com.app.musicplayer.models.Album
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumsInteractorImpl @Inject constructor(
    private val disposable: CompositeDisposable,
    @ApplicationContext private val context: Context
) : BaseInteratorImpl<AlbumsInterator.Listener>(), AlbumsInterator {
    override fun deleteAlbum(albumId: Long) {
    }

    override fun queryAlbum(albumId: Long, callback: (Album?) -> Unit) {
        disposable.add(AlbumsContentResolver(context, albumId).queryItems { albums ->
            albums.let { callback.invoke(albums.getOrNull(0)) }
        })
    }
}