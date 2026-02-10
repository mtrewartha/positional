package io.trewartha.positional

import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
    id("com.android.library")
    id("io.trewartha.positional.dependencyAnalysis")
    id("io.trewartha.positional.testLogger")
}

android {
    compileOptions {
        // Up to Java 17 APIs are available through desugaring
        // https://developer.android.com/studio/write/java11-minimal-support-table
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    compileSdk = 36

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions += "androidVariant"

    productFlavors {
        create("aosp") { dimension = "androidVariant" }
        create("gms") { dimension = "androidVariant" }
    }

    // The resource prefix is derived from the module name,
    // so resources inside ":core:module1" must be prefixed with "core_module1_"
    resourcePrefix = path.split("""\W""".toRegex()).drop(1).distinct().joinToString(separator = "_")
        .lowercase() + "_"

    testFixtures {
        enable = true
    }

    testOptions {
        managedDevices {
            localDevices {
                val pixel7Api33AospAtd = TestDevice("Pixel 7", 33, "aosp-atd")
                val pixel7Api33GoogleAtd = TestDevice("Pixel 7", 33, "google-atd")
                val testDevices = listOf(pixel7Api33AospAtd, pixel7Api33GoogleAtd)
                for (testDevice in testDevices) {
                    create(testDevice.name) {
                        device = testDevice.device
                        apiLevel = testDevice.apiLevel
                        systemImageSource = testDevice.systemImageSource
                    }
                }
                val aospDevices = listOf(pixel7Api33AospAtd)
                val gmsDevices = listOf(pixel7Api33GoogleAtd)
                groups {
                    val groups = mapOf("aosp" to aospDevices, "gms" to gmsDevices)
                    for ((groupName, groupDevices) in groups) {
                        create(groupName) {
                            for (device in groupDevices) {
                                targetDevices.add(localDevices[device.name])
                            }
                        }
                    }
                }
            }
        }
        unitTests {
            isIncludeAndroidResources = true
            all { it.useJUnitPlatform() }
        }
    }
}

dependencies {
    coreLibraryDesugaring(libs.android.tools.desugarJdkLibs)

    androidTestImplementation(libs.kotest.assertions.core)

    androidTestRuntimeOnly(libs.androidx.test.core)
    androidTestRuntimeOnly(libs.androidx.test.runner)

    testImplementation(libs.junit.vintage.engine)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.robolectric.core)
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

// https://github.com/gradle/gradle/issues/33619
tasks.withType<Test> {
    useJUnitPlatform()
    failOnNoDiscoveredTests.set(false)
}

private data class TestDevice(
    val device: String,
    val apiLevel: Int,
    val systemImageSource: String,
) {
    val name = buildString {
        append(device.lowercase().replace(" ", ""))
        append("api")
        append(apiLevel.toString())
        append(systemImageSource.replace("-", ""))
    }
}