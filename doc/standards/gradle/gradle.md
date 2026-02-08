### Gradle Standards

- **Convention Plugins**: Define these in `build-logic` to avoid repeated build logic.
- **Careful Use of `api` Dependency Configuration**: Only expose dependencies that are actually part
  of the public API of a module.
- **Configuration Cache and Build Cache Compatibility**: Maximize configuration and build speed by
  ensuring logic is compatible with the caches.
- **Multi-Platform Build Logic**: Ensure Gradle build logic (in build-logic or otherwise) works when
  executed on Windows, macOS, Linux, and any other Unix-like systems.
