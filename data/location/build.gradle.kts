plugins {
    id("io.trewartha.positional.jvm.library")
}

dependencies {
    implementation(project(":model:location"))

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
