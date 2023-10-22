package com.app.musicplayer.extentions

import com.app.musicplayer.services.MusicService
import java.util.*

fun Int.getFormattedDuration(forceShowHours: Boolean = false): String {
    val sb = StringBuilder(8)
    val hours = this / 3600
    val minutes = this % 3600 / 60
    val seconds = this % 60

    if (this >= 3600) {
        sb.append(String.format(Locale.getDefault(), "%02d", hours)).append(":")
    } else if (forceShowHours) {
        sb.append("0:")
    }

    sb.append(String.format(Locale.getDefault(), "%02d", minutes))
    sb.append(":").append(String.format(Locale.getDefault(), "%02d", seconds))
    return sb.toString()
}

fun Int.shuffleTrack(): Int {
    val shufflePosition = mutableListOf<Int>().apply {
        addAll(0 until this@shuffleTrack)
        shuffle()
    }
    return shufflePosition.indexOf(MusicService.positionTrack)
}

fun Int.getTimerMinutes(): Int {
    when (this) {
        0 -> return 10
        1 -> return 15
        2 -> return 20
        3 -> return 25
        4 -> return 30
        5 -> return 35
        6 -> return 40
        7 -> return 45
        8 -> return 50
        9 -> return 55
        10 -> return 60
        11 -> return 65
        12 -> return 70
        13 -> return 75
        14 -> return 80
        15 -> return 85
        16 -> return 90
        else -> return 10
    }
}
fun Int.convertToSeekBarProgress(): Int {
    return if (this < 0) {
        val positiveValue = this + 1500
        (positiveValue.toFloat() / 1500 * 1500).toInt()
    } else {
        (this.toFloat() / 1500 * 1500 + 1500).toInt()
    }
}