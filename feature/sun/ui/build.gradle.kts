plugins {
    id("io.trewartha.positional.android.library")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "io.trewartha.positional.sun.ui"
}

dependencies {
    ksp(libs.google.hilt.compiler)

    api(projects.core.ui)
    api(projects.feature.location)
    api(projects.feature.sun)
    api(libs.androidx.compose.runtime)

    implementation(projects.core.measurement)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.uiTooling)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewModel.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.google.hilt.android)

    testImplementation(testFixtures(projects.feature.location))
    testImplementation(testFixtures(projects.feature.sun))
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.datetime)
    testImplementation(libs.turbine)
}
