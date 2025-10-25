plugins {
    alias(libs.plugins.positional.library.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "io.trewartha.positional.location.ui"
}

dependencies {
    ksp(libs.google.hilt.compiler)

    api(projects.core.measurement)
    api(projects.core.ui)
    api(projects.feature.location)
    api(projects.feature.settings)
    api(libs.accompanist.permissions)
    api(libs.androidx.compose.runtime)

    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.uiTooling)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewModel.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.timber)

    testImplementation(testFixtures(projects.feature.location))
    testImplementation(testFixtures(projects.feature.settings))
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}

kotlin {
    compilerOptions {
        optIn.addAll(
            "androidx.compose.foundation.layout.ExperimentalLayoutApi",
            "androidx.compose.material3.ExperimentalMaterial3Api",
            "com.google.accompanist.permissions.ExperimentalPermissionsApi",
            "kotlin.time.ExperimentalTime",
        )
    }
}
