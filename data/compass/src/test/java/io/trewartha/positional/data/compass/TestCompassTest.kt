package io.trewartha.positional.data.compass

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import io.trewartha.positional.model.core.measurement.Angle
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class TestCompassTest {

    private lateinit var subject: TestCompass

    @BeforeTest
    fun setUp() {
        subject = TestCompass()
    }

    @Test
    fun `Azimuth flow emits the last set azimuth`() = runTest {
        val firstAzimuth = Azimuth(angle = Angle.Degrees(1f))
        val secondAzimuth = Azimuth(angle = Angle.Degrees(2f))
        subject.setAzimuth(firstAzimuth)
        subject.setAzimuth(secondAzimuth)

        subject.azimuth.firstOrNull().shouldBe(secondAzimuth)
    }

    @Test
    fun `Setting the azimuth triggers emission of set value`() = runTest {
        subject.azimuth.test {
            val azimuth = Azimuth(angle = Angle.Degrees(1f))

            subject.setAzimuth(azimuth)

            awaitItem().shouldBe(azimuth)
        }
    }
}
