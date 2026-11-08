---
name: kotlin-libraries
description: "Use when creating or changing a Kotlin library module (any module other modules or projects depend on, including the com.android.library modules of a multi-module app), or adding, changing, or reviewing its public API: visibility (internal vs public, explicit API mode), ABI/API dumps (apiDump, apiCheck, updateKotlinAbi), Gradle api vs implementation, artifact layout (core, extensions, testing fakes), choosing how consumers instantiate types (object, constructor, factory function, Builder, singleton, @Inject), or wiring the library's internal dependencies (hand-wired or with an internal DI graph) while keeping DI framework types out of its public API."
---

# Kotlin Library API Design

A library's public API is a long-lived contract: keep it as small as possible, keep it free of the library's implementation choices (including its DI framework), and make every public type testable through a constructor or interface seam.

These are opinionated defaults. If the project already has its own conventions (module naming, internal DI setup, API validation tooling), follow those and apply the principles below. The exception is DI agnosticism (below): whatever the project's habits, a library never exposes DI framework constructs to its consumers.

## Library vs. Application Modules

A library module is any module that other modules or projects depend on: a published artifact, or a `com.android.library` or `kotlin("jvm")` module inside a multi-module app. These rules apply to all of them.

Choosing a DI framework and assembling the object graph is an **application** concern. Only the application module (`com.android.application`, or the JVM module with the `main` entry point) does it. That's where Hilt's `@HiltAndroidApp` and `@Module`/`@InstallIn` classes, Koin modules, or a Dagger component live, binding library types through the libraries' public constructors, factory functions, and Builders.

So a library module ships no DI modules, components, or bindings for consumers to install, even when every module in the build uses the same framework. If a library module contains such wiring (e.g., a Hilt `@InstallIn` module), move it to the application module. A library may still use a DI framework *internally*, as long as that wiring stays `internal` and invisible to consumers.

## Rules

- **Information Hiding:** Hide implementation details from consumers with `internal`. Prefer [explicit API mode](https://kotlinlang.org/docs/api-guidelines-simplicity.html#use-explicit-api-mode) (`kotlin { explicitApi() }`) so every public declaration is a deliberate choice; the examples in [examples.md](references/examples.md) are written that way.
- **Tracked Public API:** Track the public API in source control as a text dump, using whichever tool the project already uses:
  - The [binary compatibility validator](https://github.com/Kotlin/binary-compatibility-validator) plugin (`org.jetbrains.kotlinx.binary-compatibility-validator`) generates `.api` files with the `apiDump` and `apiCheck` tasks. It is in maintenance mode but still widely used and stable.
  - The Kotlin Gradle plugin's built-in [ABI validation](https://kotlinlang.org/docs/gradle-binary-compatibility-validation.html) (Kotlin 2.2.0+, experimental, opt-in via `@OptIn(ExperimentalAbiValidation::class)` in `kotlin { abiValidation { … } }`). The task names changed between releases: `updateLegacyAbi`/`checkLegacyAbi` in 2.2, then `updateKotlinAbi`/`checkKotlinAbi` later. Run `./gradlew tasks --all | grep -i abi` to see which names the project has.
- **Regenerate the API dump with every public API change:** Every public API change, including a visibility change such as `internal` → `public`, must come with the regenerated dump. Run the update task for the module (e.g., `./gradlew :<module>:apiDump`) and commit the result with the code change. The check task (normally wired into `check`) fails when the committed dump no longer matches the API. Never edit dump files by hand.
- **Gradle Dependency Configurations:** Use `api` (and similar configurations) only for dependencies that are part of the public API, e.g., types that appear in public signatures; use `implementation` for everything else. This makes `jakarta.inject:jakarta.inject-api` an `api` dependency whenever public types carry its annotations, because consumers' DI processors recognize them only if they're on the consumer's compile classpath. A DI framework's runtime stays `implementation`.
- **DI Agnosticism:** Libraries must not require consumers to use any DI framework. DI framework-specific types (modules, components or graphs, bindings, scopes, etc.) must never appear in a library's public API. A library may use a DI framework *internally* to wire its own collaborators (the examples assume it does), but that wiring must stay strictly `internal`. The only DI-related annotations allowed in the public API are the vendor-neutral JSR-330 ones: `@Inject`, `@Singleton`, and qualifiers (`@Named` or a custom `@Qualifier` annotation). Use `jakarta.inject`. When wiring the library's internal collaborators, or deciding whether to use an internal DI framework at all, read [references/internal-di.md](references/internal-di.md). It also lists which consumer framework versions recognize `jakarta.inject`.
- **`@Inject` on Public Constructors:** Annotate a public constructor with `@Inject` **only when every parameter can plausibly be satisfied by a JSR-330 DI graph** — it's a convenience for consumers whose framework supports it. Misplaced `@Inject` is worse than no `@Inject` — it advertises a contract the type cannot honor, breaking consumer builds at code-gen time. Leave `@Inject` off when any parameter is:
  - An unqualified primitive or `String`. Dagger ignores Kotlin default values — it generates a Factory that calls the constructor with every parameter resolved from the graph, so an unqualified `Float = 1f` requires a `Float` binding rather than using the default.
  - A runtime-provided value (a user ID, a request payload, a file path chosen at runtime). These belong to the call site, not the DI graph; if the consumer wants them in DI, they wrap with their own assisted-injection factory.

  Consumers who want such a type in DI bind it explicitly in their own graph. Qualified primitives and `String`s are fine, since consumers bind against the qualifier, as are other types consumers genuinely produce in their graphs.

## Module Structure

A good default is to structure each library as a set of Gradle modules (artifacts) under a common parent directory. If the project already names or groups its modules differently, follow that.

- **`core`** — The primary artifact containing the library's public API (interfaces, data types, core logic). Almost every library has a `core` artifact.
- **Extension artifacts** — Optional artifacts for functionality related to the core but separable from it, typically because it ties to a specific third-party dependency (e.g., `analytics:firebase`, `network:okhttp`) or is a feature area not all consumers need.
- **`testing`** — Fakes for public interfaces that consumers are likely to want to fake, so their tests don't depend on production implementations or third-party SDKs.

A library with only one artifact drops the `core` suffix (e.g., `coroutines`, not `coroutines:core`).

## Instantiation Patterns

Two questions decide how to expose a type:

1. **Is the public type a concrete class, or an `interface` hiding a single implementation?**
2. **What is the instance's scope — a fresh instance per request, or a single shared instance? And if shared, who owns that scope: the *library* (because the type's contract requires exactly one) or the *consumer's* DI graph?**

The answers map to a seam:

| Public type                                     | Scope                              | Seam                                                                                     |
|-------------------------------------------------|------------------------------------|------------------------------------------------------------------------------------------|
| Concrete, no consumer-provided dependencies     | Library-owned singleton            | `public object`                                                                          |
| Concrete, has dependencies or per-instance      | Consumer-owned                     | Public constructor ([Constructor Rules](#constructor-rules))                             |
| Interface, single impl, per-instance            | Consumer-owned                     | Factory function `Foo(…)` ([below](#interfaces-with-a-single-production-implementation)) |
| Interface, single impl, many or optional params | Consumer-owned                     | Nested `Foo.Builder()` ([below](#interfaces-with-a-single-production-implementation))    |
| Interface, single impl, singleton by contract   | Library-owned, overridable default | Top-level `public val foo` ([below](#semantic-singletons))                               |

The **scope** column is the axis most often gotten wrong. A type that a consumer merely *treats* as a singleton is **consumer-owned** — the library hands over instances and the consumer's DI graph scopes them (that is what `@Singleton` on a public constructor advertises). Only when "exactly one instance" is part of what the type *means* does the library own the scope; that case is a [semantic singleton](#semantic-singletons).

> **Code examples for every pattern in this section are in [examples.md](references/examples.md). Read the matching example before writing or reviewing code that uses a pattern.** In those examples, `/* … acquired via the internal DI graph */` marks where the library obtains an instance from its internal DI graph — the snippets are illustrative, not compilable. [references/internal-di.md](references/internal-di.md) shows how to fill those placeholders in. If the library doesn't use a DI framework internally, construct the production collaborator directly at that point (e.g., `DefaultFooDependencyA()`); the seams and visibility rules stay the same.

### Constructor Rules

All dependencies, regardless of visibility modifier, must arrive through constructors. A type must never reach for its own dependencies inline — in property initializers, `init` blocks, or method bodies. Internal collaborators are acquired from the library's internal DI graph, and the only places that acquisition may happen are the **instantiation seams**: a public (or secondary) constructor, a factory function, or `Builder.build()`. This rule has two consequences:

- **Public constructors accept only public types.** They cannot reference `internal` types in their parameter lists, even as defaulted parameters, because that would force the type into the public API.
- **Internal primary constructor + public secondary constructor.** Whenever a public class has internal dependencies, declare an `internal` primary constructor that takes them and a `public` secondary constructor that acquires the production collaborators from the internal DI graph and delegates to the primary. The internal constructor is the substitution point for tests, which call it directly with fakes. It must exist whenever there are internal dependencies to swap, even if the public constructor takes no consumer dependencies of its own. (Kotlin `internal` constructors are still public in JVM bytecode, so Java callers can technically reach them. They are hidden from Kotlin consumers and excluded from API dumps.) Examples: [consumer and internal dependencies](references/examples.md#constructor-consumer-and-internal-dependencies), [only internal dependencies](references/examples.md#constructor-only-internal-dependencies).

When a public class has no internal dependencies, a single public constructor is sufficient: its parameter list is already the substitution point. Example: [no internal dependencies](references/examples.md#constructor-no-internal-dependencies).

### `object` Example

A singleton library type with no consumer-provided dependencies is just a Kotlin `object`. Internal collaborators are private properties acquired from the internal DI graph, initialized lazily if construction is expensive.

Example: [`object`](references/examples.md#object).

`object` is the one exception to the constructor rules: it has no constructor, so tests can't substitute its internal collaborators. **That doesn't excuse testing it less thoroughly.** If full coverage requires substituting collaborators, use a class with the constructor pattern instead.

### Shared Internal State Across Multiple Types

When two or more types need the same internal collaborator (e.g., a repository and a cache sharing a database connection), scope it as a singleton in the internal DI graph. Each type's primary constructor takes it as a parameter and the secondary constructor acquires the shared instance, so the graph owns its lifetime and no hand-rolled holder object is needed. Tests pass a fake through each type's `internal` primary constructor.

Example: [shared internal state](references/examples.md#shared-internal-state-across-multiple-types).

### Interfaces with a Single Production Implementation

Sometimes a library exposes a type as an `interface` even though it has exactly **one** production implementation. The interface earns its keep for three reasons:

1. It gives consumers a single type to depend on.
2. It hides the implementation, preserving freedom to change it later.
3. It provides a test seam, so the library can ship a fake (see [Testing](#testing)).

In this situation, exposing the implementation as `public` is a mistake: consumers would have to know about *both* the interface and the implementation, wire them together in their own DI graph (since the library stays DI-agnostic), and might inject the concrete type directly instead of the interface.

Instead, **keep the sole implementation `internal`** and expose the interface through one of the seams below — chosen by the same constructor-vs-builder criteria as everywhere else. Both return the **interface** type, never the implementation.

**Factory function** (the constructor's analog) — a PascalCase function whose name matches the interface, used when a constructor would otherwise be the natural seam.

Example: [factory function](references/examples.md#factory-function-for-an-interface).

**Nested `Builder`** — used when the [builder criteria](#builder-example) apply. It is nested inside the interface so it reads as `LocationManager.Builder()`, and `build()` returns the interface.

The implementation is built by the library's DI graph or directly, so it never needs a `public` constructor. Consumers who use a DI framework bind the interface in their own graph by delegating to the factory function or `Builder`.

### Builder Example

Use a Builder when there are many optional configuration parameters, or when configuration reads more naturally as a series of `set*` calls than as one constructor. For an interface, nest it in the interface and have `build()` return the interface, never the implementation (see [single-implementation pattern](#interfaces-with-a-single-production-implementation)).

Example: [nested `Builder`](references/examples.md#builder-nested-in-an-interface).

The consumer-provided builder values (`timeout`, `logger`, `credentials`) are passed into the graph as inputs; any purely internal collaborators the implementation needs are acquired from the graph alongside them.

### Semantic Singletons

Some interfaces are singletons by *contract*, not by preference, e.g., an event bus that every participant must share. The library must own that guarantee; it can't leave "scope this as a singleton" to the consumer's DI graph, because a consumer who forgot would silently break the semantics.

Expose the shared instance as a **public top-level `val`**, named `<camelCaseInterfaceName>`, held lazily so the library creates exactly one default instance.

Example: [semantic singleton](references/examples.md#semantic-singleton).

The shared instance is a **wired-in default, not a mandate**. Types that need the bus take it as a public parameter defaulting to the property — as `CameraController` does in the example — so the one shared instance is used automatically but can be overridden: a test can pass a fake, and a consumer that genuinely needs a different bus can pass its own. This is the interface analog of a `public object`: a library-owned singleton, but one that stays swappable because its type is an interface.

Reach for a semantic singleton **only** when the type's contract genuinely requires one instance. It is not the default for "types that are usually singletons" — a library-owned singleton is a process-wide global, harder to isolate in tests and impossible to run two of, so it must earn that cost. When the consumer could reasonably own the scope, expose the type via a constructor or factory function instead and let their DI graph decide.

### Android `Context`

Android library constructors accept `Context` as any other consumer-provided dependency. When the library only needs application-scoped state (preferences, app-wide singletons, services that don't care about UI scope), the public secondary constructor resolves to `applicationContext` before delegating to the internal primary, so the rest of the class never sees an activity-scoped `Context`. When the library genuinely needs an activity-scoped `Context` (e.g., to launch UI, observe configuration changes, or access activity-scoped resources), pass the `Context` through unmodified and document the expectation on the public constructor.

Example: [Android `Context`](references/examples.md#android-context).

## Testing

Public interfaces that consumers are likely to stub or swap should ship a public fake (e.g., `FakeLocationManager`) in the `testing` artifact, while the production implementation stays `internal` (see the [single-implementation pattern](#interfaces-with-a-single-production-implementation)). The library's own tests construct internal types directly with test doubles, since `internal` is visible in the module's test source set.