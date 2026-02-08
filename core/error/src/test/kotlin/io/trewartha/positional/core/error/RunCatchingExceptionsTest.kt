package io.trewartha.positional.core.error

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.kotest.matchers.shouldBe
import kotlin.coroutines.cancellation.CancellationException

@OptIn(BoundaryErrorHandling::class)
class RunCatchingExceptionsTest : BehaviorSpec({

    Given("a block that throws a cancellation exception") {
        val cancellationException = CancellationException()
        val block: () -> Unit = { throw cancellationException }

        When("the function is called") {
            val thrownException = shouldThrow<CancellationException> {
                runCatchingExceptionsTest(block)
            }

            Then("the cancellation exception is rethrown") {
                thrownException.shouldBe(cancellationException)
            }
        }
    }

    Given("a block that throws a non-exception throwable") {
        val nonExceptionThrowable = Error("IM IN UR SYSTEM")
        val block: () -> Unit = { throw nonExceptionThrowable }

        When("the function is called") {
            val thrownThrowable = shouldThrow<Throwable> {
                runCatchingExceptionsTest(block)
            }

            Then("the throwable is rethrown") {
                thrownThrowable.shouldBe(nonExceptionThrowable)
            }
        }
    }

    Given("a block that throws a non-cancellation exception") {
        val nonCancellationException = RuntimeException("IM IN UR BASE")
        val block: () -> Unit = { throw nonCancellationException }

        When("the function is called") {
            val result = runCatchingExceptionsTest(block)

            Then("a failure is returned containing the exception") {
                result.shouldBeFailure<RuntimeException>().shouldBe(nonCancellationException)
            }
        }
    }

    Given("a block that completes successfully") {
        val successfulBlockResult = "Success!"
        val block = { successfulBlockResult }

        When("the function is called") {
            val result = runCatchingExceptionsTest(block)

            Then("a success is returned containing the result") {
                result.shouldBeSuccess().shouldBe(successfulBlockResult)
            }
        }
    }
})
