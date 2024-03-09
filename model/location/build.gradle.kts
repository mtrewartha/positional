plugins {
    id("io.trewartha.positional.jvm.library")
}

dependencies {
    api(project(":model:core"))
    api(libs.kotlinx.datetime)
}
