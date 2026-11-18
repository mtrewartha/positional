# Feature: Location

## Blueprint

### Context
The Location feature is the app's single source of device location. Every feature that needs to know where the device is (the location screen, compass, and sun) gets it from here, so location is acquired, modeled, and released the same way everywhere.

The device's location is held only in memory while something in the app is observing it, and is discarded afterwards. This is the app's core privacy promise, and the rest of the design follows from it. How location is presented to and shared by the user is covered by the [Location UI spec](ui/spec.md).

Behavior marked _(pending POS-n)_ is intended but not yet implemented; the marker covers only the sentence it follows, the linked issue tracks the change, and the marker is removed in the same commit as the fix.

#### Glossary
- **Location attributes:**
  - **Accuracy:** the estimated horizontal accuracy radius of the location, at 68% confidence
  - **Altitude:** the location's height above the WGS84 reference ellipsoid
  - **Speed:** the horizontal rate of change of location with respect to time (speed over ground)
  - **Bearing:** the horizontal direction of travel in degrees clockwise from true north, regardless of device orientation

### Architecture
- **Where it lives:** `feature/location`, plus the location test double and random location fixtures that other features' tests use in `feature/location/src/testFixtures`.
- **Behavior:**
  - Acquiring location:
    - The Location feature requests location updates from the platform at high accuracy every second, only while something in the app is observing location, and stops them as soon as nothing is.
    - Observers receive each modeled location when the platform reports it.
    - In the Play Services build, observing location without location permission fails: the observer receives the failure, and no location updates are requested.
    - The build without Play Services fails the same way. _(pending [POS-68](https://linear.app/trewartha/issue/POS-68))_
    - Observing location with only approximate location permission fails the same way in both builds. _(pending [POS-85](https://linear.app/trewartha/issue/POS-85))_
    - The Play Services build (`gms`) uses the Google Play Services fused location provider.
    - If Play Services rejects the location request, the Play Services build falls back to Android's built-in location service.
    - It falls back the same way when Play Services location fails for any other reason. _(pending [POS-67](https://linear.app/trewartha/issue/POS-67))_
    - The build without Play Services (`aosp`) uses Android's built-in fused provider on Android 12 and later, and GPS before that.
  - Modeling locations:
    - Every location has coordinates and a timestamp. Every other attribute is absent when the platform didn't report it or it isn't meaningful.
    - Speed, bearing, and bearing accuracy are absent below 0.3 m/s, where direction of travel is indistinguishable from noise.
    - Bearing and bearing accuracy are also absent when the platform doesn't report speed.
    - Speed accuracy is present only when speed is. _(pending [POS-71](https://linear.app/trewartha/issue/POS-71))_
    - A location whose platform timestamp isn't positive is stamped with the current time.
    - A location whose coordinates aren't finite numbers is dropped on its own.
    - No attribute value that isn't a finite number reaches observers: depending on the attribute, it's absent or the location is dropped. _(pending [POS-84](https://linear.app/trewartha/issue/POS-84))_
    - Every location with finite coordinates carries the magnetic declination at its coordinates and time, computed with an altitude of 0 when altitude is unknown.
- **Depended on by:**
  - [Location UI](ui/spec.md) shows each location's coordinates and attributes.
  - Compass relies on each location's magnetic declination to show true north.
  - Sun relies on each location's coordinates to compute solar times.
- **Constraints:**
  - A location is held only in memory, for as long as something observes it, and leaves this feature only by being delivered to observers.
  - Observers are responsible for ensuring precise location permission is granted before they observe location.
  - Both builds behave identically to the rest of the app. _(pending [POS-68](https://linear.app/trewartha/issue/POS-68))_
  - Build differences stay inside location acquisition.
  - Precise and approximate location permissions are both declared, because Android requires them to be requested together.
  - The app installs only on devices with location hardware; GPS and network location are each optional.
  - The location test double and random location fixtures produce only locations that follow this spec's modeling rules. _(pending [POS-79](https://linear.app/trewartha/issue/POS-79))_

## Contract

### Definition of Done
- [ ] Location updates are requested while something observes location.
- [ ] Location updates are requested at high accuracy.
- [ ] Location updates are requested every second.
- [ ] Location updates stop when nothing observes location.
- [ ] Observers receive each modeled location when the platform reports it.
- [ ] The Play Services build requests the Play Services fused location provider.
- [ ] The build without Play Services requests the fused provider on Android 12 and later.
- [ ] The build without Play Services requests the GPS provider on Android 11 and earlier.
- [ ] The Play Services build reports a failure to the observer when location is observed without location permission.
- [ ] The Play Services build requests no location updates when location is observed without location permission.
- [ ] The build without Play Services reports a failure to the observer when location is observed without location permission. _(pending [POS-68](https://linear.app/trewartha/issue/POS-68))_
- [ ] The build without Play Services requests no location updates when location is observed without location permission. _(pending [POS-68](https://linear.app/trewartha/issue/POS-68))_
- [ ] Both builds report a failure to the observer when location is observed with only approximate location permission. _(pending [POS-85](https://linear.app/trewartha/issue/POS-85))_
- [ ] The Play Services build delivers locations from Android's built-in location service after Play Services rejects the location request.
- [ ] The Play Services build delivers locations from Android's built-in location service after Play Services location fails for any other reason. _(pending [POS-67](https://linear.app/trewartha/issue/POS-67))_
- [ ] Speed, bearing, and bearing accuracy are absent from locations below 0.3 m/s.
- [ ] Bearing and bearing accuracy are absent when the platform doesn't report speed.
- [ ] Speed accuracy is absent whenever speed is absent. _(pending [POS-71](https://linear.app/trewartha/issue/POS-71))_
- [ ] A location whose platform timestamp isn't positive is stamped with the current time.
- [ ] Attributes the platform doesn't report are absent.
- [ ] Every location with finite coordinates carries a magnetic declination.
- [ ] Magnetic declination is computed with an altitude of 0 when altitude is unknown.
- [ ] A location whose coordinates aren't finite numbers is dropped without stopping later locations.
- [ ] No attribute value that isn't a finite number reaches observers. _(pending [POS-84](https://linear.app/trewartha/issue/POS-84))_
- [ ] The app declares both precise and approximate location permissions.
- [ ] The app requires location hardware, and requires neither GPS nor network location specifically.
- [ ] The location test double and random location fixtures produce only locations that follow the modeling rules. _(pending [POS-79](https://linear.app/trewartha/issue/POS-79))_

### Regression Guardrails
- Locations are held only in memory and leave this feature only by being delivered to observers. Enforced in review by two checks: `feature/location`'s build file adds no storage, database, or networking libraries; and no log message or logged exception in `feature/location` includes location values (audited in [POS-76](https://linear.app/trewartha/issue/POS-76)).
- Only `feature/location` requests location from Android. Enforced by a search for Android's location APIs (`LocationManager`, `FusedLocationProviderClient`) outside `feature/location`.

### Scenarios
```gherkin
Scenario: Observing location
  Given precise location permission is granted
  When something in the app starts observing location
  Then location updates are requested
  And they're requested at high accuracy
  And they're requested every second

Scenario: Delivering locations
  Given location is being observed
  When the platform reports a location
  Then the observer receives it

Scenario: Observation stops
  Given location is being observed
  When nothing in the app observes location any more
  Then location updates are stopped

Scenario: Provider (Play Services build)
  Given the Play Services build is running
  When location is observed
  Then location updates are requested from the Play Services fused location provider

Scenario Outline: Provider choice (build without Play Services)
  Given the build without Play Services is running
  And the device runs Android <version>
  When location is observed
  Then location updates are requested from the <provider> provider

  Examples:
    | version     | provider |
    | 12 or later | fused    |
    | 11 or lower | GPS      |

Scenario: Observing without permission (Play Services build)
  Given the Play Services build is running
  And no location permission is granted
  When something in the app starts observing location
  Then the observer receives a failure
  And no location updates are requested

@pending @POS-68
Scenario: Observing without permission (build without Play Services)
  Given the build without Play Services is running
  And no location permission is granted
  When something in the app starts observing location
  Then the observer receives a failure
  And no location updates are requested

@pending @POS-85
Scenario: Observing with only approximate permission
  Given only approximate location permission is granted
  When something in the app starts observing location
  Then the observer receives a failure

Scenario: Play Services rejects the location request (Play Services build)
  Given Play Services rejects the location request
  When location is observed
  Then locations come from Android's built-in location service instead

@pending @POS-67
Scenario: Play Services location fails another way (Play Services build)
  Given location is being observed through Play Services
  When Play Services location fails for a reason other than rejecting the request
  Then locations come from Android's built-in location service instead

Scenario: Stationary device
  Given the platform reports a speed below 0.3 m/s
  When the location is modeled
  Then speed, bearing, and bearing accuracy are absent

Scenario: No speed reported
  Given the platform reports a bearing but no speed
  When the location is modeled
  Then bearing and bearing accuracy are absent

@pending @POS-71
Scenario: Speed accuracy without speed
  Given the platform reports a speed accuracy but no usable speed
  When the location is modeled
  Then speed accuracy is absent

Scenario: Missing timestamp
  Given the platform reports a location with a timestamp that isn't positive
  When the location is modeled
  Then the location's timestamp is the current time

Scenario Outline: Unreported attribute
  Given the platform reports a location without <attribute>
  When the location is modeled
  Then <attribute> is absent

  Examples:
    | attribute           |
    | horizontal accuracy |
    | altitude            |
    | altitude accuracy   |
    | speed               |
    | speed accuracy      |
    | bearing             |
    | bearing accuracy    |

Scenario: Magnetic declination
  Given the platform reports a location with finite coordinates
  When the location is modeled
  Then it carries a magnetic declination

Scenario: Missing altitude
  Given the platform reports a location without altitude
  When the location is modeled
  Then magnetic declination is computed with an altitude of 0

Scenario: Non-finite coordinates
  Given the platform reports a location whose coordinates aren't finite numbers
  When the location arrives
  Then that location is dropped
  And subsequent locations continue to arrive

@pending @POS-84
Scenario Outline: Infinite speed values
  Given the platform reports an infinite <attribute>
  When the location is modeled
  Then <attribute> is absent

  Examples:
    | attribute      |
    | speed          |
    | speed accuracy |

Scenario: Declared permissions
  Given the app is installed
  When its declared permissions are listed
  Then they include precise and approximate location

Scenario Outline: Device requirements
  Given a device <hardware>
  When the user looks for the app in the store
  Then the app is <availability>

  Examples:
    | hardware                                  | availability |
    | without location hardware                 | unavailable  |
    | with location hardware but no GPS         | available    |
    | with location hardware but no network location | available |

@pending @POS-79
Scenario: Test fixtures follow the modeling rules
  Given another feature's test uses the location test double or random location fixtures
  When it obtains a location from them
  Then that location follows this spec's modeling rules
```
