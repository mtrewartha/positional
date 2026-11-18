# Feature: Location UI

## Blueprint

### Context
The location screen is Positional's primary screen. It shows the device's current coordinates in the user's preferred format, along with accuracy, altitude, speed, and bearing. Users use it to read off a location for navigation, field work, or emergencies, and to hand that location to another app by copying, sharing, or opening it in a map.

The device's location comes from the [Location spec](../spec.md), which keeps it only in memory. This screen is the only place a location can leave the device, and only when the user explicitly asks.

Behavior marked _(pending POS-n)_ is intended but not yet implemented; the marker covers only the sentence it follows, the linked issue tracks the change, and the marker is removed in the same commit as the fix.

#### Glossary
The [Location spec](../spec.md#glossary) defines the location attributes.
- **Coordinate formats** (chosen by the user in settings):
  - **DD:** Decimal Degrees
  - **DDM:** Degrees and Decimal Minutes
  - **DMS:** Degrees, Minutes, and Seconds
  - **MGRS:** Military Grid Reference System
  - **UTM:** Universal Transverse Mercator

### Architecture
- **Where it lives:** `feature/location/ui`.
- **Behavior:**
  - Showing a location:
    - Coordinates are displayed in the user's chosen format, and distances and speeds in the user's chosen units.
    - Metric units show distances in meters and speeds in km/h; imperial units show feet and mph. Bearing is always shown in degrees.
    - Metric distances, including accuracy, show one decimal place; feet, speeds, and bearing show whole numbers.
    - MGRS covers the whole globe, including the polar regions, so MGRS coordinates are always shown.
    - Below the coordinates, the screen shows the local time of day at which the location was determined.
    - The screen shows loading indicators until a location arrives.
    - The screen also waits for the user's settings, so coordinates always appear in the user's chosen format and attributes in the user's chosen units. _(pending [POS-70](https://linear.app/trewartha/issue/POS-70))_
    - Map, share, and copy stay disabled until a location is loaded.
    - An absent attribute is shown as a dash.
    - Horizontal accuracy is always shown as its own attribute. The accuracy visibility setting shows or hides the accuracy of altitude, speed, and bearing, each next to the attribute it describes.
    - When UTM is the chosen format and the location is outside UTM's range (80°S to 84°N), the coordinates show dashes. _(pending [POS-75](https://linear.app/trewartha/issue/POS-75))_ Copy and share are disabled in that case. _(pending [POS-75](https://linear.app/trewartha/issue/POS-75))_ The map action stays available.
  - When location isn't available:
    - While observing location is failing, the coordinates and update time show loading indicators, and map, share, and copy are disabled.
    - The attributes show loading indicators too. _(pending [POS-74](https://linear.app/trewartha/issue/POS-74))_
    - When location becomes available again, the screen recovers on its own. _(pending [POS-74](https://linear.app/trewartha/issue/POS-74))_
    - When no location can be obtained, for example because location services are off, the screen tells the user why instead of loading indefinitely. _(pending [POS-83](https://linear.app/trewartha/issue/POS-83))_
  - Sending a location out:
    - Copy places the coordinates, formatted for copy, on the clipboard and confirms with a snackbar.
    - Share sends the same text that copy produces.
    - Share opens Android's share sheet, and shows an error instead of crashing when no app can receive the text. _(pending [POS-81](https://linear.app/trewartha/issue/POS-81))_
    - The map action opens a `geo:` URI whose pin is labeled with the date and time the location was determined, and shows an error dialog when no app handles `geo:` URIs.
  - Leaving and returning:
    - After rotation, backgrounding, or opening the help screen, the screen keeps observing location for 5 seconds, so brief interruptions don't restart location updates.
    - Switching to another tab gets the same 5-second grace period. _(pending [POS-80](https://linear.app/trewartha/issue/POS-80))_
    - Once the screen stops observing, the last location is discarded, so returning to the screen shows loading indicators.
  - Keep screen on:
    - The keep-screen-on toggle keeps the screen awake while the location screen is shown, and confirms each change with a snackbar.
    - The toggle starts off on every cold start of the app; it isn't saved in settings. When Android restores the app after reclaiming its memory, the toggle keeps its previous state.
    - Leaving the location screen lets the screen sleep. Returning after rotation, backgrounding, or the help screen with the toggle on keeps it awake again.
    - Returning after switching tabs keeps it awake again too. _(pending [POS-80](https://linear.app/trewartha/issue/POS-80))_
  - Help:
    - The help button opens a help screen that explains each attribute.
    - The help screen's statements match this spec and the Location spec. _(pending [POS-77](https://linear.app/trewartha/issue/POS-77))_
  - Permission:
    - Until precise location permission is granted, a permission-required screen is shown with a link to the privacy policy.
    - The permission-required screen's privacy statement matches this spec: the app never stores or sends a location, and a location leaves the device only when the user copies, shares, or opens it in a map. _(pending [POS-87](https://linear.app/trewartha/issue/POS-87))_
    - That link points to the privacy policy on the `main` branch. _(pending [POS-78](https://linear.app/trewartha/issue/POS-78))_
    - The first tap on the grant button shows Android's permission prompt on every supported Android version. _(pending [POS-69](https://linear.app/trewartha/issue/POS-69))_
    - Later taps show the prompt again whenever Android can still show it, and open the app's system settings once the user has permanently denied permission. _(pending [POS-73](https://linear.app/trewartha/issue/POS-73))_
- **Depends on:**
  - [Location](../spec.md) for the device's location.
  - Settings for coordinate format, units, and accuracy visibility.
- **Constraints:**
  - A location leaves the app only through explicit user actions: copying it to the clipboard, sharing it, or opening it in a map app as a `geo:` URI.
  - The app requests precise (`ACCESS_FINE_LOCATION`) and approximate (`ACCESS_COARSE_LOCATION`) location permissions together, as Android requires. _(pending [POS-69](https://linear.app/trewartha/issue/POS-69))_
  - The app relies on precise location only; approximate location alone keeps the user on the permission-required screen.
  - Coordinates shown to the user, copied, or shared are formatted with the user's locale. DD, DDM, and DMS therefore use the locale's decimal separator (for example, a comma in `de-DE`). MGRS and UTM contain no decimal separators.
  - In copied and shared coordinates, the separator between latitude and longitude can't be confused with the locale's decimal separator. _(pending [POS-82](https://linear.app/trewartha/issue/POS-82))_
  - The `geo:` URI uses plain decimal degrees with a period as the decimal separator and no symbols, regardless of locale. _(pending [POS-72](https://linear.app/trewartha/issue/POS-72))_
  - Crash reports contain no coordinates. _(pending [POS-76](https://linear.app/trewartha/issue/POS-76))_

## Contract

### Definition of Done
- [ ] Coordinates are displayed in the user's chosen format.
- [ ] Distances and speeds are displayed in the user's chosen units.
- [ ] Metric units show meters and km/h; imperial units show feet and mph.
- [ ] Bearing is always shown in degrees.
- [ ] Metric distances show one decimal place.
- [ ] Feet, speeds, and bearing show whole numbers.
- [ ] MGRS coordinates are shown for locations in the polar regions.
- [ ] The time of day the location was determined is shown below the coordinates.
- [ ] The screen shows loading indicators until the first location arrives.
- [ ] Each new location replaces the one shown.
- [ ] Changing the coordinates format updates the screen.
- [ ] Changing the units updates the screen.
- [ ] Changing the accuracy visibility setting updates the screen.
- [ ] The screen keeps loading until the user's settings are known, even after a location arrives. _(pending [POS-70](https://linear.app/trewartha/issue/POS-70))_
- [ ] Map, share, and copy are disabled until a location is loaded.
- [ ] Absent attributes are shown as dashes.
- [ ] Horizontal accuracy is shown regardless of the accuracy visibility setting.
- [ ] Altitude, speed, and bearing accuracy are shown when the accuracy visibility setting is on.
- [ ] Altitude, speed, and bearing accuracy are hidden when the accuracy visibility setting is off.
- [x] Displayed DD, DDM, and DMS coordinates use the locale's decimal separator.
- [ ] Copied DD, DDM, and DMS coordinates use the locale's decimal separator.
- [ ] Copied and shared coordinates separate latitude and longitude unambiguously in comma-decimal locales. _(pending [POS-82](https://linear.app/trewartha/issue/POS-82))_
- [ ] UTM coordinates outside UTM's range show dashes. _(pending [POS-75](https://linear.app/trewartha/issue/POS-75))_
- [ ] Copy and share are disabled for UTM coordinates outside UTM's range. _(pending [POS-75](https://linear.app/trewartha/issue/POS-75))_
- [ ] While location is failing, the coordinates and update time show loading indicators.
- [ ] While location is failing, map, share, and copy are disabled.
- [ ] While location is failing, the attributes show loading indicators. _(pending [POS-74](https://linear.app/trewartha/issue/POS-74))_
- [ ] The screen recovers on its own when location becomes available after a failure. _(pending [POS-74](https://linear.app/trewartha/issue/POS-74))_
- [ ] The screen tells the user why when no location can be obtained. _(pending [POS-83](https://linear.app/trewartha/issue/POS-83))_
- [ ] Copy places the coordinates, formatted for copy, on the clipboard.
- [ ] Copy shows a confirmation snackbar.
- [ ] Share sends the same text that copy produces.
- [ ] Share opens Android's share sheet. _(pending [POS-81](https://linear.app/trewartha/issue/POS-81))_
- [ ] Share shows an error when no app can receive the text. _(pending [POS-81](https://linear.app/trewartha/issue/POS-81))_
- [ ] The map action opens a `geo:` URI with the location's coordinates in plain decimal degrees. _(pending [POS-72](https://linear.app/trewartha/issue/POS-72))_
- [ ] The map pin's label contains the date and time the location was determined.
- [ ] The map action shows an error dialog when no app handles `geo:` URIs.
- [ ] The screen stops observing location 5 seconds after backgrounding hides it.
- [ ] The screen stops observing location 5 seconds after opening the help screen hides it.
- [ ] Rotation that hides the screen for less than 5 seconds doesn't restart location updates.
- [ ] Backgrounding that hides the screen for less than 5 seconds doesn't restart location updates.
- [ ] Opening the help screen for less than 5 seconds doesn't restart location updates.
- [ ] Switching tabs keeps observing location for 5 seconds. _(pending [POS-80](https://linear.app/trewartha/issue/POS-80))_
- [ ] Returning to the screen after it stopped observing shows loading indicators.
- [ ] Turning the keep-screen-on toggle on keeps the screen awake.
- [ ] Turning the keep-screen-on toggle off lets the screen sleep.
- [ ] Each keep-screen-on change shows a confirmation snackbar.
- [ ] Leaving the location screen lets the screen sleep.
- [ ] The keep-screen-on toggle is off on a cold start.
- [ ] The keep-screen-on toggle keeps its state when Android restores the app after reclaiming its memory.
- [ ] Returning to the location screen after rotation with the toggle on keeps the screen awake again.
- [ ] Returning to the location screen after backgrounding with the toggle on keeps the screen awake again.
- [ ] Returning to the location screen from the help screen with the toggle on keeps the screen awake again.
- [ ] Returning to the location screen after switching tabs with the toggle on keeps the screen awake again. _(pending [POS-80](https://linear.app/trewartha/issue/POS-80))_
- [ ] The help button opens the location help screen.
- [ ] The help screen says speed and bearing aren't shown below 0.3 m/s, matching the Location spec. _(pending [POS-77](https://linear.app/trewartha/issue/POS-77))_
- [ ] The permission-required screen is shown until precise location permission is granted.
- [ ] The permission-required screen links to the privacy policy.
- [ ] The permission-required screen's privacy statement matches this spec. _(pending [POS-87](https://linear.app/trewartha/issue/POS-87))_
- [ ] The privacy policy link points to the `main` branch. _(pending [POS-78](https://linear.app/trewartha/issue/POS-78))_
- [ ] The permission request asks for precise and approximate location together. _(pending [POS-69](https://linear.app/trewartha/issue/POS-69))_
- [ ] Approximate location alone keeps the permission-required screen visible.
- [ ] The first tap on the grant button shows Android's permission prompt on every supported Android version. _(pending [POS-69](https://linear.app/trewartha/issue/POS-69))_
- [ ] After one denial, the grant button shows Android's permission prompt again. _(pending [POS-73](https://linear.app/trewartha/issue/POS-73))_
- [ ] After a permanent denial, the grant button opens the app's system settings. _(pending [POS-73](https://linear.app/trewartha/issue/POS-73))_
- [ ] Crash reports contain no coordinates. _(pending [POS-76](https://linear.app/trewartha/issue/POS-76))_

### Regression Guardrails
- A location leaves the app only through the copy, share, and map actions. Enforced in review: in `feature/location/ui` and `app`, clipboard writes, share intents, and `geo:` launches happen only in response to those actions (crash-report contents are audited in [POS-76](https://linear.app/trewartha/issue/POS-76)).
- Copy output for a given location, format, and locale stays identical across releases, because users paste it into other tools. Enforced by formatter tests that pin exact display and copy strings. Today they pin DD, DDM, and DMS in `en-US`; pinning MGRS and UTM is tracked in [POS-86](https://linear.app/trewartha/issue/POS-86), and comma-decimal locales in [POS-82](https://linear.app/trewartha/issue/POS-82).

### Scenarios
```gherkin
Scenario: First location arrives
  Given precise location permission is granted
  And the location screen is open
  And the user's settings are known
  When the first location arrives
  Then the coordinates are shown in the user's chosen format
  And the attributes are shown in the user's chosen units
  And the time of day the location was determined is shown below the coordinates
  And the map, share, and copy actions become enabled

Scenario: A new location arrives
  Given a location is shown
  When a new location arrives
  Then the new location replaces it

Scenario Outline: A setting changes
  Given a location is shown
  When the user changes the <setting>
  Then <result>

  Examples:
    | setting                      | result                                                        |
    | coordinates format           | the coordinates are shown in the new format                   |
    | units                        | distances and speeds are shown in the new units               |
    | accuracy visibility setting  | altitude, speed, and bearing accuracy are shown or hidden to match |

@pending @POS-70
Scenario: Location arrives before the user's settings
  Given the location screen is open
  And the user's settings aren't known yet
  When a location arrives
  Then the screen keeps showing loading indicators

Scenario: Waiting for the first location
  Given precise location permission is granted
  And no location has arrived
  When the location screen is shown
  Then the screen shows loading indicators
  And the map, share, and copy actions are disabled

Scenario: Absent attributes
  Given a location without speed or bearing
  When the location is shown
  Then speed and bearing show dashes

Scenario: Horizontal accuracy
  Given a location with horizontal accuracy
  When the location is shown
  Then horizontal accuracy is displayed whatever the accuracy visibility setting is

Scenario: Accuracy shown by setting
  Given location accuracy visibility is set to shown
  When a location with altitude, speed, and bearing accuracy is shown
  Then each of those accuracy values is displayed next to the attribute it describes

Scenario: Accuracy hidden by setting
  Given location accuracy visibility is set to hidden
  When a location with altitude, speed, and bearing accuracy is shown
  Then none of those accuracy values are displayed

Scenario Outline: Displayed coordinates in a comma-decimal locale
  Given the user's locale is fr-FR
  And the chosen format is <format>
  When the location is shown
  Then the displayed coordinates use a comma as the decimal separator

  Examples:
    | format |
    | DD     |
    | DDM    |
    | DMS    |

Scenario: MGRS near a pole
  Given the chosen format is MGRS
  When a location north of 84°N is shown
  Then MGRS coordinates are shown

Scenario Outline: Units
  Given the user's units are <units>
  When a location with altitude and speed is shown
  Then altitude is shown in <distance> and speed in <speed>
  And bearing is shown in degrees

  Examples:
    | units    | distance | speed |
    | metric   | meters   | km/h  |
    | imperial | feet     | mph   |

Scenario Outline: Precision
  Given the user's units are <units>
  When a location with altitude <altitude> and speed <speed> is shown
  Then altitude is shown as <shown altitude> and speed as <shown speed>

  Examples:
    | units    | altitude  | speed    | shown altitude | shown speed |
    | metric   | 123.456 m | 3.6 m/s  | 123.5 m        | 13 km/h     |
    | imperial | 123.456 m | 3.6 m/s  | 405 ft         | 8 mph       |

Scenario: Copied coordinates in a comma-decimal locale
  Given the user's locale is de-DE
  And the chosen format is DD
  When the user copies the coordinates
  Then the latitude and longitude values each use a comma as the decimal separator

@pending @POS-82
Scenario: Coordinate separator in a comma-decimal locale
  Given the user's locale is de-DE
  And the chosen format is DD
  When the user copies the coordinates
  Then latitude and longitude are separated by something other than a comma

@pending @POS-75
Scenario: UTM outside its range
  Given the chosen format is UTM
  When a location north of 84°N is shown
  Then the coordinates show dashes
  And the copy and share actions are disabled

Scenario: Map stays available outside UTM's range
  Given the chosen format is UTM
  When a location north of 84°N is shown
  Then the map action is enabled

Scenario: Location fails
  Given the location screen is open
  When observing location fails
  Then the coordinates and update time show loading indicators
  And the map, share, and copy actions are disabled

@pending @POS-74
Scenario: Attributes while location fails
  Given the location screen is open
  When observing location fails
  Then the attributes show loading indicators

@pending @POS-74
Scenario: Location recovers after a failure
  Given observing location has failed
  When location becomes available again
  Then the screen shows the location without the user doing anything

@pending @POS-83
Scenario: No location can be obtained
  Given location services are off
  When the location screen is shown
  Then the screen tells the user why no location is shown

Scenario: Copy coordinates
  Given a location is loaded
  When the user taps copy
  Then the clipboard contains the coordinates formatted for copy in the chosen format
  And a confirmation snackbar is shown

Scenario: Share text
  Given a location is loaded
  When the user taps share
  Then the shared text is the coordinates formatted for copy in the chosen format

@pending @POS-81
Scenario: Share sheet
  Given a location is loaded
  When the user taps share
  Then Android's share sheet opens

@pending @POS-81
Scenario: Share with no receiving app
  Given a location is loaded
  And no installed app can receive text
  When the user taps share
  Then an error is shown instead of a crash

@pending @POS-72
Scenario: Map URI in a comma-decimal locale
  Given a location is loaded
  And the user's locale is de-DE
  And an installed app handles geo: URIs
  When the user taps the map action
  Then a geo: URI is opened with the location's coordinates in plain decimal degrees, using periods and no symbols

Scenario: Map pin label
  Given a location is loaded
  And an installed app handles geo: URIs
  When the user taps the map action
  Then the pin is labeled with the date and time the location was determined

Scenario: Open in a map with no map app installed
  Given a location is loaded
  And no installed app handles geo: URIs
  When the user taps the map action
  Then an error dialog explains that no map app is available

Scenario Outline: Leaving the screen stops observation
  Given the location screen is observing location
  When <cause> hides the screen for longer than 5 seconds
  Then the screen stops observing location

  Examples:
    | cause                    |
    | backgrounding the app    |
    | opening the help screen  |

Scenario Outline: Brief interruptions keep observation
  Given the location screen is observing location
  When <cause> hides the screen for less than 5 seconds
  Then location updates continue without restarting

  Examples:
    | cause                    |
    | rotating the device      |
    | backgrounding the app    |
    | opening the help screen  |

@pending @POS-80
Scenario: Switching tabs keeps observation
  Given the location screen is observing location
  When the user switches to another tab and back within 5 seconds
  Then location updates continue without restarting

Scenario: Returning after observation stopped
  Given the screen stopped observing location after being hidden for longer than 5 seconds
  When the user returns to the location screen
  Then the screen shows loading indicators until a new location arrives

Scenario: Turning keep-screen-on on
  Given the location screen is shown
  When the user turns the keep-screen-on toggle on
  Then the screen stays awake
  And a confirmation snackbar is shown

Scenario: Turning keep-screen-on off
  Given the keep-screen-on toggle is on
  When the user turns it off
  Then the screen can sleep
  And a confirmation snackbar is shown

Scenario: Leaving the screen with keep-screen-on enabled
  Given the keep-screen-on toggle is on
  When the user leaves the location screen
  Then the screen can sleep

Scenario: Keep-screen-on starts off
  Given the user turned the keep-screen-on toggle on in an earlier session
  When the app cold starts
  Then the keep-screen-on toggle is off

Scenario: Keep-screen-on survives process recreation
  Given the keep-screen-on toggle is on
  When Android reclaims and restores the app
  Then the keep-screen-on toggle is still on

Scenario Outline: Returning with keep-screen-on enabled
  Given the user left the location screen by <cause> with the keep-screen-on toggle on
  When the user returns to the location screen
  Then the screen stays awake again

  Examples:
    | cause                    |
    | rotating the device      |
    | backgrounding the app    |
    | opening the help screen  |

@pending @POS-80
Scenario: Returning after switching tabs with keep-screen-on enabled
  Given the user switched tabs with the keep-screen-on toggle on
  When the user returns to the location tab
  Then the screen stays awake again

Scenario: Help
  Given the location screen is shown
  When the user taps the help button
  Then the location help screen opens

@pending @POS-77
Scenario: Help matches the specs
  Given the location help screen is open
  When the user reads its explanation of speed and bearing
  Then it states the same 0.3 m/s threshold as the Location spec

Scenario: Permission-required screen
  Given precise location permission is not granted
  When the user opens the app
  Then the permission-required screen is shown
  And it links to the privacy policy

@pending @POS-87
Scenario: Permission screen privacy statement
  Given the permission-required screen is shown
  When the user reads its privacy statement
  Then it says the app never stores or sends a location, and that a location leaves the device only when the user copies, shares, or opens it in a map

@pending @POS-78
Scenario: Privacy policy link
  Given the permission-required screen is shown
  When the user taps the privacy policy link
  Then the privacy policy opens from the main branch

@pending @POS-69
Scenario: Permission request
  Given the user has never been asked for location permission
  When the user taps the grant button
  Then Android's permission prompt is shown on every supported Android version
  And it asks for precise and approximate location together

@pending @POS-73
Scenario: Grant after denying once
  Given the user has denied location permission once
  And Android can still show its permission prompt
  When the user taps the grant button
  Then Android's permission prompt is shown again

@pending @POS-73
Scenario: Grant after a permanent denial
  Given the user has permanently denied location permission
  When the user taps the grant button
  Then the app's system settings open

Scenario: Only approximate location granted
  Given the user grants approximate location but not precise location
  When the permission result returns
  Then the permission-required screen remains visible
  And no location is observed

@pending @POS-76
Scenario: Crash reports
  Given a location is shown
  When a crash report is recorded
  Then it contains no coordinates
```
