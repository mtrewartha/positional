plugins {
    id("io.trewartha.positional.jvm.library")
}

dependencies {
    api(project(":core:api"))
    api(libs.javax.inject)
    api(libs.kotlinx.datetime)

    implementation(libs.sunriseSunset)

    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotlin.test)
}
