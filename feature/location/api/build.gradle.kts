plugins {
    id("io.trewartha.positional.jvm.library")
}

dependencies {
    api(projects.core.api)
    api(libs.kotlinx.datetime)
}
