package io.trewartha.positional.core.ui

import app.cash.turbine.test
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest

class StateTest : DescribeSpec({

    describe("a loading state") {
        context("getting data or null") {
            it("it returns null") {
                State.Loading.dataOrNull.shouldBeNull()
            }
        }

        context("getting failure cause or null") {
            it("it returns null") {
                State.Loading.failureCauseOrNull.shouldBeNull()
            }
        }
    }

    describe("a failure state") {
        context("getting data or null") {
            it("it returns null") {
                State.Failure(Unit).dataOrNull.shouldBeNull()
            }
        }

        context("getting failure cause or null") {
            it("it returns the failure cause") {
                val cause = "cause"
                State.Failure(cause).failureCauseOrNull.shouldBe(cause)
            }
        }
    }

    describe("a loaded state") {
        context("getting data or null") {
            it("it returns the data") {
                val data = "data"
                val state = State.Loaded(data)
                state.dataOrNull.shouldBe(data)
            }
        }

        context("getting failure cause or null") {
            it("it returns null") {
                val data = "data"
                val state = State.Loaded(data)
                state.failureCauseOrNull.shouldBeNull()
            }
        }
    }

    describe("a loaded state with nullable data") {
        context("getting data or null") {
            it("it returns null") {
                val state = State.Loaded<String?>(null)
                state.dataOrNull.shouldBeNull()
            }
        }
    }

    describe("converting a flow of items to states") {
        context("when the upstream emits values then throws an exception") {
            it("emits loading, each loaded value, failure, and completes") {
                runTest {
                    flow {
                        emit(1)
                        emit(2)
                        error("IM IN UR BASE")
                    }.asStates().test {
                        awaitItem().shouldBe(State.Loading)
                        awaitItem().shouldBe(State.Loaded(1))
                        awaitItem().shouldBe(State.Loaded(2))
                        awaitItem().shouldBe(State.Failure(Unit))
                        awaitComplete()
                    }
                }
            }
        }

        context("when the upstream throws an exception and a failure cause transformer is provided") {
            it("passes the exception to the transformer and emits its result as the failure cause") {
                runTest {
                    val runtimeException = RuntimeException("IM IN UR BASE")
                    flow<Int> {
                        throw runtimeException
                    }.asStates { exception ->
                        exception.shouldBe(runtimeException)
                        -1
                    }.test {
                        skipItems(1)
                        awaitItem().shouldBe(State.Failure(-1))
                        cancelAndIgnoreRemainingEvents()
                    }
                }
            }
        }

        context("when the upstream throws a non-Exception Throwable") {
            it("propagates the Throwable without catching it") {
                runTest {
                    val error = Error("IM IN UR SYSTEM")
                    flow<Int> {
                        throw error
                    }.asStates().test {
                        awaitItem() // State.Loading emitted by onStart before upstream throws
                        awaitError().shouldBe(error)
                    }
                }
            }
        }

        context("when the upstream is empty") {
            it("emits loading then completes") {
                runTest {
                    emptyFlow<Int>().asStates().test {
                        awaitItem().shouldBe(State.Loading)
                        awaitComplete()
                    }
                }
            }
        }
    }
})
