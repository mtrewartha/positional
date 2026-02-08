package io.trewartha.positional

import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

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

dependencies {
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.runner.junit5)
}

tasks.withType<Test> {
    useJUnitPlatform()
}