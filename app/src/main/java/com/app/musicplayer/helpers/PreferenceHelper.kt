package com.app.musicplayer.helpers

import android.content.Context
import android.content.SharedPreferences
import com.app.musicplayer.helpers.PreferenceHelper.PreferenceVariable.CURRENT_TRACK_PROGRESS
import com.app.musicplayer.helpers.PreferenceHelper.PreferenceVariable.CURRENT_TRACK_TOTAL_CURRENT
import com.app.musicplayer.helpers.PreferenceHelper.PreferenceVariable.EQUALIZER_BASS
import com.app.musicplayer.helpers.PreferenceHelper.PreferenceVariable.EQUALIZER_PITCH
import com.app.musicplayer.helpers.PreferenceHelper.PreferenceVariable.PLAY_SPEED
import com.app.musicplayer.helpers.PreferenceHelper.PreferenceVariable.REPEAT_TRACK
import com.app.musicplayer.helpers.PreferenceHelper.PreferenceVariable.SET_RINGTONE
import com.app.musicplayer.helpers.PreferenceHelper.PreferenceVariable.SHUFFLE_TRACK
import com.app.musicplayer.helpers.PreferenceHelper.PreferenceVariable.TRACK_ID_CURRENT
import com.app.musicplayer.helpers.PreferenceHelper.PreferenceVariable.TRACK_POSITION_CURRENT
import com.app.musicplayer.utils.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PreferenceHelper @Inject constructor(@ApplicationContext private val context: Context) {
    private val appPrefs: SharedPreferences =
        context.getSharedPreferences("music_player_pref", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = appPrefs.edit()

    object PreferenceVariable {
        const val REPEAT_TRACK = "repeat_track"
        const val SHUFFLE_TRACK = "shuffle_track"
        const val SET_RINGTONE = "set_ringtone"
        const val PLAY_SPEED = "play_speed"
        const val EQUALIZER_PITCH = "equalizer_pitch"
        const val EQUALIZER_BASS = "equalizer_bass"
        const val TRACK_ID_CURRENT = "current_track_id"
        const val TRACK_POSITION_CURRENT = "current_track_position"
        const val CURRENT_TRACK_PROGRESS = "current_track_progress"
        const val CURRENT_TRACK_TOTAL_CURRENT = "current_track_total_duration"
    }

    init {
        editor.apply()
    }

    var repeatTrack: String?
        get() = appPrefs.getString(REPEAT_TRACK, REPEAT_TRACK_OFF)
        set(repeatTrack) {
            editor.putString(REPEAT_TRACK, repeatTrack)
            editor.apply()
        }

    var shuffleTrack: String?
        get() = appPrefs.getString(SHUFFLE_TRACK, SHUFFLE_TRACK_OFF)
        set(shuffleTrack) {
            editor.putString(SHUFFLE_TRACK, shuffleTrack)
            editor.apply()
        }

    var setRingtone: String?
        get() = appPrefs.getString(SET_RINGTONE, PHONE_RINGTONE)
        set(shuffleTrack) {
            editor.putString(SET_RINGTONE, shuffleTrack)
            editor.apply()
        }
    var setPlaySpeed: String?
        get() = appPrefs.getString(PLAY_SPEED, PLAY_SPEED_1x)
        set(trackSpeed) {
            editor.putString(PLAY_SPEED, trackSpeed)
            editor.apply()
        }

    var setEqPitch: String?
        get() = appPrefs.getString(EQUALIZER_PITCH, EQUALIZER_PITCH_DEFAULT)
        set(pitch) {
            editor.putString(EQUALIZER_PITCH, pitch)
            editor.apply()
        }

    var setEqBass: String?
        get() = appPrefs.getString(EQUALIZER_BASS, EQUALIZER_BASS_DEFAULT)
        set(bass) {
            editor.putString(EQUALIZER_BASS, bass)
            editor.apply()
        }

    var currentTrackId: Long?
        get() = appPrefs.getLong(TRACK_ID_CURRENT, CURRENT_TRACK_ID)
        set(currentTrackId) {
            editor.putLong(TRACK_ID_CURRENT, currentTrackId ?: 0L)
            editor.apply()
        }
    var currentTrackPosition: Int?
        get() = appPrefs.getInt(TRACK_POSITION_CURRENT, CURRENT_TRACK_POSITION)
        set(currentTrackPosition) {
            editor.putInt(TRACK_POSITION_CURRENT, currentTrackPosition ?: 0)
            editor.apply()
        }
    var currentTrackTotalDuration: Int?
        get() = appPrefs.getInt(CURRENT_TRACK_TOTAL_CURRENT, TRACK_CURRENT_TOTAL_CURRENT)
        set(currentTrackTotalDuration) {
            editor.putInt(CURRENT_TRACK_TOTAL_CURRENT, currentTrackTotalDuration ?: 0)
            editor.apply()
        }
    var currentTrackProgress: Int?
        get() = appPrefs.getInt(CURRENT_TRACK_PROGRESS, TRACK_CURRENT_PROGRESS)
        set(currentTrackProgress) {
            editor.putInt(CURRENT_TRACK_PROGRESS, currentTrackProgress ?: 0)
            editor.apply()
        }

    fun clearPreference() {
        editor.clear()
        editor.apply()
    }
}