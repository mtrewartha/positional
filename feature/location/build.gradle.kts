plugins {
    alias(libs.plugins.positional.library.android)
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "io.trewartha.positional.location"
}

dependencies {
    ksp(libs.google.hilt.compiler)

    api(projects.core.measurement)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.datetime)

    gmsApi(libs.google.playServices.location)

    implementation(libs.androidx.core)
    implementation(libs.google.hilt.android)
    implementation(libs.javax.inject)
    implementation(libs.timber)

    gmsImplementation(libs.kotlinx.coroutines.playServices)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}

kotlin {
    compilerOptions {
        optIn.addAll(
            "kotlin.time.ExperimentalTime",
        )
    }
}
