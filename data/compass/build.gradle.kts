plugins {
    id("io.trewartha.positional.jvm.library")
}

dependencies {
    api(project(":model:compass"))
    api(libs.kotlinx.coroutines.core)

    testImplementation(project(":model:core"))
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
