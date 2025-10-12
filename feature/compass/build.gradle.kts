plugins {
    id("io.trewartha.positional.android.library")
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "io.trewartha.positional.compass"
}

dependencies {
    ksp(libs.google.hilt.compiler)

    api(projects.core.measurement)
    api(libs.kotlinx.coroutines.core)

    implementation(libs.google.hilt.android)
    implementation(libs.javax.inject)

    testImplementation(projects.core.measurement)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
