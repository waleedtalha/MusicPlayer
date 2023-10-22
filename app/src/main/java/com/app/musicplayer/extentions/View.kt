package com.app.musicplayer.extentions

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.app.musicplayer.R
import com.app.musicplayer.helpers.MediaPlayer
import com.app.musicplayer.utils.SHORT_ANIMATION_DURATION

fun View.beInvisibleIf(beInvisible: Boolean) = if (beInvisible) beInvisible() else beVisible()

fun View.beVisibleIf(beVisible: Boolean) = if (beVisible) beVisible() else beGone()

fun View.beGoneIf(beGone: Boolean) = beVisibleIf(!beGone)

fun View.beInvisible() {
    visibility = View.INVISIBLE
}

fun View.beVisible() {
    visibility = View.VISIBLE
}

fun View.beGone() {
    visibility = View.GONE
}

fun View.fadeIn() {
    animate().alpha(1f).setDuration(SHORT_ANIMATION_DURATION).withStartAction { beVisible() }
        .start()
}

fun View.fadeOut() {
    animate().alpha(0f).setDuration(SHORT_ANIMATION_DURATION).withEndAction { beGone() }.start()
}

fun ImageView.setSelectedTint(context: Context) {
    this.setColorFilter(ContextCompat.getColor(context, R.color.purple))
}

fun ImageView.setUnSelectedTint(context: Context) {
    if (context.isDarkMode()) {
        this.setColorFilter(ContextCompat.getColor(context, R.color.white))
    } else {
        this.setColorFilter(ContextCompat.getColor(context, R.color.black))
    }
}

fun ImageView.updateFavoriteIcon(context: Context, isFav: Boolean) {
    if (isFav) {
        this.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite))
    } else {
        if (context.isDarkMode()) {
            this.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_border_white))
        }else{
            this.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_border))
        }
    }
}

fun ImageView.updatePlayIcon(context: Context, isPause: Boolean) {
    if (isPause) {
        this.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_play
            )
        )
    } else {
        this.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_pause
            )
        )
    }
}

fun updatePlayPauseDrawable(playPauseIcon: ImageView, context: Context) {
    if (MediaPlayer.isPlaying()) {
        playPauseIcon.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_pause
            )
        )
    } else {
        playPauseIcon.setImageDrawable(
            ContextCompat.getDrawable(
                context,
                R.drawable.ic_play
            )
        )
    }
}