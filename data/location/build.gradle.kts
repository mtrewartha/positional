plugins {
    id("io.trewartha.positional.android.library")
}

android {
    namespace = "io.trewartha.positional.data.location"
}

dependencies {
    api(project(":model:location"))
    api(libs.javax.inject)
    api(libs.kotlinx.coroutines.core)

    gmsApi(libs.google.playServices.location)

    implementation(libs.androidx.core)
    implementation(libs.timber)

    gmsImplementation(libs.kotlinx.coroutines.playServices)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
