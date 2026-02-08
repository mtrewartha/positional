package io.trewartha.positional.core.error

import kotlin.coroutines.cancellation.CancellationException

/**
 * Call the specified lambda [block] and capture its return value or thrown [Exception] in a
 * [Result]. If the lambda executes without issue, a successful result with the lambda's return
 * value is returned.
 *
 * This function is designed for safe boundary error handling functionality that doesn't interfere
 * with coroutine cancellation or non-[Exception] types of [Throwable] that we should not recover
 * from.
 *
 * @param block a lambda to run and encapsulate the result of
 *
 * @return a successful [Result] with [block]'s return value if it succeeds or a failure [Result]
 * if the lambda throws an [Exception] other than [CancellationException]
 *
 * @throws Throwable if the lambda throws any [Throwable] other than a [CancellationException] or an
 * [Exception]
 */
@BoundaryErrorHandling
public inline fun <R> runCatchingExceptions(block: () -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (cancellationException: CancellationException) {
        // Don't interfere with coroutine cancellation
        throw cancellationException
    } catch (@Suppress("TooGenericExceptionCaught") exception: Exception) {
        Result.failure(exception)
    }
}

/**
 * Internal helper to mitigate inline function test coverage issue:
 * https://github.com/jacoco/jacoco/issues/1921
 */
@BoundaryErrorHandling
internal fun <R> runCatchingExceptionsTest(block: () -> R): Result<R> = runCatchingExceptions(block)
