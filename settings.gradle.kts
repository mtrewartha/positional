pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    @Suppress("UnstableApiUsage")
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    }
}

rootProject.name = "positional"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

plugins {
    id("org.jetbrains.kotlinx.kover.aggregation") version "0.9.8"
}

kover {
    enableCoverage()

    reports {
        excludesAnnotatedBy.addAll(
            "*.Generated*"
        )
        excludedClasses.addAll(
            "io.trewartha.positional.BuildConfig",
        )
    }
}

include(
    ":app",
    ":core:di",
    ":core:error",
    ":core:measurement",
    ":core:test",
    ":core:ui",
    ":feature:compass",
    ":feature:compass:ui",
    ":feature:location",
    ":feature:location:ui",
    ":feature:settings",
    ":feature:settings:ui",
    ":feature:sun",
    ":feature:sun:ui",
)
