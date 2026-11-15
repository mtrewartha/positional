package io.trewartha.positional.core.ui

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.kotest.matchers.string.shouldStartWith
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
public class ThrowawayInstrumentedTest {

    @Test
    public fun targetContextUsesTheLibraryPackage() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.packageName shouldStartWith "io.trewartha.positional.core.ui"
    }
}
