plugins {
    alias(libs.plugins.positional.library.android)
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

    testFixturesImplementation(projects.core.test)
    testFixturesImplementation(testFixtures(projects.core.measurement))

    testImplementation(testFixtures(projects.core.measurement))
    testImplementation(libs.kotest.property)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
