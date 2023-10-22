package com.app.musicplayer.extentions

import android.app.Activity
import android.content.ContentUris
import android.os.Build
import android.provider.MediaStore
import android.view.View
import androidx.annotation.RequiresApi

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Activity.showKeyboard() {
    showKeyboard(currentFocus ?: View(this))
}

@RequiresApi(Build.VERSION_CODES.R)
fun Activity.deleteTrack(requestCode:Int,trackId:Long) {
    val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    val newUri = ContentUris.withAppendedId(uri, trackId)
    val deleteRequest = MediaStore.createDeleteRequest(contentResolver, arrayListOf(newUri)).intentSender
    startIntentSenderForResult(deleteRequest, requestCode,null, 0, 0, 0)
}
