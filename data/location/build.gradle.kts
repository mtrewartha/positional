plugins {
    id("io.trewartha.positional.jvm.library")
}

dependencies {
    implementation(project(":model:location"))
    implementation(libs.kotlinx.coroutines.test)
}
