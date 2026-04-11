plugins {
    alias(libs.plugins.positional.library.android)
    alias(libs.plugins.metro)
}

android {
    namespace = "io.trewartha.positional.compass"
}

dependencies {
    api(projects.core.di)
    api(projects.core.measurement)
    api(libs.kotlinx.coroutines.core)

    testFixturesImplementation(projects.core.test)
    testFixturesImplementation(testFixtures(projects.core.measurement))

    testImplementation(testFixtures(projects.core.measurement))
    testImplementation(libs.kotest.property)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
