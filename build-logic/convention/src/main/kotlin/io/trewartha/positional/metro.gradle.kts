package io.trewartha.positional

import dev.zacsweers.metro.gradle.MetroPluginExtension

pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
    apply(plugin = "dev.zacsweers.metro")
    configure<MetroPluginExtension> { generateContributionProviders.set(true) }
}
pluginManager.withPlugin("org.jetbrains.kotlin.android") {
    apply(plugin = "dev.zacsweers.metro")
    configure<MetroPluginExtension> { generateContributionProviders.set(true) }
}
