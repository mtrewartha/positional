buildscript {
    dependencies {
        classpath(libs.javapoet.get()) // https://github.com/google/dagger/issues/3068
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.google.ksp) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false
    alias(libs.plugins.protobuf) apply false
    alias(libs.plugins.testLogger) apply false
    alias(libs.plugins.autonomous.dependencyAnalysis)
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

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
