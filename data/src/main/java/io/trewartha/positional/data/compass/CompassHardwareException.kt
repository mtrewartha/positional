package io.trewartha.positional.data.compass

/**
 * Exception indicating that the device does not have the hardware necessary to produce a compass
 * reading
 */
class CompassHardwareException : IllegalStateException("Device is missing required hardware")
