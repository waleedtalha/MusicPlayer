package com.app.musicplayer.core.baseobservable

import java.util.*
import kotlin.collections.HashSet

open class BaseObservable<Listener>:IBaseObservable<Listener> {
    private val _listeners by lazy { HashSet<Listener>() }

    val listeners: Set<Listener>
        get() = Collections.unmodifiableSet(HashSet(_listeners))

    override fun registerListener(listener: Listener) {
        _listeners.remove(listener)
        _listeners.add(listener)
        if (_listeners.size == 1) {
            onStartedObserved()
        }
    }

    override fun unregisterListener(listener: Listener) {
        _listeners.remove(listener)
        if (_listeners.size == 0) {
            onFinishedObserved()
        }
    }

    override fun invokeListeners(invoker: (Listener) -> Unit) {
        listeners.forEach(invoker::invoke)
    }

    protected open fun onStartedObserved() {}
    protected open fun onFinishedObserved() {}
}