package com.app.musicplayer.extentions

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.app.musicplayer.utils.PLAY_SPEED_0_5x
import com.app.musicplayer.utils.PLAY_SPEED_0_75x
import com.app.musicplayer.utils.PLAY_SPEED_1_25x
import com.app.musicplayer.utils.PLAY_SPEED_1_5x
import com.app.musicplayer.utils.PLAY_SPEED_1x
import com.app.musicplayer.utils.PLAY_SPEED_2x
import com.app.musicplayer.utils.artworkUri
import java.io.File

fun String.getThumbnailUri(): String {
    return if (this == "") {
        ""
    } else {
        val coverUri = ContentUris.withAppendedId(artworkUri, this.toLong())
        coverUri.toString()
    }
}

fun String.isUnknownString(): String {
    if (this == MediaStore.UNKNOWN_STRING) {
        return this.substringAfterLast("<").substringBeforeLast(">")
    }
    return this
}

fun String.playBackSpeed(): Float {
    when (this) {
        PLAY_SPEED_0_5x -> return 0.5f
        PLAY_SPEED_0_75x -> return 0.75f
        PLAY_SPEED_1x -> return 1f
        PLAY_SPEED_1_25x -> return 1.25f
        PLAY_SPEED_1_5x -> return 1.5f
        PLAY_SPEED_2x -> return 2f
    }
    return 1f
}

fun String.shareTrack(context: Context) {
    val shareIntent = Intent(Intent.ACTION_SEND)
    shareIntent.type = "audio/*"
    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(this))
    shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this audio file!")
    context.startActivity(Intent.createChooser(shareIntent, "Share Track"))
}

fun excludeMessagesAppRecordings(): String {
    val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
    return File(musicDir, "Messenger/Recorded").absolutePath
}

fun excludeRecorderAppRecordings(): String {
    val musicDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
    return File(musicDir, "Recordings").absolutePath
}