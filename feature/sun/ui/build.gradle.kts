plugins {
    alias(libs.plugins.positional.library.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.metro)
}

android {
    namespace = "io.trewartha.positional.sun.ui"
}

dependencies {
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
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewModel.ktx)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.metro.viewModel)
    implementation(libs.metro.viewModel.compose)

    testImplementation(projects.core.test)
    testImplementation(testFixtures(projects.feature.location))
    testImplementation(testFixtures(projects.feature.sun))
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.datetime)
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