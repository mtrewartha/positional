package io.trewartha.positional.ui.location

import io.trewartha.positional.data.location.Location

data class Coordinates(val latitude: Double, val longitude: Double)

val Location.coordinates: Coordinates
    get() = Coordinates(latitude, longitude)
