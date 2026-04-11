package io.trewartha.positional.location

import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import io.trewartha.positional.AppScope

@ContributesTo(AppScope::class)
public interface AospLocatorBinding {

    @Provides
    @SingleIn(AppScope::class)
    public fun locator(aospLocator: AospLocator): Locator = aospLocator
}
