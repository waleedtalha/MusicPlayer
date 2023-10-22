package com.app.musicplayer.interator.tracks

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import com.app.musicplayer.contentresolver.TracksContentResolver
import com.app.musicplayer.interator.base.BaseInteratorImpl
import com.app.musicplayer.models.Track
import com.app.musicplayer.models.TrackCombinedData
import com.app.musicplayer.utils.getAudioFileContentUri
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TracksInteractorImpl @Inject constructor(
    private val disposable: CompositeDisposable,
    @ApplicationContext private val context: Context
) : BaseInteratorImpl<TracksInteractor.Listener>(), TracksInteractor {
    override fun deleteTrack(trackId: Long) {
    }

    override fun queryTrack(trackId: Long, callback: (Track?) -> Unit) {
        disposable.add(TracksContentResolver(context, trackId).queryItems { tracks ->
            tracks.let { callback.invoke(tracks.getOrNull(0)) }
        })
    }

    override fun queryTrackList(callback: (List<Track?>) -> Unit) {
        disposable.add(TracksContentResolver(context).queryItems { tracks ->
            tracks.let { callback.invoke(tracks) }
        })
    }

    override fun renameTrack(track:TrackCombinedData, newTitle: String) {
        val oldExtension = track.track.path?.substringAfterLast(".")
        val newDisplayName = "${newTitle.removeSuffix(".$oldExtension")}.$oldExtension"

        val values = ContentValues().apply {
            put(MediaStore.Audio.Media.TITLE, newTitle.substringAfterLast('.'))
            put(MediaStore.Audio.Media.DISPLAY_NAME, newDisplayName)
        }
        try {
            context.contentResolver.update(
                getAudioFileContentUri(track.track.id ?: 0L),
                values,
                null,
                null
            )
        } catch (e: Exception) {
//            renameSDCardSong(track)
            Log.wtf("rename exception",e.toString())
        }
    }
}