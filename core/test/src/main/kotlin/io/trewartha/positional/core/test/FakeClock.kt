package io.trewartha.positional.core.test

import kotlin.time.Clock
import kotlin.time.Instant

public class FakeClock(
    private var now: Instant? = null
) : Clock {

    override fun now(): Instant = checkNotNull(now) { "Instant was not provided to the fake" }

    public fun setNow(now: Instant) {
        this.now = now
    }
}