---
name: kotlin-metro
description: Use when writing, reviewing, or debugging Kotlin code that uses the Metro dependency-injection compiler plugin (dev.zacsweers.metro), such as @DependencyGraph, @Binds, @Provides, @SingleIn, @ContributesBinding, createGraph, or graph extensions; when porting Dagger, Hilt, Anvil, or kotlin-inject code to Metro; or when Metro reports missing bindings, scope errors, or unprocessed upstream declarations.
---

# Metro

Most Metro mistakes come from Dagger, Anvil, or kotlin-inject habits, or from pre-1.0 Metro knowledge. This page lists them. For everything else, see the [docs](https://zacsweers.github.io/metro/latest/). When porting, read [Differences from other DI frameworks](https://zacsweers.github.io/metro/latest/differences/).

Checked against **Metro 1.5.1**. A version in parentheses is where a feature first appeared.

If the `kotlin-libraries` skill is available, its `references/internal-di.md` covers using Metro inside a library without exposing it to consumers.

**Don't guess the version.** Run `gh api repos/ZacSweers/metro/releases/latest --jq .tag_name`. The project's Kotlin version must be listed for that release on the [compatibility page](https://zacsweers.github.io/metro/latest/compatibility/). The `dev.zacsweers.metro` plugin adds the runtime dependency.

## Common mistakes

| Mistake | Fix |
|---|---|
| Scope on a `@Binds` (Dagger allows it) | Error: "@Binds declarations may not have scopes." Scope the `@Inject` class or a `@Provides` instead. |
| Class bound via `@Binds` lacks `@Inject` | It needs `dev.zacsweers.metro.Inject` (or an interop annotation). `@Contributes*` classes don't (0.7.0). |
| Scoped binding in an unscoped graph, or one with a different scope | Error. Scope the graph: `@DependencyGraph(AppScope::class)`. |
| `Provider<T>` for a new instance per call | Use `() -> T`. `Provider<T>` warns (1.0.0-RC3). `Lazy` is `kotlin.Lazy`. |
| Dagger-style `@Module`, `@BindsInstance`, `@Component.Factory`, subcomponents | Put `@Binds`/`@Provides` in the graph or a `@ContributesTo` interface. Pass runtime values as `@Provides` parameters on a `@DependencyGraph.Factory`. Use `@GraphExtension` for subcomponents. `@BindingContainer` exists but is rarely needed. |
| `createGraph<G>()` on a graph that has a factory | `createGraphFactory<G.Factory>().create(...)` |
| `internal` `@ContributesBinding` class, with the graph in another module | It must be public, unless the contributing module sets `metro { generateContributionProviders.set(true) }` (0.13.0, Kotlin 2.3.20+). Then the graph sees only the bound type. |
| Contributing module reachable from the graph module only through an `implementation` dependency | Contributions merge only from the graph module's compile classpath, so they are silently dropped. Depend on the module directly. To find these, run `checkMainMetroHiddenDependencies` (Android: `check<Variant>MetroHiddenDependencies`) on the graph module (1.5.0). |
| `jakarta.inject` annotations ignored | `metro { interop { includeJakarta() } }` (or `includeJavax()`). Neither is in the interop docs. `includeDagger()` is for Dagger code only: it also adds Dagger runtime interop and the `interop-dagger` dependency. |
| Expecting Metro to build an `@Inject` class from a jar it didn't compile | Error `UNPROCESSED_UPSTREAM_DECLARATION` (1.3.0). Write a `@Provides`, which takes precedence. If Dagger built the jar, `includeDagger()` reuses its factories. |
| jakarta `@Singleton` class (a public library type, or code mid-migration from Dagger) in an `AppScope`-only graph | It's a separate scope (`IncompatiblyScopedBindings` error). Put both on the graph: `@Singleton @DependencyGraph(AppScope::class)`. The class keeps only `@Singleton`. |

## Shape of a correct graph

Compiles as-is with Metro 1.5.1.

```kotlin
import dev.zacsweers.metro.*

interface HttpClient

@SingleIn(AppScope::class)          // scope the class, not the @Binds
@Inject
class OkHttpClientImpl(@Named("baseUrl") private val baseUrl: String) : HttpClient

@Inject
class Request

@Inject
class UserRepository(private val client: HttpClient, private val newRequest: () -> Request)

@DependencyGraph(AppScope::class)
interface AppGraph {
    val userRepository: UserRepository

    @Binds val OkHttpClientImpl.bind: HttpClient

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides @Named("baseUrl") baseUrl: String): AppGraph
    }
}

val graph = createGraphFactory<AppGraph.Factory>().create("https://example.com")
```

## Going deeper

To look up an error message, diagnostic ID, or DSL option, clone `ZacSweers/metro` into a temp directory at the release tag. Search its `docs/`, `CHANGELOG.md`, and `gradle-plugin/.../MetroPluginExtension.kt`. That file lists every DSL option, including undocumented ones.
