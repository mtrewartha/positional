### Architecture Standards

- **Clean Architecture-inspired Core, Data, Feature, and App Modules**:
    - Apps are broken into discrete modules that fit into one of four categories:
        1. **Core modules:** Define a tightly-scoped cohesive chunk of low-level functionality (e.g.
           networking, storage, date formatting) that might be useful in a wide variety of other
           modules. They may depend on other core modules but cannot depend on app, feature, or data
           modules. When possible, these should be pure Kotlin modules with no Android dependencies
           (including the Android Gradle Plugin), but they may apply the Android Gradle Plugin if
           need be. They are grouped under a "core" directory at the project root.
        2. **Data modules:** Model a single domain's data and the abstractions that provide access
           to the data (e.g. repositories and use cases). They may depend on core modules and other
           data modules but must never depend on app or feature modules. When possible, these should
           be pure Kotlin modules with no Android dependencies (including the Android Gradle
           Plugin), but they may apply the Android Gradle Plugin if need be. They are grouped under
           a "data" directory at the project root.
        3. **Feature modules:** Provide unique UI functionality (e.g. Composables, Fragments,
           ViewModels) or other framework-specific constructs (e.g. Workers, Services,
           BroadcastReceivers) that expose data and functionality to the user. Internally, these
           modules employ Model-View-Intent (MVI) architecture and unidirectional data flow. Feature
           modules almost always depend on data and core modules but cannot depend on other feature
           modules. They are grouped under a "feature" directory at the project's root.
        4. **App module:** This module stitches together the feature, data, and core modules into a
           cohesive app. Common responsibilities of this modules include:
            - Setting up logging, crash reporting, and analytics
            - Defining the navigation graph
            - Defining the dependency injection graph

           This module lives in the "app" directory at the project's root.
    - Example project structure:
        ```
        /app <- Gradle subproject
        /core
          /network <- Gradle subproject
          /storage <- Gradle subproject
        /data
          /user <- Gradle subproject
          /product <- Gradle subproject
        /feature
          /login <- Gradle subproject
          /productDetails <- Gradle subproject
        ```
- **High Cohesion, Low Coupling**: Each module should have a single, well-defined responsibility,
  should depend on as few other modules as possible, and should expose a minimal public API.
- **Flavors for Single App Differentiation Only**: Do not use flavors to create multiple apps from a
  single codebase. Flavors should only be used to differentiate versions of a single app (e.g. free
  vs paid, or internal vs public).
