package io.trewartha.positional.model.settings

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Length of device vibration to trigger when the compass crosses north
 */
enum class CompassNorthVibration {

    /**
     * No vibration
     */
    NONE {
        override val duration = Duration.ZERO
    },

    /**
     * Short vibration
     */
    SHORT {
        override val duration = 10.milliseconds
    },

    /**
     * Medium vibration
     */
    MEDIUM {
        override val duration = 50.milliseconds
    },

    /**
     * Long vibration
     */
    LONG {
        override val duration = 100.milliseconds
    };

    /**
     * Duration of the vibration
     */
    abstract val duration: Duration
}
