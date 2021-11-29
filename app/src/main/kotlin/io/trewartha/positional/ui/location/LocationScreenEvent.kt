package io.trewartha.positional.ui.location

sealed class LocationScreenEvent {

    object NavigateToLocationHelp : LocationScreenEvent()

    object NavigateToSettings : LocationScreenEvent()

    data class RequestPermissions(val permissions: List<String>) : LocationScreenEvent()

    object ShowScreenLockedSnackbar : LocationScreenEvent()

    object ShowScreenUnlockedSnackbar : LocationScreenEvent()

    object ShowCoordinatesCopyErrorSnackbar : LocationScreenEvent()

    object ShowCoordinatesCopySuccessBothSnackbar : LocationScreenEvent()

    object ShowCoordinatesCopySuccessLatitudeSnackbar : LocationScreenEvent()

    object ShowCoordinatesCopySuccessLongitudeSnackbar : LocationScreenEvent()

    object ShowCoordinatesShareErrorSnackbar : LocationScreenEvent()

    data class ShowCoordinatesShareSheet(val coordinates: String) : LocationScreenEvent()
}
