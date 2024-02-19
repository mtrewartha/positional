package io.trewartha.positional.ui.location

import androidx.compose.runtime.Immutable
import io.trewartha.positional.model.core.measurement.Units
import io.trewartha.positional.model.location.Location
import io.trewartha.positional.model.settings.CoordinatesFormat
import io.trewartha.positional.model.settings.LocationAccuracyVisibility

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
