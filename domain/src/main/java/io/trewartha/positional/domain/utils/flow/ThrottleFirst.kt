package io.trewartha.positional.domain.utils.flow

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T> Flow<T>.throttleFirst(period: Duration): Flow<T> {
    require(period.isPositive()) { "period must be positive" }
    return flow {
        var lastEmissionTime = 0L
        val periodMillis = period.inWholeMilliseconds
        collect { value ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastEmissionTime >= periodMillis) {
                lastEmissionTime = currentTime
                emit(value)
            }
        }
    }
}

fun <T> Flow<T>.throttleFirst(periodMillis: Long): Flow<T> =
    throttleFirst(periodMillis.milliseconds)