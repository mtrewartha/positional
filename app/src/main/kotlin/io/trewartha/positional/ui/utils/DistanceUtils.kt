package io.trewartha.positional.ui.utils

object DistanceUtils {

    private const val FEET_PER_METER = 3.2808399200439453f
    private const val MPH_PER_MPS = 2.236936f
    private const val KPH_PER_MPS = 3.6f

    fun metersToFeet(meters: Float) = meters * FEET_PER_METER

    fun metersPerSecondToMilesPerHour(metersPerSecond: Float) = metersPerSecond * MPH_PER_MPS

    fun metersPerSecondToKilometersPerHour(metersPerSecond: Float) = metersPerSecond * KPH_PER_MPS
}