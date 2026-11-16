# Feature: Location

## Blueprint

### Context
Location is the app's single source of device position. Every feature that needs to know where the
device is (the location screen, compass, and sun) gets it from here, so position is acquired,
converted, and released the same way everywhere.

Position is held only in memory while something in the app is observing it, and is discarded
afterwards. This is the app's core privacy promise, and the rest of the design follows from it. How
position is presented and shared is covered by the [Location UI spec](ui/spec.md).

#### Glossary
- **Fix:** a single determination of the device's position at a point in time.
- **Location attributes:**
  - **Accuracy:** the estimated horizontal accuracy radius of the location, at 68% confidence
  - **Altitude:** the location's height above the WGS84 reference ellipsoid
  - **Speed:** the horizontal rate of change of location with respect to time (speed over ground)
  - **Bearing:** the horizontal direction of travel, regardless of device orientation

### Architecture
- **Where it lives:** `feature/location`, plus the position test double that other features' tests
  use in `feature/location/src/testFixtures`.
- **Behavior:**
  - Acquiring position:
    - Position updates are requested at high accuracy every second, only while something in the
      app is observing position, and released as soon as nothing is.
    - Position updates begin once location permission is granted, without observation having to
      restart.
    - The Play Services build (`gms`) uses Google Play Services fused location. When Play Services
      can't provide location, it falls back to Android's built-in location service.
    - The build without Play Services (`aosp`) uses Android's built-in fused provider on Android 12
      and later, and GPS before that.
  - Fixes:
    - Every fix has coordinates and a timestamp. Every other attribute is absent when the platform
      didn't report it or it isn't meaningful.
    - Speed, bearing, and bearing accuracy are absent below 0.3 m/s, where direction of travel is
      indistinguishable from noise.
    - A fix whose platform timestamp isn't positive is stamped with the current time.
    - Every fix carries the magnetic declination at its position, computed with an altitude of 0
      when altitude is unknown.
    - A fix the platform reports but that can't be converted is dropped on its own.
- **Depended on by:**
  - [Location UI](ui/spec.md) shows each fix's coordinates and attributes.
  - Compass relies on each fix's magnetic declination to show true north.
  - Sun relies on each fix's coordinates and timestamp to compute solar times.
- **Constraints:**
  - A position exists only in memory. Nothing in this feature writes a position to disk,
    preferences, or logs, or sends it over the network.
  - Logs describe lifecycle events (start, stop, fallback, dropped fix) and never include
    coordinates.
  - Both builds behave identically to the rest of the app. Build differences stay inside position
    acquisition.
  - Precise and approximate location permissions are both declared, because Android expects them
    together. The app relies on precise location only (see the [Location UI spec](ui/spec.md)).

## Contract

### Definition of Done
- [ ] Both builds deliver position updates while observed and release them when observation stops.
- [ ] The Play Services build delivers fixes from Android's built-in location service after Play
      Services fails.
- [ ] Both builds begin delivering fixes, without being restarted, once permission is granted
      mid-observation.
- [ ] Speed, bearing, and bearing accuracy are absent from fixes below 0.3 m/s.
- [ ] A fix whose platform timestamp isn't positive is stamped with the current time.
- [ ] Magnetic declination is computed with an altitude of 0 when altitude is unknown.

### Regression Guardrails
- A position is never persisted or transmitted by this feature (see Constraints).
- Every feature gets device position from this feature.
- No position updates are requested while nothing observes position.
- Fixes that fail conversion are dropped individually and never stop position updates.
- Attributes the platform doesn't know are absent, never zero or another placeholder value.

### Scenarios
```gherkin
Scenario: Observing position
  Given precise location permission is granted
  When something in the app starts observing position
  Then high-accuracy position updates are requested
  And each fix the platform reports is delivered to the observer

Scenario: Observation stops
  Given position is being observed
  When nothing in the app observes position any more
  Then position updates are released

Scenario: Permission granted while observing
  Given position is being observed without location permission
  When the user grants precise location permission
  Then position updates begin without restarting observation

Scenario: Play Services can't provide location (Play Services build)
  Given Play Services fails to provide location
  When position is observed
  Then fixes come from Android's built-in location service instead

Scenario: Stationary device
  Given the platform reports a speed below 0.3 m/s
  When the fix is converted
  Then speed, bearing, and bearing accuracy are absent

Scenario: Unconvertible fix
  Given the platform reports a fix that cannot be converted
  When the fix arrives
  Then that fix is dropped
  And subsequent fixes continue to arrive

Scenario: Missing altitude
  Given the platform reports a fix without altitude
  When the fix is converted
  Then altitude is absent
  And magnetic declination is still present
```
