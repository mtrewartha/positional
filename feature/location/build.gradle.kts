plugins {
    alias(libs.plugins.positional.library.android)
}

android {
    namespace = "io.trewartha.positional.location"
}

dependencies {
    api(projects.core.di)
    api(projects.core.measurement)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.datetime)

    gmsApi(libs.google.playServices.location)

    implementation(libs.androidx.core)
    implementation(libs.timber)

    gmsImplementation(libs.kotlinx.coroutines.playServices)

    testFixturesImplementation(projects.core.test)
    testFixturesImplementation(testFixtures(projects.core.measurement))
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
