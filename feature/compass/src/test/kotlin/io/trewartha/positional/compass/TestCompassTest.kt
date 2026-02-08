package io.trewartha.positional.compass

import app.cash.turbine.test
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.trewartha.positional.core.measurement.degrees
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest

class TestCompassTest : AnnotationSpec() {

    private lateinit var subject: TestCompass

    @BeforeEach
    fun setUp() {
        subject = TestCompass()
    }

    @Test
    fun `Azimuth flow emits the last set azimuth`() = runTest {
        val firstAzimuth = Azimuth(angle = 1.degrees)
        val secondAzimuth = Azimuth(angle = 2.degrees)
        subject.setAzimuth(firstAzimuth)
        subject.setAzimuth(secondAzimuth)

        subject.azimuth.firstOrNull().shouldBe(secondAzimuth)
    }

    @Test
    fun `Setting the azimuth triggers emission of set value`() = runTest {
        subject.azimuth.test {
            val azimuth = Azimuth(angle = 1.degrees)

            subject.setAzimuth(azimuth)

            awaitItem().shouldBe(azimuth)
        }
    }
}