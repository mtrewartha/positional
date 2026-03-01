package io.trewartha.positional.core.error

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import kotlin.coroutines.cancellation.CancellationException

@OptIn(BoundaryErrorHandling::class)
class RunCatchingExceptionsTest : DescribeSpec({

    describe("invoking the function") {
        context("with a block that throws a CancellationException") {
            it("rethrows the CancellationException") {
                val cancellationException = CancellationException()
                val thrownException = shouldThrow<CancellationException> {
                    runCatchingExceptionsTest { throw cancellationException }
                }
                thrownException.shouldBe(cancellationException)
            }
        }

        context("with a block that throws a non-Exception Throwable") {
            it("rethrows the Throwable") {
                val error = Error("IM IN UR SYSTEM")
                val thrownThrowable = shouldThrow<Throwable> {
                    runCatchingExceptionsTest { throw error }
                }
                thrownThrowable.shouldBe(error)
            }
        }

        context("with a block that throws a non-cancellation Exception") {
            it("returns a failure result containing the exception") {
                val exception = RuntimeException("IM IN UR BASE")
                val result = runCatchingExceptionsTest { throw exception }
                result.shouldBeFailure<RuntimeException>().shouldBe(exception)
            }
        }

        context("with a block that completes successfully") {
            it("returns a successful result containing the return value of the block") {
                val result = runCatchingExceptionsTest { "Success!" }
                result.shouldBeSuccess().shouldBe("Success!")
            }
        }
    }
})
