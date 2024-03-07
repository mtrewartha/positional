plugins {
    id("io.trewartha.positional.android.library")
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "io.trewartha.positional.ui.core"
}

dependencies {
    ksp(libs.google.hilt.compiler)

    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.compiler)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.google.hilt.android)
    implementation(libs.javax.inject)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)
}
