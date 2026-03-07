package io.trewartha.positional.location

import io.trewartha.positional.core.measurement.randomAngle
import io.trewartha.positional.core.measurement.randomCoordinates
import io.trewartha.positional.core.measurement.randomDistance
import io.trewartha.positional.core.measurement.randomSpeed
import io.trewartha.positional.core.test.orNull
import io.trewartha.positional.core.test.randomInstant

public fun randomLocation(): Location =
    Location(
        timestamp = randomInstant(),
        coordinates = randomCoordinates(),
        horizontalAccuracy = randomDistance().orNull(),
        bearing = randomAngle().orNull(),
        bearingAccuracy = randomAngle().orNull(),
        altitude = randomDistance().orNull(),
        altitudeAccuracy = randomDistance().orNull(),
        magneticDeclination = randomAngle().orNull(),
        speed = randomSpeed().orNull(),
        speedAccuracy = randomSpeed().orNull(),
    )
