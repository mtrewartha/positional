buildscript {
    dependencies {
        classpath(libs.javapoet.get()) // https://github.com/google/dagger/issues/3068
    }
}

plugins {
    // Specify any plugins here that convention plugins would apply. Convention plugins don't have
    // access to the version catalog like we do here, so it keeps things tidier if they only have
    // to use the plugin IDs, even if using manual strings for those is a bit ugly (we can't avoid
    // that).
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.jvm) apply false

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
