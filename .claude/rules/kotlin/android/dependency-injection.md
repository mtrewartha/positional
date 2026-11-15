---
paths:
  - "**/src/**/*.kt"
---

# Dependency Injection Rules

Metro with Anvil-style aggregation into a single `AppScope`. For general Metro usage, use the
`kotlin-metro` skill.

- **Contribute, don't edit the graph**: Modules contribute bindings with `@ContributesTo`,
  `@ContributesBinding`, and `@ContributesIntoMap`; adding a binding never requires touching the
  `app` graph.
- **Visibility**: `@ContributesTo` interfaces are `public`. `@ContributesBinding` and
  `@ContributesIntoMap` classes are `internal` (`generateContributionProviders` exposes only the
  bound type), unless another module references the class directly.
- **ViewModels**: Annotate with `@ContributesIntoMap(AppScope::class)`, `@ViewModelKey`, and
  `@Inject`; obtain them in Composables with `metroViewModel()` (`dev.zacsweers.metrox.viewmodel`).
- **Scoping**: Use `@SingleIn(AppScope::class)` only for bindings that must be singletons.
