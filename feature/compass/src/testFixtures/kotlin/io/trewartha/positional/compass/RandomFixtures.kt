package io.trewartha.positional.compass

import io.kotest.property.Arb
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.next
import io.trewartha.positional.core.measurement.randomAngle
import io.trewartha.positional.core.test.TEST_RANDOM_SOURCE
import io.trewartha.positional.core.test.orNull

public fun randomAzimuth(): Azimuth =
    Azimuth(
        angle = randomAngle(),
        accelerometerAccuracy = randomAzimuthAccuracy().orNull(),
        magnetometerAccuracy = randomAzimuthAccuracy().orNull(),
    )

public fun randomAzimuthAccuracy(): Azimuth.Accuracy =
    Arb.enum<Azimuth.Accuracy>().next(TEST_RANDOM_SOURCE)
