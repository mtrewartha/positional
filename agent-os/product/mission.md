# Product Mission

## Pitch

Positional is a minimalist, privacy-focused Android app that helps users access essential
information about their location with a clean, distraction-free experience that respects privacy and
doesn't collect or share personal data.

## Users

### Primary Customers

- **Outdoor Enthusiasts**: Hikers, campers, and adventurers who need reliable location and compass
  data without relying on internet connectivity or privacy-invasive apps
- **Privacy-Conscious Users**: Individuals who want basic location utilities without tracking, ads,
  or data collection
- **Technical Professionals**: Surveyors, photographers, and field workers who need precise
  coordinate information in multiple formats
- **Casual Users**: Anyone seeking simple, straightforward answers to "where am I?" or "when does
  the sun set today?"

### User Personas

**Sarah, the Weekend Hiker** (28-45)

- **Role:** Outdoor recreation enthusiast
- **Context:** Regular day hikes and weekend camping trips in regional parks
- **Pain Points:** Doesn't want heavy navigation apps that drain battery, track location history, or
  require cellular data. Needs quick coordinate checks and compass bearings for safety.
- **Goals:** Quick access to current location coordinates for emergency purposes, compass heading to
  maintain direction, sunset time to plan return trips

**David, the Privacy Advocate** (25-55)

- **Role:** Tech-savvy professional who values digital privacy
- **Context:** Uses Android daily but carefully manages app permissions and data sharing
- **Pain Points:** Frustrated by apps that require excessive permissions, contain ads, or monetize
  location data. Wants basic utilities without compromises.
- **Goals:** Access essential location services without surveillance or data collection, support
  open-source software, maintain control over personal information

**Maria, the Field Surveyor** (30-50)

- **Role:** Land surveyor or field technician
- **Context:** Professional work requiring precise coordinate information
- **Pain Points:** Needs coordinates in multiple formats (decimal degrees, DMS, UTM, MGRS) without
  expensive specialized equipment for initial assessments
- **Goals:** Quick coordinate references in various formats, reliable compass with magnetic
  declination correction, ability to export or share location data

## The Problem

### Privacy Invasion in Basic Utilities

Most location and compass apps unnecessarily collect user data, track location history, serve ads,
or require accounts and internet connectivity. Users seeking simple answers to "where am I?" or
"which direction am I facing?" are forced to compromise their privacy or wade through cluttered
interfaces filled with features they don't need.

**Our Solution:** Positional provides exactly three core functions - location, compass, and sun
times - with zero data collection beyond anonymous crash reports. Location data is shown to the user
and immediately discarded unless explicitly exported.

### Bloated Apps for Simple Tasks

Users don't need social features, maps, route planning, or gamification to check their coordinates
or find north. The proliferation of feature-bloated apps makes simple tasks unnecessarily complex.

**Our Solution:** We maintain a minimalist interface focused exclusively on displaying information
clearly. No social features, no unnecessary complexity, no distractions.

### Lack of Transparency and Trust

Proprietary apps with vague privacy policies create uncertainty about how personal location data is
handled. Users have no way to verify claims about privacy protection.

**Our Solution:** Open source codebase under GPL-3.0 license, intentionally simple privacy policy,
and transparent development on GitHub allow users to verify exactly what the app does.

## Differentiators

### Privacy by Design

Unlike Google Maps, navigation apps, or fitness trackers that store location history, Positional
collects location data only to display it immediately to the user. No accounts, no cloud sync, no
tracking, no location history. The only data collected is anonymous crash diagnostics to improve
stability.

This results in users having confidence their movements aren't being tracked, monetized, or stored.

### Minimalist Philosophy

Unlike feature-rich apps like Gaia GPS or AllTrails that bundle maps, routes, social features, and
subscriptions, Positional does exactly three things: shows location, provides compass heading, and
displays solar times.

This results in faster app performance, smaller app size, lower battery consumption, and immediate
access to information without navigating complex menus.

### Open Source Transparency

Unlike proprietary alternatives where users must trust privacy claims, Positional's entire codebase
is publicly available under GPL-3.0 license on GitHub. Anyone can audit the code, verify privacy
claims, contribute improvements, or fork the project.

This results in verifiable privacy guarantees and community-driven development that serves users
rather than shareholders.

### Device Flexibility

Unlike apps that require Google Play Services, Positional offers both Android Open Source Project
(AOSP) and Google Mobile Services (GMS) flavors, ensuring users on de-Googled devices, custom ROMs,
or privacy-focused Android distributions can access full functionality.

This results in broader device compatibility and respect for users' choice of Android distribution.

### No Cost Barriers

Unlike professional surveying tools or premium apps with paywalls, Positional is completely free
with no in-app purchases, subscriptions, or premium features.

This results in universal access to basic location utilities regardless of economic circumstances.

## Key Features

### Core Features

- **Multi-Format Location Display:** View current coordinates in decimal degrees,
  degrees-minutes-seconds (DMS), Universal Transverse Mercator (UTM), and Military Grid Reference
  System (MGRS) formats, with one-tap export to maps or sharing capabilities
- **Digital Compass:** Real-time heading display with automatic magnetic declination correction
  using device location, showing both magnetic and true north headings
- **Solar Times Calculator:** View sunrise, sunset, and twilight times (civil, nautical,
  astronomical) for any selected date based on current location

### Privacy Features

- **Zero Location Tracking:** Location data is displayed immediately and discarded - never stored,
  uploaded, or shared without explicit user action
- **Anonymous Crash Reports:** Only non-identifying crash diagnostics collected to improve app
  stability (release builds only, GMS flavor)
- **No Accounts or Registration:** No user accounts, no logins, no personal information required
- **Minimal Permissions:** Only requests location and sensor permissions necessary for core
  functionality

### Technical Features

- **Offline Operation:** Core functionality works without internet connectivity (location via
  GPS/GNSS, compass via magnetometer, sun calculations local)
- **Clean Architecture:** Multi-module structure with clear separation between domain logic and UI
  for maintainability and testability
- **Modern Android Development:** Built with Jetpack Compose, Material 3 design, and unidirectional
  data flow (UDF) for smooth, responsive UI
- **Flavor Variants:** Android Open Source Project (AOSP) flavor for Google Play Services-free
  devices, Google Mobile Services (GMS) flavor for enhanced location accuracy and anonymous crash
  reporting to Firebase
