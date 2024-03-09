plugins {
    id("io.trewartha.positional.jvm.library")
}

dependencies {
    api(project(":model:core"))

    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotlin.test)
}
