plugins {
    id("io.trewartha.positional.android.library")
}

android {
    namespace = "io.trewartha.positional.data.compass"
}

dependencies {
    api(project(":model:compass"))
    api(libs.javax.inject)
    api(libs.kotlinx.coroutines.core)

    testImplementation(project(":model:core"))
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
