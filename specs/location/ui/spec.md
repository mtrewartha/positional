# Feature: Location UI

## Blueprint

### Context
The location screen is Positional's primary screen. It shows the device's current coordinates in
the user's preferred format, along with accuracy, altitude, speed, and bearing. People use it to
read off a position for navigation, field work, or emergencies, and to hand that position to
another app by copying, sharing, or opening it in a map.

Position comes from the [Location spec](../spec.md), which keeps it only in memory. This screen is
the only place a position can leave the device, and only when the user explicitly asks.

#### Glossary
Location attributes and fixes are defined in the [Location spec](../spec.md#glossary).
- **Coordinate formats** (chosen by the user in settings):
  - **DD:** Decimal Degrees
  - **DDM:** Degrees and Decimal Minutes
  - **DMS:** Degrees, Minutes, and Seconds
  - **MGRS:** Military Grid Reference System
  - **UTM:** Universal Transverse Mercator

### Architecture
- **Where it lives:** `feature/location/ui`. The `app` module decides when the permission-required
  screen replaces the location screen, and turns a share request into the Android share sheet.
- **Behavior:**
  - Coordinates use the user's chosen format, and distances and speeds use the user's chosen
    units.
  - Accuracy values appear only when the user's accuracy visibility setting shows them.
  - The screen shows a loading state until the first fix and settings arrive. Map, share, and copy
    stay disabled until a position is loaded.
  - The screen keeps observing position for 5 s after it goes away, so brief interruptions such as
    configuration changes don't restart position updates.
  - The keep-screen-on toggle lasts only while the screen is shown.
- **Depends on:**
  - [Location](../spec.md) for fixes.
  - Settings for coordinate format, units, and accuracy visibility.
- **Constraints:**
  - A position leaves the app only through explicit user actions: copying it to the clipboard,
    sharing it through the Android share sheet, or opening it in a map app as a `geo:` URI.
  - The app requests and relies on precise location permission (`ACCESS_FINE_LOCATION`) only.
    Approximate location alone never unlocks the location screen.
  - DD, DDM, and DMS copy output formats numbers with the device's default locale, so the decimal
    separator matches the user's locale (for example, a comma in `de-DE`). MGRS and UTM copy output
    contains no decimal separators.

## Contract

### Definition of Done
- [x] Each coordinate format's display and copy outputs are covered by unit tests.
- [x] The location screen's state is loading until the first fix and settings arrive, and then
      follows every change to either.
- [ ] Map, share, and copy actions are disabled until a position is loaded.

### Regression Guardrails
- A position leaves the app only through an explicit user action (see Constraints).
- An absent location attribute is shown as no value, never as zero or a placeholder.
- Copy output stays stable for a given format and locale, because users paste it into other tools.

### Scenarios
```gherkin
Scenario: First fix arrives
  Given precise location permission is granted
  And the location screen is open
  When the first fix arrives
  Then the screen shows the coordinates in the user's chosen format
  And the map, share, and copy actions become enabled

Scenario: Waiting for the first fix
  Given precise location permission is granted
  And no fix has arrived
  When the location screen is shown
  Then the screen shows a loading state
  And the map, share, and copy actions are disabled

Scenario: Permission not yet granted
  Given precise location permission is not granted
  When the user opens the app
  Then the permission-required screen is shown with a link to the privacy policy
  When the user taps the grant button and Android allows a rationale
  Then the user is sent to system settings instead of a new permission prompt

Scenario: Only approximate location granted
  Given the user grants approximate location but not precise location
  When the permission result returns
  Then the permission-required screen remains visible
  And no position is observed

Scenario: Stationary device
  Given a fix without speed or bearing is shown
  Then the speed and bearing blocks show no value

Scenario: Copy coordinates
  Given a position is loaded
  When the user taps copy
  Then the clipboard contains the coordinates formatted for copy in the chosen format
  And a confirmation snackbar is shown

Scenario: Open in a map with no map app installed
  Given a position is loaded
  And no installed app handles geo: URIs
  When the user taps the map action
  Then an error dialog explains that no map app is available

Scenario: Keep screen on
  Given the keep-screen-on toggle is enabled
  When the user leaves the location screen
  Then the window no longer keeps the screen on

Scenario: Accuracy hidden by setting
  Given location accuracy visibility is set to hidden
  When a position with accuracy values is shown
  Then no accuracy values are displayed

Scenario: Leaving the screen stops updates
  Given the location screen is observing position
  When the screen has been gone for longer than 5 s
  Then the screen stops observing position
  But a configuration change shorter than 5 s keeps it observing
```
