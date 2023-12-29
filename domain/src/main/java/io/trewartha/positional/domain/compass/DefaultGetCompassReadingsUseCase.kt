package io.trewartha.positional.domain.compass

import io.trewartha.positional.data.compass.Compass
import io.trewartha.positional.data.location.Locator
import io.trewartha.positional.data.measurement.Angle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

/**
 * [GetCompassReadingsUseCase] implementation powered by a [Compass] and [Locator]
 */
class DefaultGetCompassReadingsUseCase @Inject constructor(
    private val compass: Compass,
    locator: Locator,
) : GetCompassReadingsUseCase {

    private val magneticDeclination: Flow<Angle?> = locator.location
        .map { it.magneticDeclination }
        .distinctUntilChanged()
        .onStart { emit(null) }

    override operator fun invoke(): Flow<CompassReading> =
        combine(
            compass.azimuth,
            magneticDeclination,
        ) { compassAzimuth, magneticDeclination ->
            CompassReading(compassAzimuth, magneticDeclination)
        }
}
