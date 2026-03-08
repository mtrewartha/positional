### Tech Stack

#### Framework & Runtime

- **Application Framework:** Android
- **Language/Runtime:** Kotlin
- **Build System & Dependency Manager:** Gradle

#### Key Libraries & Tools

- **Concurrency:** [Kotlinx.coroutines](https://github.com/Kotlin/kotlinx.coroutines)
- **Dependency Injection:** [Hilt](https://dagger.dev/hilt/)
- **Key-Value Stores:**
  Protobuf-based [DataStore](https://developer.android.com/topic/libraries/architecture/datastore)
- **Time:** kotlin.time and [Kotlinx.datetime](https://github.com/Kotlin/kotlinx-datetime)
- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose) w/ Material Design

#### Testing & Quality

- **Test Frameworks:**
    - JUnit 4 (instrumented tests)
    - JUnit 5 (non-instrumented unit tests)
    - Kotest (assertions, matchers, property-based testing, and BehaviorSpec style)
    - Turbine (Flow testing)
- **Linting/Formatting:** Detekt

#### Deployment & Infrastructure

- **CI/CD:** GitHub Actions
- **Hosting:** Google Play

#### Third-Party Services

- **Crash Reporting:** Firebase
