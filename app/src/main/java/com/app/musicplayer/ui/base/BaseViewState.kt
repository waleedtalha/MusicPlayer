package com.app.musicplayer.ui.base

import androidx.lifecycle.ViewModel
import com.app.musicplayer.core.utils.DataLiveEvent
import com.app.musicplayer.core.utils.LiveEvent

open class BaseViewState : ViewModel() {
    val finishEvent = LiveEvent()
    val errorEvent = DataLiveEvent<Int>()
    val messageEvent = DataLiveEvent<Int>()

    open fun attach() {}

    open fun detach() {}
}