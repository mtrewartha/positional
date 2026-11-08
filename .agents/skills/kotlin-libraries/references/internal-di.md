# Dependency Injection Inside a Kotlin Library

How a library satisfies its **own internal** dependencies without forcing a DI framework on consumers. [SKILL.md](../SKILL.md) covers the public API surface (`public` vs `internal`, instantiation seams, `@Inject` on public constructors). This file fills in the mechanism behind the `/* … acquired via the internal DI graph */` placeholders in [examples.md](examples.md).

**First, check what the library already uses.** Look in its build files for a DI framework (e.g., the `dev.zacsweers.metro` plugin, or `com.google.dagger`, `me.tatarka.inject`, or `io.insert-koin` dependencies) and follow it. If it uses none, start with hand-wiring (below).

The rules work with any compile-time DI framework the project uses (Metro, Dagger, kotlin-inject…). **Code examples use [Metro](https://zacsweers.github.io/metro/)**: `@DependencyGraph` is its graph/component, `createGraph`/`createGraphFactory` instantiate one, and `@SingleIn(AppScope::class)` is its scope. Translate them to the project's framework. For framework-specific details, see that framework's docs. If the `kotlin-metro` skill is available, read it for Metro specifics.

## Hand-wire or use a framework?

An internal DI framework is optional. In a small library, the seam can just call constructors: `public fun LocationManager(): LocationManager = DefaultLocationManager(DefaultLocationSource())`. Bring in a framework when hand-wiring gets heavy, for example:

- many collaborators, or deep construction chains;
- scoped instances shared by several public types;
- consumer input that has to be threaded through many constructors;
- bindings contributed from extension artifacts.

The principles below apply either way. "The graph" can be a hand-written `internal` object that holds the wiring.

## Principles

- **Consumers never need a DI framework.** Nothing may force a DI framework, or a particular one, onto a consumer.
- **DI-framework types never appear in the public API.** Graphs/components, modules, `@Provides`/`@Binds`, scopes, contributions, and graph-holder properties are all `internal`.
- **Only JSR-330 annotations from `jakarta.inject` may appear in the public API:** `@Inject`, `@Singleton`, and qualifiers (`@Named` or a custom `@Qualifier`). They are vendor-neutral conveniences for consumers who use a JSR-330 framework. Internally, use the framework's own annotations where it has them (Metro and kotlin-inject do). Keeping the two sets separate keeps the mechanism out of the API.
  - `jakarta.inject` is the maintained successor to `javax.inject`. Consumers' frameworks recognize it in these versions:
    - Dagger 2.41+ reads `@Inject`.
    - Hilt 2.56+ (2.57.1+ for `@Singleton`).
    - Metro, with `includeJakarta()`.
    - Koin Annotations 2.2+.
    - Guice 7+, which is jakarta-only.
  - Consumers on older Dagger/Hilt won't see the annotations. They can still bind the types by hand.
  - Don't mix `javax.inject` and `jakarta.inject` in one library.
- **The DI framework's runtime is `implementation`, never `api`.** The annotation artifact `jakarta.inject:jakarta.inject-api` is different: it is `api`, because the annotations are part of the public signature, and a consumer's DI processor only recognizes an annotation if its class is on the consumer's compile classpath.
- **The seam is the only bridge.** A public constructor, top-level property, factory function, or `Builder.build()` is where the library pulls collaborators from the internal graph. Production code never reaches into the graph from property initializers, `init` blocks, or method bodies. The one exception is a `public object`: it has no constructor, so its property initializers read the graph.

## The internal graph

Every seam reads from an `internal` graph (a Metro `@DependencyGraph`, a Dagger or kotlin-inject `@Component`). The graph builds whatever the seam needs while keeping implementations `internal`:

- for a single-implementation interface, it exposes the sole implementation *as the public type*;
- for a public class's secondary constructor, it supplies the internal collaborators that get passed to the primary constructor.

```kotlin
@DependencyGraph(AppScope::class)
internal interface LocationGraph {
    val locationManager: LocationManager                 // exposes only the public type

    @Binds val DefaultLocationManager.bind: LocationManager
    @Binds val DefaultLocationSource.bind: LocationSource
}

@Inject                                                  // the framework's @Inject, not jakarta
internal class DefaultLocationManager(private val source: LocationSource) : LocationManager

@SingleIn(AppScope::class)                               // Metro: scope the class, never the @Binds
@Inject
internal class DefaultLocationSource : LocationSource
```

Two independent choices express every seam:

- **Per binding: unscoped or scoped.** An unscoped binding builds a fresh instance on every read. A scoped binding is cached in the graph *instance*, so every read returns the same one.
- **Per graph: held once or created per call.** This depends on one question: does the graph need a consumer-provided value (a `Context`, config, `Builder` values) to *construct* its collaborators? If not, hold it once, which is the default. If it does, create it per call.

## Hold the graph once (default)

When the graph needs no consumer input, create it once, lazily, in an `internal` top-level `val`:

```kotlin
internal val locationGraph: LocationGraph by lazy { createGraph<LocationGraph>() }
```

That one graph covers every lifetime. You choose the lifetime per binding:

- **Per-instance type** (factory function or public constructor): read an unscoped binding, so every call gets a fresh instance.
  ```kotlin
  public fun LocationManager(): LocationManager = locationGraph.locationManager
  ```
- **Per-instance type sharing an internal singleton:** the graph above already does this. Every `LocationManager` is new, but they all share the one scoped `DefaultLocationSource`.
- **Semantic singleton:** scope the implementation so the instance is shared wherever it is injected. Expose it through a `by lazy` val, never a factory function, because a factory function promises a fresh instance per call. The scope guarantees the identity; `by lazy` only defers graph access.
  ```kotlin
  public val locationManager: LocationManager by lazy { locationGraph.locationManager }
  ```
- **`public object`:** reads the held graph in its property initializers.
- **Several types sharing one collaborator:** each type reads the same scoped binding in its secondary constructor.
  ```kotlin
  public class FooRepository internal constructor(private val connection: FooConnection) {
      @Inject public constructor() : this(fooGraph.fooConnection)   // jakarta.inject.Inject
  }
  public class FooCache internal constructor(private val connection: FooConnection) {
      @Inject public constructor() : this(fooGraph.fooConnection)
  }
  ```

If a consumer value isn't needed to *construct* the graph's collaborators, it doesn't force a per-call graph. Pass it straight to the public constructor, and keep reading collaborators from the held graph:

```kotlin
public class BazRepository internal constructor(
    private val config: BazConfig,
    private val depA: BazDependencyA,
) {
    @Inject public constructor(config: BazConfig) : this(config, bazGraph.bazDependencyA)
}
```

Tests skip the graph entirely: they call the `internal` primary constructor with fakes.

## Create the graph per call (consumer input needed for construction)

When a collaborator's construction needs a consumer value, pass that value in through the graph's factory (Metro `@DependencyGraph.Factory`, Dagger `@Component.Factory`, kotlin-inject component constructor parameters). The value is baked into the graph *instance*, so a held graph would freeze the first caller's value. Create the graph inside the seam instead:

```kotlin
@DependencyGraph
internal interface QuxGraph {
    val quxRepository: QuxRepository
    @Binds val DefaultQuxRepository.bind: QuxRepository

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(
            @Provides timeout: Long,
            @Provides logger: Logger?,
            @Provides credentials: Credentials,
        ): QuxGraph
    }
}

// In QuxRepository.Builder:
public fun build(): QuxRepository =
    createGraphFactory<QuxGraph.Factory>().create(timeout, logger, credentials).quxRepository
```

A factory function or public secondary constructor that needs a consumer value works the same way. For example, on Android, with a graph whose factory takes a `Context`:

```kotlin
public fun LocationManager(context: Context): LocationManager =
    createGraphFactory<ContextLocationGraph.Factory>().create(context.applicationContext).locationManager
```

> **Scoping footgun.** Scopes cache per graph *instance*, not per process. A per-call graph therefore gives every call its own copy of each scoped binding, so the "singleton" is fresh every time. If a per-call seam also needs a truly shared collaborator, put that collaborator in a held no-input graph and read it from there.

## Multi-artifact libraries: contributing from an extension

For a library split into a **core** artifact plus optional **extension** artifacts, an extension can contribute bindings to a scope instead of the core hand-writing them (Metro `@ContributesBinding`/`@ContributesTo`, Anvil, kotlin-inject-anvil). Keep the contributed types `internal`.

Contributions merge only into graphs **compiled with the contributing module on their compile classpath**. A graph declared in core, which cannot depend on its extensions, never sees them. Declare the graph in the extension, or in an artifact that depends on both. Whether an `internal` implementation can be contributed across modules depends on the framework, so check its docs.

## `@Inject` on a public constructor the graph also builds

A public constructor may carry `jakarta.inject.Inject` when every parameter can plausibly come from a JSR-330 graph. If the library's own graph also needs to construct that class, the framework must recognize `jakarta.inject`:

- Dagger does this natively.
- Metro needs annotation interop turned on: `metro { interop { includeJakarta() } }`.
- kotlin-inject doesn't read JSR-330 annotations at all. Provide the class explicitly.

If that class is also annotated `@jakarta.inject.Singleton`, the internal graph must declare that scope as well as its own. Otherwise the graph rejects the binding as incompatibly scoped; Metro reports `IncompatiblyScopedBindings`.

```kotlin
@DependencyGraph(AppScope::class)
@jakarta.inject.Singleton            // needed for the graph to build the public @Singleton type
internal interface FooGraph {
    val fooClient: FooClient
}
```

In every other pattern above the two annotation sets never meet, because the seam is a plain factory function or secondary constructor.

## Checklist

- The graph, its bindings and modules, the implementation classes, and any graph-holder `val` are all `internal`.
- The DI runtime is an `implementation` dependency. `jakarta.inject-api` is `api`.
- Public annotations come only from `jakarta.inject`, never `javax.inject`.
- No framework-specific annotation or type appears on a `public` declaration. If the project tracks its public API with an API dump (Kotlin ABI validation / binary-compatibility-validator `.api` file), confirm that the dump shows only the public types and their seams.
