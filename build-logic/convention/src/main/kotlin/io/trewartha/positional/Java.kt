package io.trewartha.positional

import org.gradle.api.JavaVersion
import org.gradle.api.plugins.JavaPluginExtension

internal fun JavaPluginExtension.configureJava() {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
