package io.trewartha.positional.common

open class Event<out T>(private val content: T) {

    var handled = false
        private set

    fun handle(): T? = if (handled) {
        null
    } else {
        handled = true
        content
    }

    fun peekContent(): T = content
}