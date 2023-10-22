package com.app.musicplayer.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.app.musicplayer.extentions.sendIntent
import com.app.musicplayer.utils.*

class ControlActionsListener : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (val action = intent.action) {
            PREVIOUS, PLAYPAUSE, NEXT, FINISH, DISMISS -> context.sendIntent(action)
        }
    }
}