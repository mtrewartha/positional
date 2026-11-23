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

// Plain `detekt` skips rules that need type resolution (e.g. ForbiddenMethodCall), so it also runs
// the type-resolved tasks. Android modules aggregate their variants under detektMain/detektTest.
val typeResolvedDetektTask = Regex("detekt(Main|Test|TestFixtures)")
tasks.named("detekt") {
    // Filter by name so Gradle doesn't realize unrelated tasks.
    dependsOn(tasks.names.filter { it.matches(typeResolvedDetektTask) })
}
