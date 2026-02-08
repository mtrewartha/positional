### Models Standards

- **Data Classes**: Use Kotlin data classes for models to automatically provide useful methods like
  `copy()`, `toString()`, `equals()`, and `hashCode()`.
- **Immutability**: Always use `val` for properties. Always use immutable types for those properties
  as well (e.g. `List` instead of `MutableList`).
- **Wrappers Around Ambiguous Primitives**: Use wrappers (ideally inline value classes) around
  primitive types that would otherwise be ambiguous to improve type safety, e.g.:
    - `Instant` for timestamps instead of `Long`
    - `Duration` for time intervals instead of `Long` or `Int`
- **Pure Kotlin**: Do not use platform-specific or library-specific types in models other than those
  intended to be ubiquitous across the Kotlin ecosystem (e.g. `Duration` or `Instant`). e.g. Avoid
  using Android's `Uri` and instead use `String` or define an inline/value type for it.
- **Avoid Transport/Storage Concerns**: Never define transport or storage concerns in models, e.g.
  conversions to database/ORM types, JSON representations, etc. Use separate mappers/adapters for
  those concerns.
