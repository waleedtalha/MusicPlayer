package com.app.musicplayer.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.app.musicplayer.extentions.sendIntent
import com.app.musicplayer.utils.DISMISS

class NotificationDismissedReceiver : BroadcastReceiver() {
    override fun onReceive(context : Context, intent: Intent?) {
        context.sendIntent(DISMISS)
    }

}