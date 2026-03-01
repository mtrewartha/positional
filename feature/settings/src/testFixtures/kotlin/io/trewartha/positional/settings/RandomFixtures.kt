package io.trewartha.positional.settings

import io.kotest.property.Arb
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.next
import io.trewartha.positional.core.measurement.Units
import io.trewartha.positional.core.test.TEST_RANDOM_SOURCE

public fun randomCompassMode(): CompassMode =
    Arb.enum<CompassMode>().next(TEST_RANDOM_SOURCE)

public fun randomCompassNorthVibration(): CompassNorthVibration =
    Arb.enum<CompassNorthVibration>().next(TEST_RANDOM_SOURCE)

public fun randomCoordinatesFormat(): CoordinatesFormat =
    Arb.enum<CoordinatesFormat>().next(TEST_RANDOM_SOURCE)

public fun randomLocationAccuracyVisibility(): LocationAccuracyVisibility =
    Arb.enum<LocationAccuracyVisibility>().next(TEST_RANDOM_SOURCE)

public fun randomTheme(): Theme =
    Arb.enum<Theme>().next(TEST_RANDOM_SOURCE)

public fun randomUnits(): Units =
    Arb.enum<Units>().next(TEST_RANDOM_SOURCE)
