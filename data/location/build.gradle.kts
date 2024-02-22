plugins {
    id("io.trewartha.positional.android.library")
}

android {
    namespace = "io.trewartha.positional.data.location"
}

dependencies {
    api(project(":model:location"))
    api(libs.javax.inject)

    implementation(libs.androidx.core)
    implementation(libs.timber)

    "gmsApi"(libs.google.playServices.location)
    "gmsImplementation"(libs.kotlinx.coroutines.playServices)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
