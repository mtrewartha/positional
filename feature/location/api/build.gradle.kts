plugins {
    id("io.trewartha.positional.jvm.library")
}

dependencies {
    api(project(":core:api"))
    api(libs.kotlinx.datetime)
}
