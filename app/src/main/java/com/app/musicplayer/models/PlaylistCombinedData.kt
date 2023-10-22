package com.app.musicplayer.models

import android.view.View
import com.app.musicplayer.db.entities.PlaylistEntity

data class PlaylistCombinedData(val playlist: PlaylistEntity, val position: Int,val view: View?=null)