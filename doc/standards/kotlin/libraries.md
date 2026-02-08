### Library Standards

- **Create README.md files** that describe the purpose and usage of each library module. For feature
  modules, these should be located at the top-level directory of the module. For core/data modules,
  these should be located in the top level directory of the module. At minimum, README files should
  include the following sections:
    - Overview: A brief overview of the library's purpose and functionality
    - Getting Started: Brief instructions on how to use the library, including Gradle dependencies
      and key interfaces
    - Test Fixtures: Description of test fixtures provided by the library and how to use them
- **Version libraries using Semantic Versioning (SemVer)** (when libraries are published to
  repositories) to clearly communicate the impact of changes to consumers.
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
