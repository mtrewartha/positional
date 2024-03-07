plugins {
    id("io.trewartha.positional.android.library")
}

android {
    namespace = "io.trewartha.positional.ui.design"

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
    }
}

dependencies {
    implementation(project(":ui:core"))

    api(libs.androidx.compose.runtime)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.compiler)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.uiTooling)
    implementation(libs.androidx.core)
    implementation(libs.composePlaceholder.material3)
    implementation(libs.markwon)
}
