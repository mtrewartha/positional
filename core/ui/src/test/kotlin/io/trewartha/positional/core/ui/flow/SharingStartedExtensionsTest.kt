package io.trewartha.positional.core.ui.flow

import app.cash.turbine.test
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingCommand
import kotlinx.coroutines.flow.SharingStarted

class SharingStartedExtensionsTest : DescribeSpec({

    describe("sharing for a view model") {
        context("when the first subscriber arrives") {
            it("starts sharing") {
                val subscriptionCount = MutableStateFlow(0)
                SharingStarted.ForViewModel.command(subscriptionCount).test {
                    subscriptionCount.value = 1

                    awaitItem().shouldBe(SharingCommand.START)
                }
            }
        }

        context("when the last subscriber leaves") {
            it("keeps sharing until just before the stop timeout elapses") {
                val subscriptionCount = MutableStateFlow(1)
                SharingStarted.ForViewModel.command(subscriptionCount).test {
                    awaitItem().shouldBe(SharingCommand.START)

                    subscriptionCount.value = 0
                    delay(4_999.milliseconds)

                    expectNoEvents()
                    cancelAndIgnoreRemainingEvents()
                }
            }

            it("stops sharing and clears the replay cache once the stop timeout elapses") {
                val subscriptionCount = MutableStateFlow(1)
                SharingStarted.ForViewModel.command(subscriptionCount).test {
                    awaitItem().shouldBe(SharingCommand.START)

                    subscriptionCount.value = 0

                    awaitItem()
                        .shouldBe(SharingCommand.STOP_AND_RESET_REPLAY_CACHE)
                }
            }
        }

        context("when a subscriber returns before the stop timeout elapses") {
            it("never stops sharing") {
                val subscriptionCount = MutableStateFlow(1)
                SharingStarted.ForViewModel.command(subscriptionCount).test {
                    awaitItem().shouldBe(SharingCommand.START)

                    subscriptionCount.value = 0
                    delay(4_999.milliseconds)
                    subscriptionCount.value = 1
                    delay(10_000.milliseconds)

                    expectNoEvents()
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }
    }
})
