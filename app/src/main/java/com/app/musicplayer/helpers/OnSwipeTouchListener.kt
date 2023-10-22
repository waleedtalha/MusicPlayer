package com.app.musicplayer.helpers

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

internal open class OnSwipeTouchListener(c: Context?) :
    View.OnTouchListener {
    private val gestureDetector: GestureDetector
    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(motionEvent)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD: Int = 100
        private val SWIPE_VELOCITY_THRESHOLD: Int = 100
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }
        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            try {
                val diffY = e2.y - e1.y
                val diffX = e2.x - e1.x
                if (abs(diffX) < abs(diffY)) {
                    if (abs(diffY) > SWIPE_THRESHOLD && abs(
                            velocityY
                        ) > SWIPE_VELOCITY_THRESHOLD
                    ) {
                        if (diffY < 0) {
//                            onSwipeUp()
                        } else {
                            onSwipeDown()
                        }
                    }
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
            return false
        }
    }
    open fun onSwipeDown() {}

    init {
        gestureDetector = GestureDetector(c, GestureListener())
    }
}