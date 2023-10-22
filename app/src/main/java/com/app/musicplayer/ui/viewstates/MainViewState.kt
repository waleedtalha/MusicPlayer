package com.app.musicplayer.ui.viewstates

import android.content.Context
import com.app.musicplayer.ui.base.BaseViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class MainViewState @Inject constructor(
    @ApplicationContext private val context: Context
) : BaseViewState()