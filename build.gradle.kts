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
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.google.firebase.crashlytics) apply false
    alias(libs.plugins.protobuf) apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
