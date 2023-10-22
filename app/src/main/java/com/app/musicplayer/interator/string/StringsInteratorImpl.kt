package com.app.musicplayer.interator.string

import android.content.Context
import com.app.musicplayer.core.baseobservable.BaseObservable
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class StringsInteratorImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : BaseObservable<StringsInteractor.Listener>(), StringsInteractor {
    override fun getString(stringRes: Int) = context.getString(stringRes)
}