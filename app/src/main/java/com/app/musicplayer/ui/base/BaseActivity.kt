package com.app.musicplayer.ui.base

import android.Manifest
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.audiofx.Equalizer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.musicplayer.R
import com.app.musicplayer.databinding.BsAddToPlaylistBinding
import com.app.musicplayer.databinding.BsEqualizerBinding
import com.app.musicplayer.databinding.BsPlaybackSpeedBinding
import com.app.musicplayer.databinding.BsRenameTrackBinding
import com.app.musicplayer.databinding.BsSetRingtoneBinding
import com.app.musicplayer.databinding.BsSleepTimerBinding
import com.app.musicplayer.databinding.PopupTrackPropertiesBinding
import com.app.musicplayer.db.entities.PlaylistEntity
import com.app.musicplayer.extentions.convertToSeekBarProgress
import com.app.musicplayer.extentions.formatMillisToHMS
import com.app.musicplayer.extentions.getPermissionString
import com.app.musicplayer.extentions.hasPermission
import com.app.musicplayer.extentions.isDarkMode
import com.app.musicplayer.extentions.isUnknownString
import com.app.musicplayer.extentions.sendIntent
import com.app.musicplayer.extentions.toast
import com.app.musicplayer.helpers.MediaPlayer.mPlayer
import com.app.musicplayer.helpers.PreferenceHelper
import com.app.musicplayer.interator.string.StringsInteractor
import com.app.musicplayer.models.TrackCombinedData
import com.app.musicplayer.ui.adapters.PlaylistsAdapter
import com.app.musicplayer.utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

abstract class BaseActivity<VM : BaseViewState> : AppCompatActivity(), BaseView<VM> {

    abstract override val viewState: VM
    abstract val contentView: View?
    lateinit var linearLayoutManager: RecyclerView.LayoutManager
    private var playerMenuCallBack: (String) -> Unit = {}
    private var trackMenuCallBack: (String) -> Unit = {}
    private lateinit var audioManager: AudioManager

    @Inject
    lateinit var playlistAdapter: PlaylistsAdapter

    private lateinit var volumeReceiver: BroadcastReceiver
    private val pitchValues = arrayOf(0.5f, 0.75f, 1f, 1.25f, 1.5f)
    private var pitch = 1f
    private val equalizer = mPlayer()?.audioSessionId?.let { Equalizer(0, it) }

    @Inject
    lateinit var disposables: CompositeDisposable

    @Inject
    lateinit var strings: StringsInteractor

    @Inject
    lateinit var prefs: PreferenceHelper

    var actionOnPermission: ((granted: Boolean) -> Unit)? = null
    private var deleteConfirmation: (Boolean) -> Unit = {}
    private var setRingtoneCallBack: (String) -> Unit = {}
    private var setPlaySpeedCallBack: (String) -> Unit = {}
    private var setRenameTrackCallBack: (String) -> Unit = {}
    private var setSleepTimerCallBack: (Int) -> Unit = {}
    private var setEqualizerCallBack: (String) -> Unit = {}
    private var addToPlaylistCallBack: (String) -> Unit = {}
    private var createPlaylistCallBack: (String) -> Unit = {}
    var isAskingPermissions = false
    var showSettingAlert: AlertDialog? = null
    var bassLevel: Int? = null
    var selectedPlaylistId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentView)
        linearLayoutManager = LinearLayoutManager(applicationContext)
        onSetup()
        viewState.apply {
            attach()
            errorEvent.observe(this@BaseActivity) {
                it.ifNew?.let(this@BaseActivity::showError)
            }

            finishEvent.observe(this@BaseActivity) {
                it.ifNew?.let { finish() }
            }

            messageEvent.observe(this@BaseActivity) {
                it.ifNew?.let(this@BaseActivity::showMessage)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewState.detach()
        disposables.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }

    override fun finish() {
        finishAndRemoveTask()
    }

    override fun showError(stringResId: Int) {
        applicationContext.toast(strings.getString(stringResId))
    }

    override fun showMessage(stringResId: Int) {
        applicationContext.toast(strings.getString(stringResId))
    }

    fun moveBack() {
        finish()
    }

    fun bsSetRingtone(setRingtoneCallBack: (String) -> Unit) {
        this.setRingtoneCallBack = setRingtoneCallBack
        var setRingtoneValue: String? = null
        val setRingtoneBottomSheet: BottomSheetDialog?
        setRingtoneBottomSheet = BottomSheetDialog(this, R.style.BottomSheetDialog)
        val binding = BsSetRingtoneBinding.inflate(LayoutInflater.from(this))
        setRingtoneBottomSheet.setContentView(binding.root)
        defaultCheckedRingtone(binding.setRingtoneGroup, prefs)
        binding.setRingtoneGroup.setOnCheckedChangeListener { group, checked ->
            val radioButton = group.findViewById<RadioButton>(checked)
            setRingtoneValue = radioButton.text.toString()
        }
        binding.cancelButton.setOnClickListener {
            if (setRingtoneBottomSheet.isShowing) {
                setRingtoneBottomSheet.dismiss()
            }
        }
        binding.doneButton.setOnClickListener {
            if (setRingtoneValue.equals(PHONE_RINGTONE) or setRingtoneValue.equals(ALARM_RINGTONE)) {
                prefs.setRingtone = setRingtoneValue
            }
            setRingtoneCallBack(DONE)
            if (setRingtoneBottomSheet.isShowing) {
                setRingtoneBottomSheet.dismiss()
            }
        }
        setRingtoneBottomSheet.show()
    }

    fun bsPlaySpeed(setSpeedCallBack: (String) -> Unit) {
        this.setPlaySpeedCallBack = setSpeedCallBack
        var playSpeedValue: String? = null
        val setRingtoneBottomSheet: BottomSheetDialog?
        setRingtoneBottomSheet = BottomSheetDialog(this, R.style.BottomSheetDialog).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        val binding = BsPlaybackSpeedBinding.inflate(LayoutInflater.from(this))
        setRingtoneBottomSheet.setContentView(binding.root)
        defaultCheckedPlaySpeed(binding.playSpeedGroup, prefs)
        binding.playSpeedGroup.setOnCheckedChangeListener { group, checked ->
            val radioButton = group.findViewById<RadioButton>(checked)
            playSpeedValue = radioButton.text.toString()
        }
        binding.cancelButton.setOnClickListener {
            if (setRingtoneBottomSheet.isShowing) {
                setRingtoneBottomSheet.dismiss()
            }
        }
        binding.doneButton.setOnClickListener {
            if (playSpeedValue.equals(PLAY_SPEED_0_5x) or playSpeedValue.equals(PLAY_SPEED_0_75x) or playSpeedValue.equals(
                    PLAY_SPEED_1x
                ) or playSpeedValue.equals(PLAY_SPEED_1_25x) or playSpeedValue.equals(
                    PLAY_SPEED_1_5x
                ) or playSpeedValue.equals(
                    PLAY_SPEED_2x
                )
            ) {
                prefs.setPlaySpeed = playSpeedValue
            }
            setPlaySpeedCallBack(DONE)
            if (setRingtoneBottomSheet.isShowing) {
                setRingtoneBottomSheet.dismiss()
            }
        }
        setRingtoneBottomSheet.show()
    }

    fun bsEqualizer(setEqualizerCallBack: (String) -> Unit) {
        this.setEqualizerCallBack = setEqualizerCallBack
        equalizer?.enabled = true
        val setRingtoneBottomSheet: BottomSheetDialog?
        setRingtoneBottomSheet = BottomSheetDialog(this, R.style.BottomSheetDialog).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        val binding = BsEqualizerBinding.inflate(LayoutInflater.from(this))
        setUpVolumeSeekbar(binding.soundBar)
        setUpPitchSeekbar(binding.pitchBar)
        setUpBassSeekbar(binding.baseBar)
        setRingtoneBottomSheet.setContentView(binding.root)
        binding.reset.setOnClickListener {
            binding.pitchBar.progress = 50
            binding.baseBar.progress = 1500
            prefs.setEqPitch = "1f"
            prefs.setEqBass = "0"
            val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val halfVolume = maxVolume / 2
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, halfVolume, 0)
        }
        binding.cancelButton.setOnClickListener {
            unregisterReceiver(volumeReceiver)
            if (setRingtoneBottomSheet.isShowing) {
                setRingtoneBottomSheet.dismiss()
            }
        }
        binding.doneButton.setOnClickListener {
            setEqualizerCallBack(DONE)
            unregisterReceiver(volumeReceiver)
            if (setRingtoneBottomSheet.isShowing) {
                setRingtoneBottomSheet.dismiss()
            }
        }
        setRingtoneBottomSheet.show()
    }

    private fun setUpVolumeSeekbar(seekBar: SeekBar) {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        seekBar.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        seekBar.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    progress, AudioManager.FLAG_SHOW_UI
                )
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        volumeReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, intent: Intent?) {
                if (intent?.action == VOLUME_CHANGED_ACTION) {
                    val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                    seekBar.progress = currentVolume
                }
            }
        }
        val filter = IntentFilter(VOLUME_CHANGED_ACTION)
        registerReceiver(volumeReceiver, filter)
    }

    private fun setUpPitchSeekbar(seekBar: SeekBar) {
        seekBar.progress =
            ((prefs.setEqPitch?.toFloat()!! - pitchValues[0]) / (pitchValues[pitchValues.size - 1] - pitchValues[0]) * 100).toInt()

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val pitchIndex = (progress / 100f * (pitchValues.size - 1)).toInt()
                pitch = if (pitchIndex == pitchValues.size - 1) {
                    pitchValues[pitchIndex]
                } else {
                    val minPitch = pitchValues[pitchIndex]
                    val maxPitch = pitchValues[pitchIndex + 1]
                    val pitchFraction =
                        (progress % (100f / (pitchValues.size - 1))) / (100f / (pitchValues.size - 1))
                    minPitch + (maxPitch - minPitch) * pitchFraction
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                prefs.setEqPitch = "${pitch}f"
            }
        })
    }

    private fun setUpBassSeekbar(seekBar: SeekBar) {
        val numberOfBands = equalizer?.numberOfBands
        val minBassLevel = equalizer?.bandLevelRange?.get(0)
        val maxBassLevel = equalizer?.bandLevelRange?.get(1)
        if (maxBassLevel != null) {
            seekBar.max = maxBassLevel - minBassLevel!!
        }
        seekBar.progress = prefs.setEqBass?.toInt()?.convertToSeekBarProgress() ?: 1500
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                bassLevel = minBassLevel?.plus(progress)
                for (band in 0 until numberOfBands!!) {
                    bassLevel?.toShort()?.let {
                        equalizer?.setBandLevel(band.toShort(), it)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                prefs.setEqBass = bassLevel.toString()
            }
        })
    }

    private fun defaultCheckedRingtone(ringtoneGroup: RadioGroup, prefs: PreferenceHelper) {
        for (count in 0 until ringtoneGroup.childCount) {
            val radioButton: RadioButton = ringtoneGroup.getChildAt(count) as RadioButton
            radioButton.let {
                when (it.text) {
                    prefs.setRingtone -> {
                        it.isChecked = true
                    }
                }
            }
        }
    }

    private fun defaultCheckedPlaySpeed(playSpeedGroup: RadioGroup, prefs: PreferenceHelper) {
        for (count in 0 until playSpeedGroup.childCount) {
            val radioButton: RadioButton = playSpeedGroup.getChildAt(count) as RadioButton
            radioButton.let {
                when (it.text) {
                    prefs.setPlaySpeed -> {
                        it.isChecked = true
                    }
                }
            }
        }
    }

    fun playerMenu(
        menu_btn: ImageView, menuCallBack: (String) -> Unit
    ) {
        this.playerMenuCallBack = menuCallBack
        val wrapper: Context = ContextThemeWrapper(this, R.style.popUpMenuMain)
        val popupMenuSelected = PopupMenu(wrapper, menu_btn)
        popupMenuSelected.inflate(R.menu.player_menu)
        popupMenuSelected.gravity = Gravity.END
        popupMenuSelected.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.ab_repeat -> {
                    playerMenuCallBack(AB_REPEAT_TRACK)
                }

                R.id.play_speed -> {
                    playerMenuCallBack(PLAY_SPEED_TRACK)
                }

                R.id.sleep_timer -> {
                    playerMenuCallBack(SLEEP_TIMER)
                }

                R.id.add_to_playlist -> {
                    playerMenuCallBack(ADD_TO_PLAYLIST)
                }

                R.id.share_track -> {
                    playerMenuCallBack(SHARE_TRACK)
                }

                R.id.set_track_as -> {
                    playerMenuCallBack(SET_TRACK_AS)
                }

                R.id.delete_track -> {
                    playerMenuCallBack(DELETE_TRACK)
                }

                R.id.settings -> {
                    playerMenuCallBack(SETTINGS)
                }
            }
            true
        }
        popupMenuSelected.show()

    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun launchGrantAllFilesDialog() {
        MaterialAlertDialogBuilder(this).setMessage(getString(R.string.access_storage_prompt))
            .setPositiveButton("OK") { _, _ ->
                if (!Environment.isExternalStorageManager()) {
                    launchGrantAllFilesIntent()
                }
            }.setCancelable(false).show()
    }

    private fun launchGrantAllFilesIntent() {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.addCategory("android.intent.category.DEFAULT")
            intent.data = Uri.parse("package:${this.packageName}")
            startActivityForResult(intent, 214)
        } catch (e: Exception) {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
            try {
                startActivity(intent)
            } catch (e: Exception) {
            }
        }
    }

    fun showTrackPropertiesDialog(track: TrackCombinedData) {
        val binding = PopupTrackPropertiesBinding.inflate(LayoutInflater.from(this))
        val view = binding.root
        val builder = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogTheme)
        builder.setView(view)
        if (isMPlus()) {
            if (this.isDarkMode()) {
                binding.path.setTextColor(getColor(R.color.white))
                binding.name.setTextColor(getColor(R.color.white))
                binding.duration.setTextColor(getColor(R.color.white))
                binding.artist.setTextColor(getColor(R.color.white))
            } else {
                binding.path.setTextColor(getColor(R.color.black))
                binding.name.setTextColor(getColor(R.color.black))
                binding.duration.setTextColor(getColor(R.color.black))
                binding.artist.setTextColor(getColor(R.color.black))
            }
        }
        binding.path.text = track.track.path ?: ""
        binding.name.text = track.track.path?.substringAfterLast("/")?.substringBeforeLast(".")
        binding.duration.text = formatMillisToHMS(track.track.duration ?: 0L)
        binding.artist.text = track.track.artist?.isUnknownString()
        builder.setTitle("Properties")
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun deleteConfirmationDialog(isSong: Boolean? = null, deleteConfirmation: (Boolean) -> Unit) {
        this.deleteConfirmation = deleteConfirmation
        val builder = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialogTheme)
        builder.setTitle("Delete")
        if (isSong == true) {
            builder.setMessage("Do you want to delete this song?")
        } else {
            builder.setMessage("Do you want to delete this playlist?")
        }

        builder.setPositiveButton("OK") { dialog, _ ->
            deleteConfirmation(true)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    fun bsAddToPlaylist(list: List<PlaylistEntity>, addToPlaylistCallBack: (String) -> Unit) {
        this.addToPlaylistCallBack = addToPlaylistCallBack
        val bsAddToPlaylist: BottomSheetDialog?
        bsAddToPlaylist = BottomSheetDialog(this, R.style.BottomSheetDialog).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        val binding = BsAddToPlaylistBinding.inflate(LayoutInflater.from(this))
        bsAddToPlaylist.setContentView(binding.root)
        playlistAdapter.apply {
            viewHolderType = PLAYLISTS_VT
            isListOnly = true
            isChecked = false
            selected_position = -1
            binding.playlistRv.layoutManager =
                LinearLayoutManager(this@BaseActivity, RecyclerView.VERTICAL, false)
            items = list
            binding.playlistRv.adapter = this
            setOnPlaylistSelectListener { playlistEntity ->
                selectedPlaylistId = playlistEntity.playlistId
                notifyDataSetChanged()
            }
        }
        binding.addButton.setOnClickListener {
            if (bsAddToPlaylist.isShowing && selectedPlaylistId != 0L) {
                addToPlaylistCallBack(selectedPlaylistId.toString())
                bsAddToPlaylist.dismiss()
            } else {
                toast("Select playlist first")
            }
        }
        binding.createPlaylist.setOnClickListener {
            addToPlaylistCallBack(CREATE_PLAYLIST)
            bsAddToPlaylist.dismiss()
        }
        binding.cancelButton.setOnClickListener {
            if (bsAddToPlaylist.isShowing) {
                bsAddToPlaylist.dismiss()
            }
        }
        bsAddToPlaylist.show()
    }

    fun bsCreatePlaylist(createPlaylistCallBack: (String) -> Unit) {
        this.createPlaylistCallBack = createPlaylistCallBack
        val setRingtoneBottomSheet: BottomSheetDialog?
        setRingtoneBottomSheet = BottomSheetDialog(this, R.style.BottomSheetDialog)
        val binding = BsRenameTrackBinding.inflate(LayoutInflater.from(this))
        setRingtoneBottomSheet.setContentView(binding.root)
        binding.renameButton.text = getString(R.string.create)
        binding.bsTitle.text = getString(R.string.create_playlist)
        binding.cancelButton.setOnClickListener {
            if (setRingtoneBottomSheet.isShowing) {
                setRingtoneBottomSheet.dismiss()
            }
        }
        binding.renameButton.setOnClickListener {
            createPlaylistCallBack(binding.etName.text.toString())
            if (setRingtoneBottomSheet.isShowing) {
                setRingtoneBottomSheet.dismiss()
            }
        }
        setRingtoneBottomSheet.show()
    }

    fun bsSleepTimer(setSleepTimerCallBack: (Int) -> Unit) {
        this.setSleepTimerCallBack = setSleepTimerCallBack
        var selectedTime = 0
        val setRingtoneBottomSheet: BottomSheetDialog?
        setRingtoneBottomSheet = BottomSheetDialog(this, R.style.BottomSheetDialog)
        val binding = BsSleepTimerBinding.inflate(LayoutInflater.from(this))
        setRingtoneBottomSheet.setContentView(binding.root)
        val displayedValues = (10..90 step 5).map { "$it mins" }.toTypedArray()
        binding.numberPicker.displayedValues = displayedValues
        binding.numberPicker.minValue = 0
        binding.numberPicker.maxValue = displayedValues.size - 1
        binding.numberPicker.value = 0
        binding.numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            selectedTime = newVal
        }
        binding.setTimer.setOnClickListener {
            setSleepTimerCallBack(selectedTime)
            if (setRingtoneBottomSheet.isShowing) {
                setRingtoneBottomSheet.dismiss()
            }
        }
        binding.cancelButton.setOnClickListener {
            if (setRingtoneBottomSheet.isShowing) {
                setRingtoneBottomSheet.dismiss()
            }
        }
        setRingtoneBottomSheet.show()
    }

    fun bsRenameTrack(name: String, setRenameTrackCallBack: (String) -> Unit) {
        this.setRenameTrackCallBack = setRenameTrackCallBack
        val setRingtoneBottomSheet: BottomSheetDialog?
        setRingtoneBottomSheet = BottomSheetDialog(this, R.style.BottomSheetDialog)
        val binding = BsRenameTrackBinding.inflate(LayoutInflater.from(this))
        setRingtoneBottomSheet.setContentView(binding.root)
        binding.etName.setText(name.substringBeforeLast("."))
        binding.cancelButton.setOnClickListener {
            if (setRingtoneBottomSheet.isShowing) {
                setRingtoneBottomSheet.dismiss()
            }
        }
        binding.renameButton.setOnClickListener {
            setRenameTrackCallBack(binding.etName.text.toString())
            if (setRingtoneBottomSheet.isShowing) {
                setRingtoneBottomSheet.dismiss()
            }
        }
        if (isRPlus()) {
            if (Environment.isExternalStorageManager()) {
                setRingtoneBottomSheet.show()
            } else {
                launchGrantAllFilesDialog()
            }
        }
    }

    fun showTrackMenu(
        view: View,
        isRecent: Boolean? = false,
        isPlaylist: Boolean? = false,
        isPlaylistSong: Boolean? = false,
        menuCallBack: (String) -> Unit
    ) {
        this.trackMenuCallBack = menuCallBack
        val wrapper: Context = ContextThemeWrapper(this, R.style.popUpMenuMain)
        val popupMenuSelected = PopupMenu(wrapper, view)
        popupMenuSelected.inflate(R.menu.track_menu)
        popupMenuSelected.gravity = Gravity.END
        popupMenuSelected.menu.findItem(R.id.rename).isVisible = isRecent == false
        if (isPlaylist == true) {
            popupMenuSelected.menu.findItem(R.id.play).isVisible = false
            popupMenuSelected.menu.findItem(R.id.add_to_playlist).isVisible = false
            popupMenuSelected.menu.findItem(R.id.share).isVisible = false
            popupMenuSelected.menu.findItem(R.id.rename).isVisible = false
            popupMenuSelected.menu.findItem(R.id.properties).isVisible = false
            popupMenuSelected.menu.findItem(R.id.delete).title = "Delete Playlist"
        } else if (isPlaylistSong == true) {
            popupMenuSelected.menu.findItem(R.id.add_to_playlist).isVisible = false
            popupMenuSelected.menu.findItem(R.id.delete).title = "Remove from Playlist"
        }
        popupMenuSelected.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.play -> {
                    trackMenuCallBack(PLAY_TRACK)
                }

                R.id.add_to_playlist -> {
                    trackMenuCallBack(ADD_TO_PLAYLIST)
                }

                R.id.share -> {
                    trackMenuCallBack(SHARE_TRACK)
                }

                R.id.delete -> {
                    trackMenuCallBack(DELETE_TRACK)
                }

                R.id.rename -> {
                    trackMenuCallBack(RENAME_TRACK)
                }

                R.id.properties -> {
                    trackMenuCallBack(PROPERTIES_TRACK)
                }
            }
            true
        }
        popupMenuSelected.show()
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun handlePermission(permissionId: Int, callback: (granted: Boolean) -> Unit) {
        actionOnPermission = null
        if (hasPermission(permissionId)) {
            callback(true)
        } else {
            isAskingPermissions = true
            actionOnPermission = callback
            ActivityCompat.requestPermissions(
                this, arrayOf(getPermissionString(permissionId)), GENERIC_PERMISSION_HANDLER
            )
        }
    }

    fun handleNotificationPermission(callback: (granted: Boolean) -> Unit) {
        if (!isTiramisuPlus()) {
            callback(true)
        } else {
            handlePermission(PERMISSION_POST_NOTIFICATIONS) { granted ->
                callback(granted)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        isAskingPermissions = false
        if (requestCode == GENERIC_PERMISSION_HANDLER) {
            for (i in permissions.indices) {
                val per: String = permissions[i]
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    val showRationale = shouldShowRequestPermissionRationale(per)
                    if (!showRationale) {
                        val builder = AlertDialog.Builder(this@BaseActivity)
                        builder.setTitle("App Permission")
                            .setMessage(R.string.access_storage_from_settings)
                            .setPositiveButton(
                                "Open Settings"
                            ) { _, _ ->
                                val intent =
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts(
                                    "package",
                                    packageName, null
                                )
                                intent.data = uri
                                startActivityForResult(
                                    intent,
                                    OPEN_SETTINGS
                                )
                                finish()
                            }
                        showSettingAlert = builder.setCancelable(false).create()
                        showSettingAlert?.show()
                    } else {
                        ActivityCompat.requestPermissions(
                            this, arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_MEDIA_IMAGES
                            ), 0
                        )
                    }
                } else {
                    actionOnPermission?.invoke(grantResults[0] == 0)
                }
            }
        }
//        else if (requestCode == 0) {
//            for (i in permissions.indices) {
//                val per: String = permissions[i]
//                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
//                    val showRationale = shouldShowRequestPermissionRationale(per)
//                    if (!showRationale) {
//                        //user clicked on never ask again
//                        val builder = AlertDialog.Builder(this@BaseActivity)
//                        builder.setTitle("App Permission")
//                            .setMessage(R.string.access_storage_from_settings)
//                            .setPositiveButton(
//                                "Open Settings"
//                            ) { _, _ ->
//                                val intent =
//                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                                val uri = Uri.fromParts(
//                                    "package",
//                                    packageName, null
//                                )
//                                intent.data = uri
//                                startActivityForResult(
//                                    intent,
//                                    OPEN_SETTINGS
//                                )
//                                finish()
//                            }
//                        showSettingAlert = builder.setCancelable(false).create()
//                        showSettingAlert?.show()
//                    } else {
//                        ActivityCompat.requestPermissions(
//                            this, arrayOf(
//                                Manifest.permission.READ_EXTERNAL_STORAGE,
//                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                                Manifest.permission.READ_MEDIA_IMAGES
//                            ), 0
//                        )
//                    }
//                } else {
//                    toast("granted")
//                }
//            }
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DELETE_PLAYING_TRACK && resultCode == RESULT_OK) {
            sendIntent(NEXT)
        }
    }

    open fun <VS : BaseViewState> onFragmentSetup(fragment: BaseFragment<VS>) {}

}