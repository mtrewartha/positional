package io.trewartha.positional.ui.location

sealed interface LocationEvent {

    object NavigateToLocationHelp : LocationEvent

    object NavigateToSettings : LocationEvent

    object ShowScreenLockedSnackbar : LocationEvent

    object ShowScreenUnlockedSnackbar : LocationEvent

    object ShowCoordinatesCopyErrorSnackbar : LocationEvent

    object ShowCoordinatesCopySuccessBothSnackbar : LocationEvent

    object ShowCoordinatesCopySuccessLatitudeSnackbar : LocationEvent

    object ShowCoordinatesCopySuccessLongitudeSnackbar : LocationEvent

    object ShowCoordinatesShareErrorSnackbar : LocationEvent

    data class ShowCoordinatesShareSheet(val coordinates: String) : LocationEvent
}
