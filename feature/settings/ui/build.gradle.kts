plugins {
    alias(libs.plugins.positional.library.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.metro)
}

android {
    namespace = "io.trewartha.positional.settings.ui"
}

dependencies {
    api(projects.core.ui)
    api(projects.feature.settings)
    api(libs.androidx.compose.runtime)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.uiTooling)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewModel.ktx)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.metro.viewModel)
    implementation(libs.metro.viewModel.compose)
}

kotlin {
    compilerOptions {
        optIn.addAll(
            "androidx.compose.material3.ExperimentalMaterial3Api",
        )
    }
}

