plugins {
    alias(libs.plugins.positional.library.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.metro)
}

android {
    namespace = "io.trewartha.positional.core.ui"
}

dependencies {
    api(projects.core.di)
    api(libs.androidx.appcompat)
    api(libs.androidx.compose.runtime)
    api(libs.androidx.navigation3.runtime)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.uiTooling)
    implementation(libs.composePlaceholder.material3)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)
    implementation(libs.markwon)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}

kotlin {
    compilerOptions {
        optIn.addAll(
            "androidx.compose.material3.ExperimentalMaterial3Api",
            "kotlin.time.ExperimentalTime",
        )
    }
}
