package io.trewartha.positional.core.test

import io.kotest.property.RandomSource
import kotlin.random.Random

/**
 * Seed used for [TEST_RANDOM] to make random test fixture data reproducible. The seed is logged at
 * test startup so it can be hard-coded when debugging flaky tests.
 *
 * @see TEST_RANDOM
 */
public val TEST_RANDOM_SEED: Long =
    System.currentTimeMillis().also { println("Random test seed: $it") }

/**
 * # What is this?
 * This is a random number generator (RNG) used to generate random test fixture data.
 *
 * # How should I use it?
 * In nearly any test, you need some sort of test fixture data- it might be as simple as an `Int` or
 * it might be some highly complex data class. In either case, if your test fixture data can be
 * random, you can use this RNG or one of the functions that utilize it under the hood to generate
 * test fixture data with minimal effort. You can even customize the data after it's generated, like
 * so:
 *
 * ```kotlin
 * data class Foo(val bar: Int, val baz: Int)
 *
 * val randomInt = randomInt()
 * val randomFoo = randomFoo().copy(baz = 123)
 * ```
 *
 * # Why is this helpful?
 * First of all, you _can and should_ hard-code any test fixture data that _cannot_ be random for
 * the sake of test integrity. Don't use this in those cases.
 *
 * When you have test data that _can_ be random, use this RNG (or the helper functions that utilize
 * it) to create that real data with minimal effort. If you don't see a helper function that can
 * create the type of test fixture data you need, create it! Your future self and other devs will
 * thank you when they can quickly and easily create test data.
 *
 * ## There's a (kinda) catch...
 * There are times when randomness can bring temporary pain. Say you create a test and it's passing,
 * but then it randomly fails when you've changed nothing. While this is annoying in the short term,
 * it is a very strong hint that your test is incomplete. It doesn't completely specify the test
 * conditions and how your code should behave in those conditions. When this happens, _do not ignore
 * it!_ It's an explicit call to improve your test.
 *
 * _Over time, randomness helps us harden and improve our test suite. The benefits are well worth it
 * in the long run._
 *
 * ## How do I find the data causing the flakiness?
 * The RNG is seeded with the [TEST_RANDOM_SEED]. A RNG initialized with a given seed will always
 * produce the same values when the same functions are called on it in the same order. You can use
 * this to your advantage to find the source of flakiness.
 *
 * When tests fail due to flakiness, grab the logged seed and temporarily hard-code it as the value
 * of [TEST_RANDOM_SEED]. You can now reliably re-run your tests (make sure to use the same Gradle
 * task) as many times as you need to find the data that causes the issue, because the data is no
 * longer random. Once you find the data that is randomly causing failures, either constrict its
 * randomness to values that make sense for your test or hard-code it to a single value that makes
 * sense for your test, then remove your hard-coded value for [TEST_RANDOM_SEED].
 *
 * @see TEST_RANDOM_SEED
 */
public val TEST_RANDOM: Random = Random(TEST_RANDOM_SEED)

/**
 * Kotest [RandomSource] based on our [TEST_RANDOM] and [TEST_RANDOM_SEED]
 *
 * @see TEST_RANDOM
 * @see TEST_RANDOM_SEED
 */
public val TEST_RANDOM_SOURCE: RandomSource = RandomSource(TEST_RANDOM, TEST_RANDOM_SEED)