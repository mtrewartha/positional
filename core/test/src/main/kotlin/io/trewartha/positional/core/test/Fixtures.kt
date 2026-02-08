package io.trewartha.positional.core.test

import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next

/**
 * Generate a random integer within a given [range]
 *
 * @param range The range that the return value must fall within
 *
 * @return a random Int within the specified [range]
 */
public fun randomInt(range: IntRange = Int.MIN_VALUE..Int.MAX_VALUE): Int =
    Arb.int(range).next(TEST_RANDOM_SOURCE)