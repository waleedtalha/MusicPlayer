package com.app.musicplayer.ui.viewstates

import android.content.Context
import androidx.lifecycle.LiveData
import com.app.musicplayer.models.Track
import com.app.musicplayer.ui.list.ListViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewState @Inject constructor(@ApplicationContext private val context: Context) :
    ListViewState<Track>() {
    override fun getItemsObservable(callback: (LiveData<List<Track>>) -> Unit) {
    }
}