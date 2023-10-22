package com.app.musicplayer.utils

import android.content.ContentUris
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.provider.MediaStore
import androidx.annotation.ChecksSdkIntAtLeast

//room db
const val ROOM_DB_VERSION = 1
const val ROOM_DB_NAME = "music_player_db"

const val ALL_TRACKS_VT = 0
const val ALBUMS_VT = 1
const val ARTISTS_VT = 2
const val RECENT_TRACK_VT = 3
const val FAVORITES_VT = 4
const val PLAYLISTS_VT = 5
const val VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION"

private const val PATH = "com.app.musicplayer.action."
const val DISMISS = PATH + "DISMISS"
const val FINISH = PATH + "FINISH"
const val PREVIOUS = PATH + "PREVIOUS"
const val PLAYPAUSE = PATH + "PLAYPAUSE"
const val NEXT = PATH + "NEXT"
const val INIT = PATH + "INIT"
const val FINISH_IF_NOT_PLAYING = PATH + "FINISH_IF_NOT_PLAYING"
const val NOTIFICATION_DISMISSED = PATH + "NOTIFICATION_DISMISSED"
const val PROGRESS_CONTROLS_ACTION = PATH + "PROGRESS_CONTROLS"
const val NEXT_PREVIOUS_ACTION = PATH + "NEXT_PREVIOUS_ACTION"
const val PLAY_PAUSE_ACTION = PATH + "PLAY_PAUSE_ACTION"
const val DISMISS_PLAYER_ACTION = PATH + "dismiss_player_action"
const val TRACK_COMPLETE_ACTION = PATH + "track_complete_action"
const val SET_PROGRESS = PATH + "SET_PROGRESS"

const val DISMISS_PLAYER = "dismiss_player"
const val GET_CURRENT_POSITION = "track_position"
const val GET_TRACK_DURATION = "track_duration"
const val PLAY_PAUSE_ICON = "play_pause_icon"
const val COMPLETE_CALLBACK = "complete"
const val PROGRESS = "progress"
const val REPEAT_TRACK_ON = "repeat_on"
const val REPEAT_TRACK_OFF = "repeat_off"
const val SHUFFLE_TRACK_ON = "shuffle_on"
const val SHUFFLE_TRACK_OFF = "shuffle_off"
const val PHONE_RINGTONE = "Phone Ringtone"
const val PLAY_SPEED_0_5x = "0.5x"
const val PLAY_SPEED_0_75x = "0.75x"
const val PLAY_SPEED_1x = "1x (Normal)"
const val PLAY_SPEED_1_25x = "1.25x"
const val PLAY_SPEED_1_5x = "1.5x"
const val PLAY_SPEED_2x = "2x"
const val EQUALIZER_PITCH_DEFAULT = "1f"
const val EQUALIZER_BASS_DEFAULT = "0"
const val CURRENT_TRACK_ID = 0L
const val CURRENT_TRACK_POSITION = 0
const val TRACK_CURRENT_PROGRESS = 0
const val TRACK_CURRENT_TOTAL_CURRENT = 0
const val ALARM_RINGTONE = "Alarm tone"
const val SHARE_TRACK = "share_track"
const val AB_REPEAT_TRACK = "ab_repeat_track"
const val PLAY_SPEED_TRACK = "play_speed_track"
const val SLEEP_TIMER = "sleep_timer"
const val PLAY_TRACK = "play_track"
const val ADD_TO_PLAYLIST = "add_to_playlist"
const val CREATE_PLAYLIST = "create_playlist"
const val RENAME_TRACK = "rename_track"
const val PROPERTIES_TRACK = "properties_track"
const val SET_TRACK_AS = "set_as"
const val DELETE_TRACK = "delete_track"
const val SETTINGS = "settings"
const val DONE = "done"
const val GENERIC_PERMISSION_HANDLER = 1
const val DELETE_PLAYING_TRACK = 21
const val DELETE_TRACK_CODE = 22

const val PERMISSION_READ_STORAGE = 2
const val PERMISSION_WRITE_STORAGE = 3
const val PERMISSION_READ_MEDIA_AUDIOS = 4
const val PERMISSION_POST_NOTIFICATIONS = 5
const val OPEN_SETTINGS = 6

const val POSITION = "position"
const val FROM_MINI_PLAYER = "mini_player"
const val PLAYER_LIST = "player_list"
const val FROM_ALL_SONG = "from_all_song"
const val FROM_RECENT = "from_recent"
const val FROM_FAVORITE = "from_favorite"
const val FROM_ALBUM_LIST = "from_album_list"
const val FROM_ARTIST_LIST = "from_artist_list"
const val FROM_PLAYLIST = "from_playlist"
const val TRACK_ID = "track_id"
const val ALBUM_ID = "album_id"
const val ARTIST_ID = "artist_id"
const val SONGS_IN_ARTIST = "number_of_tracks"
const val ALBUMS_IN_ARTIST = "number_of_albums"
const val ALBUM_TITLE = "album_title"
const val ARTIST_TITLE = "artist_title"
const val NEXT_PREVIOUS_TRACK_ID = "next_prev_track_id"
const val TRACK_COMPLETED = "track_complete"
const val TRACK_ID_SERVICE = "track_id_service"
const val PLAYLIST_ID = "playlist_id"
const val PLAYLIST_NAME = "playlist_name"
const val SHORT_ANIMATION_DURATION = 150L

val artworkUri = Uri.parse("content://media/external/audio/albumart")

fun getPermissionToRequest() =
    if (isTiramisuPlus()) PERMISSION_READ_MEDIA_AUDIOS else PERMISSION_WRITE_STORAGE

fun isMPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
//check if device is running on Android 11 or higher
fun isRPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

//check if device is running on Android 10 or higher
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
fun isQPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

//check if device is running on Android 12 or higher
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.TIRAMISU)
fun isTiramisuPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

//check if device is running on Android 8 or higher (for notification)
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun isSPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
fun isOnMainThread() = Looper.myLooper() == Looper.getMainLooper()

fun ensureBackgroundThread(callback: () -> Unit) {
    if (isOnMainThread()) {
        Thread {
            callback()
        }.start()
    } else {
        callback()
    }
}
fun getAudioFileContentUri(id: Long): Uri {
    val baseUri = if (isQPlus()) {
        MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else {
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }

    return ContentUris.withAppendedId(baseUri, id)
}
