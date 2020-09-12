package io.trewartha.positional.utils

import com.google.common.collect.EvictingQueue

/**
 * Calculates the exponential moving average of the entire queue
 *
 * @param alpha the exponential weight. Must be between 0 and 1 (both exclusive). Higher values will
 * cause the most recent readings to have more weight and lower values will cause the oldest values
 * to have more weight.
 *
 * @return the exponential moving average of the entire queue
 */
@Suppress("UnstableApiUsage")
fun EvictingQueue<Float>.ema(alpha: Float): Float? {
    if (isEmpty()) return null
    if (alpha.isNaN()
            || alpha == Float.NEGATIVE_INFINITY
            || alpha == Float.POSITIVE_INFINITY
            || alpha == Float.MIN_VALUE
            || alpha == Float.MAX_VALUE
            || alpha <= 0f
            || alpha >= 1f
    ) throw IllegalArgumentException("alpha must be between 0 and 1")

    return reduceIndexed { index, previousEma, floatElement ->
        if (index == 0)
            floatElement
        else
            (alpha * floatElement) + ((1f - alpha) * previousEma)
    }
}

/**
 * Calculates the exponential moving average of the entire queue
 *
 * @param alpha the exponential weight. Must be between 0 and 1 (both exclusive). Higher values will
 * cause the most recent readings to have more weight and lower values will cause the oldest values
 * to have more weight.
 *
 * @return the exponential moving average of the entire queue
 */
@Suppress("UnstableApiUsage")
fun EvictingQueue<Double>.ema(alpha: Double): Double? {
    if (isEmpty()) return null
    if (alpha.isNaN()
            || alpha == Double.NEGATIVE_INFINITY
            || alpha == Double.POSITIVE_INFINITY
            || alpha == Double.MIN_VALUE
            || alpha == Double.MAX_VALUE
            || alpha <= 0.0
            || alpha >= 1.0
    ) throw IllegalArgumentException("alpha must be between 0 and 1")

    return reduceIndexed { index, previousEma, doubleElement ->
        if (index == 0)
            doubleElement
        else
            (alpha * doubleElement) + ((1f - alpha) * previousEma)
    }
}