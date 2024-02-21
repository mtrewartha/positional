plugins {
    id("io.trewartha.positional.jvm.library")
}

dependencies {
    api(project(":model:core"))
    api(libs.kotlinx.coroutines.core)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
