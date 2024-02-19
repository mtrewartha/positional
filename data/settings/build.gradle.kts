plugins {
    id("io.trewartha.positional.jvm.library")
}

dependencies {
    api(project(":model:core"))
    api(project(":model:settings"))
    api(libs.kotlinx.coroutines.core)
}
