plugins {
    id("io.trewartha.positional.android.library")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "io.trewartha.positional.core.ui"
}

dependencies {
    ksp(libs.google.hilt.compiler)

    api(libs.androidx.appcompat)
    api(libs.androidx.compose.runtime)
    api(libs.javax.inject)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.uiTooling)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.composePlaceholder.material3)
    implementation(libs.google.hilt.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)
    implementation(libs.markwon)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
