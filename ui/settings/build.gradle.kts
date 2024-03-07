plugins {
    id("io.trewartha.positional.android.library")
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "io.trewartha.positional.ui.settings"

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
    }
}

dependencies {
    ksp(libs.google.hilt.compiler)

    api(libs.androidx.compose.runtime)

    implementation(project(":data:settings"))
    implementation(project(":ui:core"))
    implementation(project(":ui:design"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.compiler)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.uiTooling)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewModel.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.google.hilt.android)
}
