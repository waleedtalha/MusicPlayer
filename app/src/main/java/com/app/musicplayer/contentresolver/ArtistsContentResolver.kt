package com.app.musicplayer.contentresolver

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.app.musicplayer.core.SelectionBuilder
import com.app.musicplayer.extentions.getLongValue
import com.app.musicplayer.extentions.getStringValue
import com.app.musicplayer.models.Artist

class ArtistsContentResolver(
    context: Context,
    private val artistId: Long? = null,
    private val name: String? = null
) :
    BaseContentResolver<Artist>(context) {
    override val uri: Uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI
    override val filterUri: Uri? = null

    override val selection: String
        get() {
            val selection = if (artistId != null)
                SelectionBuilder().addSelection(MediaStore.Audio.Artists._ID, artistId)
            else
                SelectionBuilder().addSelection(MediaStore.Audio.Artists.ARTIST, name)
            filter?.let { selection.addString("(${MediaStore.Audio.Artists.ARTIST} LIKE '%$filter%')") }
            return selection.build()
        }
    override val sortOrder: String? = null
    override val projection: Array<String> = arrayOf(
        MediaStore.Audio.Artists._ID,
        MediaStore.Audio.Artists.ARTIST,
        MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
        MediaStore.Audio.Artists.NUMBER_OF_ALBUMS
    )
    override val selectionArgs: Array<String>? = null

    override fun convertCursorToItem(cursor: Cursor) = Artist(
        id = cursor.getLongValue(MediaStore.Audio.Artists._ID),
        artistTitle = cursor.getStringValue(MediaStore.Audio.Artists.ARTIST) ?: "",
        tracksCount = cursor.getStringValue(MediaStore.Audio.Artists.NUMBER_OF_TRACKS) ?: "",
        albumsCount = cursor.getStringValue(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS) ?: "",
        albumId = 0L
    )
}