package io.trewartha.positional

import kotlinx.kover.gradle.plugin.dsl.KoverReportExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureKoverForAndroidLibrary() {
    extensions.configure<KoverReportExtension> {
        defaults {
            mergeWith("aospDebug")
            mergeWith("aospRelease")
            mergeWith("gmsDebug")
            mergeWith("gmsRelease")
        }
    }
    configureKoverForJvmLibrary()
}

internal fun Project.configureKoverForJvmLibrary() {
    extensions.configure<KoverReportExtension> {
    }
}
