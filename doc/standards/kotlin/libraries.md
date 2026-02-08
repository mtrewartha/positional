### Library Standards

- **Use Kotlin's explicit API mode** to ensure public APIs are well-defined and intentional.
- **Document public APIs** using KDocs to provide clear explanations of their purpose and usage.
- **Hide implementation details** by using `internal` visibility for classes, functions, and
  properties that don't need to be exposed publicly.
- **Minimize external dependencies** to reduce the risk of dependency conflicts for consumers of the
  library.
- **Provide test fixtures** so dependent modules can easily test their integrations with the
  library. These should live in the `testFixtures` source set.
- **Provide ProGuard/R8 rules** if the library requires specific code shrinking or obfuscation
  configurations.
- **Maintain backward compatibility** for public APIs whenever possible to avoid breaking dependent
  modules.
- **Use deprecation annotations** to signal upcoming breaking changes and provide migration paths
  for consumers.
- **Regularly update dependencies** to ensure the library remains secure and compatible with the
  latest versions of its dependencies.
