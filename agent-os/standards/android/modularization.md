## Modularization best practices

- **Break apps into top-level app, feature, and core modules**: Modules should each be their own
  Gradle subproject.
    - App modules should live in the "app" directory. Their code should stitch together features
      into a cohesive app. Common responsibilities of these modules include defining the navigation
      graph and dependency injection graph as well as initialization of services like crash
      reporting, logging, and analytics.
    - Feature modules should live under a "feature" directory. Their code should define unique
      features offered by the app. Feature modules may depend on and build upon other feature
      modules. They may also depend on core modules.
    - Core modules should live under a "core" directory. Their code should define tightly scoped
      functionality that might be found in a wide variety of apps (e.g. networking, storage, date
      formatting).
    - If any given module has UI-related code, that should be placed in a nested "ui" submodule.
- **Use Gradle script convention plugins**: Keep build logic DRY by utilizing convention plugins
  that then get applied to Gradle subprojects as necessary.
- **Prefer the Kotlin JVM Gradle plugin over Android Gradle Plugin**: If a module does not need the
  Android framework, avoid usage of the Android Gradle Plugin for performance's sake.