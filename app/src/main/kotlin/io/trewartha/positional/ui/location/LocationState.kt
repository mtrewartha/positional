package io.trewartha.positional.ui.location

import androidx.compose.runtime.Immutable
import io.trewartha.positional.data.location.CoordinatesFormat
import io.trewartha.positional.data.location.Location
import io.trewartha.positional.data.measurement.Units
import io.trewartha.positional.data.ui.LocationAccuracyVisibility

@Immutable
sealed interface LocationState {

    data object Loading : LocationState

    data class Data(
        val location: Location,
        val coordinatesFormat: CoordinatesFormat,
        val units: Units,
        val accuracyVisibility: LocationAccuracyVisibility
    ) : LocationState
}
