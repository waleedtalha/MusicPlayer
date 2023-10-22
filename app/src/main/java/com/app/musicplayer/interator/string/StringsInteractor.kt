package com.app.musicplayer.interator.string

import androidx.annotation.StringRes
import com.app.musicplayer.interator.base.BaseInterator

interface StringsInteractor : BaseInterator<StringsInteractor.Listener> {
    interface Listener

    fun getString(@StringRes stringRes: Int): String
}