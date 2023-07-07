package io.trewartha.positional.domain.compass

import io.trewartha.positional.data.measurement.Angle
import io.trewartha.positional.domain.location.GetLocationUseCase
import io.trewartha.positional.domain.utils.flow.throttleFirst
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.minutes

class GetCompassDeclinationUseCase(
    private val getLocationUseCase: GetLocationUseCase
) {
    operator fun invoke(): Flow<Angle> = getLocationUseCase()
        .throttleFirst(LOCATION_THROTTLE_PERIOD)
        .map { it.magneticDeclination }

    private companion object {
        private val LOCATION_THROTTLE_PERIOD = 5.minutes
    }
}
