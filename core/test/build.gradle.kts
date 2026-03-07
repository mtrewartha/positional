plugins {
    alias(libs.plugins.positional.library.jvm)
}

dependencies {
    api(libs.kotest.property)
}

kotlin {
    compilerOptions {
        optIn.addAll(
            "kotlin.time.ExperimentalTime",
        )
    }
}