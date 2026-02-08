package io.trewartha.positional.core.ui

import app.cash.turbine.test
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest

class StateTest : AnnotationSpec() {

    @Test
    fun `Getting data or null returns null when state is loading`() {
        State.Loading.dataOrNull.shouldBeNull()
    }

    @Test
    fun `Getting data or null returns null when state is failure`() {
        State.Failure(Unit).dataOrNull.shouldBeNull()
    }

    @Test
    fun `Getting data or null returns data when state is loaded`() {
        val data = "data"
        State.Loaded(data).dataOrNull.shouldBe(data)
    }

    @Test
    fun `Getting failure cause or null returns null when state is loading`() {
        State.Loading.failureCauseOrNull.shouldBeNull()
    }

    @Test
    fun `Getting failure cause or null returns failure cause when state is failure`() {
        val cause = "cause"
        State.Failure(cause).failureCauseOrNull.shouldBe(cause)
    }

    @Test
    fun `Getting failure cause or null returns null when state is loaded`() {
        State.Loaded("data").failureCauseOrNull.shouldBeNull()
    }

    @Test
    fun `Getting Flow as States returns Flow that emits load states`() = runTest {
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

    @Test
    fun `Getting Flow as States transforms exception into failure causes`() = runTest {
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