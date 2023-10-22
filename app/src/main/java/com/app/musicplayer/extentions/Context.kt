package com.app.musicplayer.extentions

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.app.musicplayer.services.MusicService
import com.app.musicplayer.utils.*
import com.app.musicplayer.utils.isOnMainThread
import java.util.*
import java.util.concurrent.TimeUnit


fun Context.sendIntent(action: String) {
    Intent(this,MusicService::class.java).apply {
        this.action = action
        try {
//            if (isOreoPlus()) {
//                startForegroundService(this)
//            } else {
                startService(this)
//            }
        } catch (ignored: java.lang.Exception) {
        }
    }
}
fun createWaveform(): IntArray {
    val random = Random(System.currentTimeMillis())
    val length = 50
    val values = IntArray(length)
    var maxValue = 0
    for (i in 0 until length) {
        val newValue: Int = 5 + random.nextInt(50)
        if (newValue > maxValue) {
            maxValue = newValue
        }
        values[i] = newValue
    }
    return values
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Context.showKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
    inputMethodManager!!.toggleSoftInputFromWindow(
        view.applicationWindowToken,
        InputMethodManager.SHOW_FORCED,
        0
    )
}

fun Context.isDarkMode(): Boolean {
    val darkModeFlag = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return darkModeFlag == Configuration.UI_MODE_NIGHT_YES
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun Context.hasPermission(permId: Int) = ContextCompat.checkSelfPermission(
    this,
    getPermissionString(permId)
) == PackageManager.PERMISSION_GRANTED

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun getPermissionString(id: Int) = when (id) {
    PERMISSION_READ_STORAGE -> Manifest.permission.READ_EXTERNAL_STORAGE
    PERMISSION_WRITE_STORAGE -> Manifest.permission.WRITE_EXTERNAL_STORAGE
    PERMISSION_READ_MEDIA_AUDIOS -> Manifest.permission.READ_MEDIA_AUDIO
    PERMISSION_POST_NOTIFICATIONS -> Manifest.permission.POST_NOTIFICATIONS
    else -> ""
}
fun Context.toast(id: Int, length: Int = Toast.LENGTH_SHORT) {
    toast(getString(id), length)
}

fun Context.toast(msg: String, length: Int = Toast.LENGTH_SHORT) {
    try {
        if (isOnMainThread()) {
            doToast(this, msg, length)
        } else {
            Handler(Looper.getMainLooper()).post {
                doToast(this, msg, length)
            }
        }
    } catch (_: java.lang.Exception) {
    }
}

private fun doToast(context: Context, message: String, length: Int) {
    if (context is Activity) {
        if (!context.isFinishing && !context.isDestroyed) {
            Toast.makeText(context, message, length).show()
        }
    } else {
        Toast.makeText(context, message, length).show()
    }
}

fun formatMillisToHMS(milliseconds: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
    return if (hours > 0) {
        val minutes =
            TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(hours)
        val seconds =
            TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(minutes) - TimeUnit.HOURS.toSeconds(
                hours
            )
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        val seconds =
            TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(minutes)
        String.format("%02d:%02d", minutes, seconds)
    }
}


val Context.notificationManager: NotificationManager
    get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager