package io.trewartha.positional

plugins {
    id("dev.detekt")
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom(rootProject.layout.projectDirectory.file("detekt.yml"))
    parallel = true
    baseline = layout.projectDirectory.file("detekt-baseline.xml")
}
