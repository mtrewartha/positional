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
    id("org.jetbrains.kotlinx.kover.aggregation") version "0.9.2"
}

kover {
    enableCoverage()

    reports {
        excludesAnnotatedBy.addAll(
            "dagger.*",
            "*.Generated*"
        )
        excludedClasses.addAll(
            "dagger.*",
            "hilt_aggregated_deps.*",
            "*Hilt_*",
            "*_Hilt*",
            "*.*_*Injector*",
            "*_*Factory*",
            "io.trewartha.positional.BuildConfig",
        )
    }
}

include(
    ":app",
    ":core:api",
    ":core:ui",
    ":feature:compass:api",
    ":feature:compass:impl",
    ":feature:compass:ui",
    ":feature:location:api",
    ":feature:location:impl",
    ":feature:location:ui",
    ":feature:settings:api",
    ":feature:settings:impl",
    ":feature:settings:ui",
    ":feature:sun:api",
    ":feature:sun:impl",
    ":feature:sun:ui",
)
