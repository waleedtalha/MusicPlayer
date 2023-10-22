package com.app.musicplayer.interator.player

import android.content.Context
import com.app.musicplayer.contentresolver.TracksContentResolver
import com.app.musicplayer.helpers.RingtoneManager
import com.app.musicplayer.models.Track
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class PlayerInteractorImpl @Inject constructor(
    private val disposable: CompositeDisposable,
    @ApplicationContext private val context: Context
) : PlayerInteractor {
    override fun deleteTrack(trackId: Long) {
    }
    override fun queryTrackList(callback: (List<Track?>) -> Unit) {
        disposable.add(TracksContentResolver(context).queryItems { tracks ->
            tracks.let { callback.invoke(tracks) }
        })
    }
    override fun setPhoneRingtone(context: Context, trackId: Long) {
        if (RingtoneManager.requiresDialog(context)) {
            RingtoneManager.showDialog(context)
        } else {
            RingtoneManager.setRingtone(context, trackId)
        }
    }

    override fun setAlarmTone(context: Context, trackPath: String) {
        if (RingtoneManager.requiresDialog(context)) {
            RingtoneManager.showDialog(context)
        } else {
            RingtoneManager.setAlarmTone(context, trackPath)
        }
    }
}