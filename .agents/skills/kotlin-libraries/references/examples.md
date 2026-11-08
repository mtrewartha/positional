# Kotlin Library API Examples

Code examples for the `kotlin-libraries` skill. The rules and the reasoning behind each pattern live in [SKILL.md](../SKILL.md); each heading below names the SKILL.md section it illustrates.

`/* … acquired via the internal DI graph */` marks where the library obtains an instance from its internal DI graph — the snippets are illustrative, not compilable. If the library doesn't use a DI framework internally, construct the production collaborator directly at that point (e.g., `DefaultFooDependencyA()`). `@Inject` is `jakarta.inject.Inject`; imports are omitted.

## Contents

- [object](#object)
- [Constructor, no internal dependencies](#constructor-no-internal-dependencies)
- [Constructor, consumer and internal dependencies](#constructor-consumer-and-internal-dependencies)
- [Constructor, only internal dependencies](#constructor-only-internal-dependencies)
- [Shared internal state across multiple types](#shared-internal-state-across-multiple-types)
- [Factory function for an interface](#factory-function-for-an-interface)
- [Builder nested in an interface](#builder-nested-in-an-interface)
- [Semantic singleton](#semantic-singleton)
- [Android Context](#android-context)

## object

SKILL.md section: `object` Example.

```kotlin
// Public API
public object FooRepository {
    private val internalDep: FooDependencyA by lazy { /* FooDependencyA acquired via the internal DI graph */ }

    public fun doFoo(): String = internalDep.compute()
}

// All internal from here down

internal interface FooDependencyA {
    fun compute(): String
}
```

## Constructor, no internal dependencies

SKILL.md section: Constructor Rules.

```kotlin
// Public API
public class BarRecorder @Inject constructor(
    private val analytics: BarAnalytics,
) : Recorder {
    override fun record(event: Event) {
        analytics.log(event.name)
    }
}
```

## Constructor, consumer and internal dependencies

SKILL.md section: Constructor Rules.

```kotlin
// Public API
public class BazRepository internal constructor(
    private val config: BazConfig,
    private val depA: BazDependencyA,
) {
    @Inject
    public constructor(config: BazConfig) : this(config, /* BazDependencyA acquired via the internal DI graph */)

    public fun doBaz(): String = depA.compute(config)
}

public data class BazConfig(public val apiKey: String)

// All internal from here down

internal interface BazDependencyA {
    fun compute(config: BazConfig): String
}
```

## Constructor, only internal dependencies

SKILL.md section: Constructor Rules.

```kotlin
// Public API
public class FooRepository internal constructor(
    private val depA: FooDependencyA,
    private val depB: FooDependencyB,
) {
    @Inject
    public constructor() : this(
        /* FooDependencyA acquired via the internal DI graph */,
        /* FooDependencyB acquired via the internal DI graph */,
    )

    public fun doFoo(): String = depA.compute() + depB.compute()
}

// All internal from here down

internal interface FooDependencyA {
    fun compute(): String
}

internal interface FooDependencyB {
    fun compute(): String
}
```

## Shared internal state across multiple types

SKILL.md section: Shared Internal State Across Multiple Types.

```kotlin
// Public API
public class FooRepository internal constructor(
    private val connection: FooConnection,
) {
    @Inject
    public constructor() : this(/* FooConnection acquired via the internal DI graph */)

    public fun doFoo(): String = connection.query("foo")
}

public class FooCache internal constructor(
    private val connection: FooConnection,
) {
    @Inject
    public constructor() : this(/* FooConnection acquired via the internal DI graph */)

    public fun clear(): Unit = connection.clear()
}

// All internal from here down

internal interface FooConnection {
    fun query(key: String): String
    fun clear()
}
```

## Factory function for an interface

SKILL.md section: Interfaces with a Single Production Implementation.

```kotlin
// Public API
public interface LocationManager {
    public fun current(): Location
}

// Factory function: PascalCase, named after the interface; returns the interface, never the implementation.
public fun LocationManager(context: Context): LocationManager =
    /* DefaultLocationManager acquired via the internal DI graph */

// All internal from here down

internal class DefaultLocationManager(
    private val context: Context,
    private val source: LocationSource,
) : LocationManager {
    override fun current(): Location = source.last()
}

internal interface LocationSource {
    fun last(): Location
}
```

## Builder nested in an interface

SKILL.md section: Builder Example.

```kotlin
// Public API
public interface QuxRepository {
    public fun doQux(): String

    public class Builder {
        private var timeout: Long = 30_000L
        private var logger: Logger? = null
        private lateinit var credentials: Credentials

        public fun setTimeout(timeout: Long): Builder = apply { this.timeout = timeout }
        public fun setLogger(logger: Logger): Builder = apply { this.logger = logger }
        public fun setCredentials(credentials: Credentials): Builder = apply { this.credentials = credentials }

        // Builder values are passed in as graph inputs; the graph constructs the implementation.
        public fun build(): QuxRepository = /* DefaultQuxRepository acquired via the internal DI graph */
    }
}

// All internal from here down

internal class DefaultQuxRepository(
    private val timeout: Long,
    private val logger: Logger?,
    private val credentials: Credentials,
) : QuxRepository {
    override fun doQux(): String = TODO()
}
```

## Semantic singleton

SKILL.md section: Semantic Singletons.

```kotlin
// Public API
public interface EventBus {
    public val events: Flow<Event>
    public fun publish(event: Event)
}

// Single production implementation hidden from public API
internal class DefaultEventBus : EventBus {
    override val events: Flow<Event> = TODO()
    override fun publish(event: Event): Unit = TODO()
}

// The library-managed default: one shared instance, wired in by default.
public val eventBus: EventBus by lazy { /* DefaultEventBus acquired via the internal DI graph */ }

// A collaborator that publishes to the bus takes it as a defaulted parameter: callers get the one
// shared bus for free, but a test — or a consumer needing a different bus — can pass a different one.
public class CameraController(
    private val bus: EventBus = eventBus,
) {
    public fun moveCamera(event: Event): Unit = bus.publish(event)
}
```

## Android Context

SKILL.md section: Android `Context`.

```kotlin
public class StorageRepository internal constructor(
    private val context: Context,
    private val keyValueStore: KeyValueStore,
) {
    @Inject
    public constructor(context: Context) : this(
        context.applicationContext,
        /* KeyValueStore acquired via the internal DI graph */,
    )

    public fun read(key: String): String? = keyValueStore.read(key)
}

// All internal from here down

internal interface KeyValueStore {
    fun read(key: String): String?
}
```
