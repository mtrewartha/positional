package io.trewartha.positional

import kotlinx.kover.gradle.plugin.dsl.KoverProjectExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

internal fun Project.configureKoverForAndroidLibrary() {
    extensions.configure<KoverProjectExtension> {
    }
    configureKoverForJvmLibrary()
}

internal fun Project.configureKoverForJvmLibrary() {
    extensions.configure<KoverProjectExtension> {
    }
}
