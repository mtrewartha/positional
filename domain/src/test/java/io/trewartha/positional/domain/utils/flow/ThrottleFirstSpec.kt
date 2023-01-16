package io.trewartha.positional.domain.utils.flow

import app.cash.turbine.Event
import app.cash.turbine.test
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldHaveAtMostSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow

class ThrottleFirstSpec : BehaviorSpec({

    val periodMillis = 100L

    given("a flow with zero items") {
        val items = emptyList<Int>()
        lateinit var flow: Flow<Int>
        beforeEach {
            flow = items.asFlow()
        }
        `when`("it is throttled") {
            lateinit var throttledFlow: Flow<Int>
            beforeEach {
                throttledFlow = flow.throttleFirst(periodMillis)
            }
            then("it is empty like the original flow") {
                throttledFlow.test {
                    awaitComplete()
                }
            }
        }
    }
    given("a flow with one item") {
        val items = listOf(1)
        lateinit var flow: Flow<Int>
        beforeEach {
            flow = items.asFlow()
        }
        `when`("it is throttled") {
            lateinit var throttledFlow: Flow<Int>
            beforeEach {
                throttledFlow = flow.throttleFirst(periodMillis)
            }
            then("only the item from the flow should be emitted") {
                throttledFlow.shouldOnlyEmitItems(items)
            }
            then("the item is emitted immediately") {
                throttledFlow.test {
                    awaitItem().shouldBe(items.first())
                    cancelAndIgnoreRemainingEvents()
                }
            }
        }
    }
    given("a flow with more than one item") {
        val items = listOf(1, 2)
        lateinit var flow: Flow<Int>
        beforeEach {
            flow = items.asFlow()
        }
        `when`("it is throttled") {
            lateinit var throttledFlow: Flow<Int>
            beforeEach {
                throttledFlow = flow.throttleFirst(periodMillis)
            }
            then("only items from the flow should be emitted") {
                throttledFlow.shouldOnlyEmitItems(items)
            }
            then("the first item is emitted immediately") {
                throttledFlow.test {
                    expectMostRecentItem().shouldBe(items.first())
                    cancelAndIgnoreRemainingEvents()
                }
            }
            and("an item is emitted from the flow within the same period as a previous item") {
                then("the item is not emitted from the throttled flow") {
                    throttledFlow.test {
                        awaitItem().shouldBe(items.first())
                        awaitComplete()
                    }
                }
            }
            and("an item is emitted from the flow after the period a previous item was emitted in") {
                beforeEach {
                    flow = flow {
                        emit(items.first())
                        delay(periodMillis)
                        emit(items.last())
                    }
                    throttledFlow = flow.throttleFirst(periodMillis)
                }
                then("the item is emitted from the throttled flow") {
                    throttledFlow.test {
                        awaitItem().shouldBe(items.first())
                        awaitItem().shouldBe(items.last())
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }
    }
})

private suspend fun <T> Flow<T>.shouldOnlyEmitItems(items: List<T>) {
    val emittedItems = mutableListOf<T>()
    test {
        do {
            val event = awaitEvent()
            if (event is Event.Item) emittedItems.add(event.value)
        } while (event != Event.Complete)
    }
    for (emittedItem in emittedItems) {
        emittedItem.shouldBeIn(items)
    }
    emittedItems.shouldHaveAtMostSize(items.size)
}
