buildscript {
    dependencies {
        classpath(libs.javapoet.get()) // https://github.com/google/dagger/issues/3068
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.google.ksp) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false
    alias(libs.plugins.protobuf) apply false
    alias(libs.plugins.testLogger) apply false
    alias(libs.plugins.autonomous.dependencyAnalysis)
    alias(libs.plugins.kotlinx.kover)
}

dependencies {
    kover(project(":app"))
    kover(project(":model:compass"))
    kover(project(":model:core"))
    kover(project(":model:location"))
    kover(project(":model:settings"))
    kover(project(":model:sun"))
    kover(project(":data:compass"))
    kover(project(":data:location"))
    kover(project(":data:settings"))
    kover(project(":data:sun"))
    kover(project(":ui:compass"))
    kover(project(":ui:core"))
    kover(project(":ui:design"))
    kover(project(":ui:location"))
    kover(project(":ui:sun"))
    kover(project(":ui:settings"))
}

dependencyAnalysis {
    issues {
        all {
            onUsedTransitiveDependencies {
                // Ignore transitive dependency suggestions for now, but we may revisit this later.
                severity("ignore")
            }
        }
    }
    structure {
        bundle("kotest-assertions-core") { includeGroup("io.kotest") }
    }
}

kover {
    reports {
        filters {
            excludes {
                packages(
                    "dagger.hilt.internal.aggregatedroot.codegen",
                    "hilt_aggregated_deps",
                    "io.trewartha.positional.ui.*"
                )
                annotatedBy(
                    "androidx.compose.runtime.Composable",
                    "dagger.*"
                )
                classes(
                    "*Hilt_*",
                    "io.trewartha.positional.BuildConfig",
                )
            }
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
