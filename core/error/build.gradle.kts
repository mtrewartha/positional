plugins {
    alias(libs.plugins.positional.library.jvm)
}

dependencies {
    testImplementation(projects.core.test)
}