package com.app.musicplayer.helpers

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.provider.BaseColumns
import android.provider.MediaStore
import android.provider.Settings
import com.app.musicplayer.R
import com.app.musicplayer.extentions.toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object RingtoneManager {
    fun setRingtone(context: Context, trackId: Long) {
        val uri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            trackId
        )
        val resolver = context.contentResolver

        try {
            val cursor = resolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.MediaColumns.TITLE),
                BaseColumns._ID + "=?",
                arrayOf(trackId.toString()), null
            )
            cursor.use { cursorSong ->
                if (cursorSong != null && cursorSong.count == 1) {
                    cursorSong.moveToFirst()
                    Settings.System.putString(resolver, Settings.System.RINGTONE, uri.toString())
                    val message = context
                        .getString(R.string.x_has_been_set_as_ringtone, cursorSong.getString(0))
                    context.toast(message)
                }
            }
        } catch (ignored: SecurityException) {
        }
    }

    fun setAlarmTone(context: Context, trackPath: String) {
        RingtoneManager.setActualDefaultRingtoneUri(
            context,
            RingtoneManager.TYPE_ALARM,
            Uri.parse(trackPath)
        )
        context.toast("Set default alarm tone")
    }

    fun requiresDialog(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(context)) {
                return true
            }
        }
        return false
    }

    fun showDialog(context: Context) {
        return MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialogTheme)
            .setTitle(R.string.set_ringtone)
            .setMessage(R.string.dialog_message_set_ringtone)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                intent.data = Uri.fromParts("package", context.packageName, null)
                context.startActivity(intent)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .create().show()
    }
}