package io.trewartha.positional

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ManagedVirtualDevice
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.invoke

context(project: Project)
internal fun CommonExtension<*, *, *, *, *, *>.configureAndroid() {
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        // Up to Java 17 APIs are available through desugaring
        // https://developer.android.com/studio/write/java11-minimal-support-table
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    testOptions.unitTests.isIncludeAndroidResources = true

    flavorDimensions += "androidVariant"
    productFlavors {
        create("aosp") {
            dimension = "androidVariant"
        }
        create("gms") {
            dimension = "androidVariant"
        }
    }

    testOptions {
        managedDevices {
            devices {
                val pixel7Api33AospAtd = TestDevice("Pixel 7", 33, "aosp-atd")
                val pixel7Api33GoogleAtd = TestDevice("Pixel 7", 33, "google-atd")
                val testDevices = listOf(pixel7Api33AospAtd, pixel7Api33GoogleAtd)
                for (testDevice in testDevices) {
                    maybeCreate(testDevice.name, ManagedVirtualDevice::class.java).apply {
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
                        maybeCreate(groupName).apply {
                            for (device in groupDevices) targetDevices.add(devices[device.name])
                        }
                    }
                }
            }
        }
    }

    project.dependencies {
        add("coreLibraryDesugaring", project.libs.findLibrary("android.tools.desugarJdkLibs").get())
    }
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