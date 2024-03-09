plugins {
    id("io.trewartha.positional.jvm.library")
}

dependencies {
    implementation(libs.worldwind)

    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotlin.test)
}
