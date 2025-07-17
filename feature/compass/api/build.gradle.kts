plugins {
    id("io.trewartha.positional.jvm.library")
}

dependencies {
    api(project(":core:api"))

    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotlin.test)
}
