plugins {
    id("io.trewartha.positional.android.library")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "io.trewartha.positional.ui.design"
}

dependencies {
    api(project(":ui:core"))
    api(libs.androidx.compose.runtime)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.uiTooling)
    implementation(libs.composePlaceholder.material3)
    implementation(libs.markwon)
}
