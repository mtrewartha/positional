package io.trewartha.positional.domain.utils.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.ComparableTimeMark
import kotlin.time.Duration
import kotlin.time.TimeSource

/**
 * Throttles emissions from a flow
 *
 * @param period Period of time that must elapse before the next upstream emission will be emitted
 * downstream
 * @param timeSource Time source to use for measuring time elapsed between emissions
 *
 * @return a flow that immediately emits the first item emitted from the upstream flow, then emits
 * subsequent items as long as they aren't emitted within a given [period] from the most recent
 * downstream emission
 */
fun <T> Flow<T>.throttleFirst(
    period: Duration,
    timeSource: TimeSource.WithComparableMarks = TimeSource.Monotonic
): Flow<T> {
    require(period.isPositive()) { "period must be positive" }
    return flow {
        var downstreamEmissionMark: ComparableTimeMark? = null
        collect { value ->
            val currentMark = timeSource.markNow()
            if (downstreamEmissionMark?.let { currentMark - it >= period } != false) {
                downstreamEmissionMark = currentMark
                emit(value)
            }
        }
    }
}
