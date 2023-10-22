package com.app.musicplayer.interator.albums

import com.app.musicplayer.interator.base.BaseInterator
import com.app.musicplayer.models.Album

interface AlbumsInterator : BaseInterator<AlbumsInterator.Listener> {
    interface Listener

    fun deleteAlbum(albumId: Long)

    fun queryAlbum(albumId: Long, callback: (Album?) -> Unit)
}