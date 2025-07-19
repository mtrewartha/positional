plugins {
    id("io.trewartha.positional.android.library")
}

android {
    namespace = "io.trewartha.positional.compass"
}

dependencies {
    api(project(":feature:compass:api"))
    api(libs.javax.inject)
    api(libs.kotlinx.coroutines.core)

    testImplementation(project(":core:api"))
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
