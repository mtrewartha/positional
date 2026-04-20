package io.trewartha.positional

import dev.zacsweers.metro.gradle.MetroPluginExtension

val applyMetro: () -> Unit = {
    apply(plugin = "dev.zacsweers.metro")
    configure<MetroPluginExtension> { generateContributionProviders.set(true) }
}

pluginManager.withPlugin("org.jetbrains.kotlin.jvm") { applyMetro() }
pluginManager.withPlugin("com.android.library") { applyMetro() }
pluginManager.withPlugin("com.android.application") { applyMetro() }
