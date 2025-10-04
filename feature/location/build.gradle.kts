plugins {
    id("io.trewartha.positional.android.library")
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "io.trewartha.positional.location"
}

dependencies {
    ksp(libs.google.hilt.compiler)

    api(projects.core.measurement)
    api(libs.google.hilt.android)
    api(libs.javax.inject)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.datetime)

    gmsApi(libs.google.playServices.location)

    implementation(libs.androidx.core)
    implementation(libs.timber)

    gmsImplementation(libs.kotlinx.coroutines.playServices)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
