package io.trewartha.positional

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.ManagedVirtualDevice
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.invoke

internal fun CommonExtension<*, *, *, *, *, *>.configureGradleManagedDevices() {
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
