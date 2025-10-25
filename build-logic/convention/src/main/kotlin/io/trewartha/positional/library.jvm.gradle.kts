package io.trewartha.positional

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("io.trewartha.positional.testFixtures.jvm")
    id("io.trewartha.positional.dependencyAnalysis")
    id("io.trewartha.positional.testLogger")
}

java {
    sourceCompatibility = JVM_SOURCE_COMPATIBILITY
    targetCompatibility = JVM_SOURCE_COMPATIBILITY
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(FREE_COMPILER_ARGS)
        jvmTarget = JVM_TARGET
    }
    explicitApi = EXPLICIT_API_MODE
}