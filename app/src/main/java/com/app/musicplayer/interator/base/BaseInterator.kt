package com.app.musicplayer.interator.base

interface BaseInterator<Listener> {
    fun registerListener(listener: Listener)
    fun unregisterListener(listener: Listener)
    fun invokeListeners(listener: (Listener) -> Unit)
}