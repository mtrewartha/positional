plugins {
    alias(libs.plugins.positional.library.android)
    alias(libs.plugins.metro)
    alias(libs.plugins.wire)
}

android {
    namespace = "io.trewartha.positional.settings"
}

dependencies {
    api(projects.core.di)
    api(projects.core.measurement)
    api(libs.kotlinx.coroutines.core)

    implementation(libs.androidx.dataStore)
    implementation(libs.wire.runtime)

    testFixturesImplementation(projects.core.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric.core)
    testImplementation(libs.turbine)
}

wire {
    kotlin {}
}
