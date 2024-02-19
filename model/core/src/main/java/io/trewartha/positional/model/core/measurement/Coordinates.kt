package io.trewartha.positional.model.core.measurement

/**
 * Geographic coordinates of some point on the Earth, represented as latitude and longitude in
 * decimal degrees
 *
 * @property latitude Latitude in decimal degrees
 * @property longitude Longitude in decimal degrees
 */
data class Coordinates(val latitude: Double, val longitude: Double)
