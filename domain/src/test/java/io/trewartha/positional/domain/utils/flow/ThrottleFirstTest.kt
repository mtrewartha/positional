package io.trewartha.positional.domain.utils.flow

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.testTimeSource
import kotlin.test.Test
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

class ThrottleFirstTest {

    private val period = 10.seconds

    @Test
    fun testEmptyFlowEmitsNothing() = runTest {
        val flow = emptyFlow<Int>()

        flow.throttleFirst(period).test {
            awaitComplete()
        }
    }

    @Test
    fun testSingleItemFlowEmitsItem() = runTest {
        val flow = flowOf(1)

        flow.throttleFirst(period).test {
            awaitItem().shouldBe(1)
            awaitComplete()
        }
    }

    @Test
    fun testSingleItemFlowEmitsInstantly() = runTest {
        val flow = flowOf(1)

        flow.throttleFirst(period).test {
            val timeBeforeWait = testScheduler.currentTime
            awaitItem()
            testScheduler.currentTime.shouldBe(timeBeforeWait)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun testSubsequentEmissionBeforeThrottlePeriodIsDropped() = runTest {
        val flow = flow {
            emit(1)
            delay(period - 1.nanoseconds)
            emit(2)
        }

        flow.throttleFirst(period, testTimeSource).test {
            awaitItem().shouldBe(1)
            awaitComplete()
        }
    }

    @Test
    fun testSubsequentEmissionAfterThrottlePeriodIsEmitted() = runTest {
        val flow = flow {
            emit(1)
            delay(period)
            emit(2)
        }

        flow.throttleFirst(period, testTimeSource).test {
            awaitItem().shouldBe(1)
            awaitItem().shouldBe(2)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
