plugins {
    id("io.trewartha.positional.jvm.library")
    alias(libs.plugins.google.ksp)
}

dependencies {
    ksp(libs.google.hilt.compiler)

    api(projects.core.measurement)
    api(libs.javax.inject)
    api(libs.kotlinx.datetime)

    implementation(libs.google.hilt.core)
    implementation(libs.sunriseSunset)

    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotlin.test)
}
