plugins {
    alias(libs.plugins.positional.library.jvm)
}

dependencies {
    implementation(libs.worldwind) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-reflect")
    }
}
