package io.trewartha.positional.ui.location

sealed interface LocationEvent {
    data class LockToggle(val locked: Boolean) : LocationEvent
}
