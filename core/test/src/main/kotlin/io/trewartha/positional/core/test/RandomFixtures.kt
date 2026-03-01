package io.trewartha.positional.core.test

import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.next
import kotlin.time.Instant

/**
 * Generate a random boolean
 *
 * @return a random boolean
 */
public fun randomBoolean(): Boolean = Arb.boolean().next(TEST_RANDOM_SOURCE)

/**
 * Generate a random instant in time
 *
 * @return a random instant in time
 */
public fun randomInstant(): Instant =
    Instant.fromEpochMilliseconds(Arb.long(0L..Long.MAX_VALUE).next(TEST_RANDOM_SOURCE))

/**
 * Generate a random integer within a given [range]
 *
 * @param range The range that the return value must fall within
 *
 * @return a random Int within the specified [range]
 */
public fun randomInt(range: IntRange = Int.MIN_VALUE..Int.MAX_VALUE): Int =
    Arb.int(range).next(TEST_RANDOM_SOURCE)

/**
 * Randomly retain a value or nullify it
 *
 * @return randomly either `null` or this value
 */
public fun <T : Any> T.orNull(): T? = if (randomBoolean()) this else null