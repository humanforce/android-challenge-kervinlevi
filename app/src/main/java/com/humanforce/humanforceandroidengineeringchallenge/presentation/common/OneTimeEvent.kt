package com.humanforce.humanforceandroidengineeringchallenge.presentation.common

/**
 * Created by kervinlevi on 26/12/24
 */
data class OneTimeEvent<T>(private val event: T, private var consumed: Boolean = false) {
    fun consumeOneTimeEvent(): T? {
        return if (!consumed) {
            consumed = true
            event
        } else {
            null
        }
    }
}
