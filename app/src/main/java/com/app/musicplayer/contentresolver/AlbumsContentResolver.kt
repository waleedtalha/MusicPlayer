package com.app.musicplayer.contentresolver

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.app.musicplayer.core.SelectionBuilder
import com.app.musicplayer.extentions.getLongValue
import com.app.musicplayer.extentions.getStringValueOrNull
import com.app.musicplayer.models.Album

class AlbumsContentResolver(
    context: Context,
    private val albumId: Long? = null,
    private val name: String? = null
) :
    BaseContentResolver<Album>(context) {
    override val uri: Uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
    override val filterUri: Uri? = null

    override val selection: String
        get() {
            val selection = if (albumId != null)
                SelectionBuilder().addSelection(MediaStore.Audio.Albums._ID, albumId)
            else
                SelectionBuilder().addSelection(MediaStore.Audio.Albums.ALBUM, name)
            filter?.let { selection.addString("(${MediaStore.Audio.Albums.ALBUM} LIKE '%$filter%')") }
            selection.addString("(${MediaStore.Audio.Albums.ALBUM} NOT LIKE '${"Recordings"}%')")
            selection.addString("(${MediaStore.Audio.Albums.ALBUM} NOT LIKE '${"Recorded"}%')")
            return selection.build()
        }
    override val sortOrder: String? = null
    override val projection: Array<String> = arrayOf(
        MediaStore.Audio.Albums._ID,
        MediaStore.Audio.Albums.ALBUM_ID,
        MediaStore.Audio.Albums.ALBUM,
        MediaStore.Audio.Albums.NUMBER_OF_SONGS,
        MediaStore.Audio.Albums.ARTIST
    )
    override val selectionArgs: Array<String>? = null

    override fun convertCursorToItem(cursor: Cursor) = Album(
        id = cursor.getLongValue(MediaStore.Audio.Albums._ID),
        albumId = cursor.getLongValue(MediaStore.Audio.Albums.ALBUM_ID),
        albumTitle = cursor.getStringValueOrNull(MediaStore.Audio.Albums.ALBUM) ?: "",
        trackCount = cursor.getStringValueOrNull(MediaStore.Audio.Albums.NUMBER_OF_SONGS) ?: "",
        artist = cursor.getStringValueOrNull(MediaStore.Audio.Albums.ARTIST) ?: ""
    )
}