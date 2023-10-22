package com.app.musicplayer.models

import android.view.View
import com.app.musicplayer.db.entities.RecentTrackEntity

data class RecentTrackCombinedData(val track: RecentTrackEntity, val position: Int,val view: View?=null)
