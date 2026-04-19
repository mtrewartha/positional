plugins {
    alias(libs.plugins.positional.library.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "io.trewartha.positional.compass.ui"
}

dependencies {
    api(projects.core.measurement)
    api(projects.core.ui)
    api(projects.feature.compass)
    api(projects.feature.location)
    api(projects.feature.settings)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.uiTooling)
    implementation(libs.androidx.constraintLayout.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewModel.ktx)
    implementation(libs.google.materialComponents)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.metro.viewModel)
    implementation(libs.metro.viewModel.compose)

    testImplementation(testFixtures(projects.feature.compass))
    testImplementation(testFixtures(projects.feature.location))
    testImplementation(testFixtures(projects.feature.settings))
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.datetime)
    testImplementation(libs.turbine)
}

kotlin {
    compilerOptions {
        optIn.addAll(
            "kotlin.time.ExperimentalTime",
            "kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }
}
