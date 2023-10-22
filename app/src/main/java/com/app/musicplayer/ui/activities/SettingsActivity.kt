package com.app.musicplayer.ui.activities

import android.view.View
import androidx.activity.viewModels
import com.app.musicplayer.databinding.ActivitySettingsBinding
import com.app.musicplayer.helpers.MediaPlayer.setEqualizerPitch
import com.app.musicplayer.ui.base.BaseActivity
import com.app.musicplayer.ui.viewstates.SettingsViewState
import com.app.musicplayer.utils.DONE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : BaseActivity<SettingsViewState>() {
    private val binding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }
    override val viewState: SettingsViewState by viewModels()
    override val contentView: View by lazy { binding.root }

    override fun onSetup() {
        setUpClicks()
    }

    private fun setUpClicks() {
        binding.apply {
            moveBack.setOnClickListener {
                finish()
            }
            eqSound.setOnClickListener {
                bsEqualizer {eqCallBack->
                    when(eqCallBack){
                        DONE->setEqualizerPitch(prefs.setEqPitch?.toFloat()?:1f)
                    }
                }
            }
        }
    }
}