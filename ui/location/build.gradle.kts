plugins {
    id("io.trewartha.positional.android.library")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "io.trewartha.positional.ui.location"

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs +
                "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi" +
                "-opt-in=com.google.accompanist.permissions.ExperimentalPermissionsApi"
    }
}

dependencies {
    ksp(libs.google.hilt.compiler)

    api(project(":data:location"))
    api(project(":data:settings"))
    api(project(":model:core"))
    api(project(":model:location"))
    api(project(":model:settings"))
    api(project(":ui:core"))
    api(libs.accompanist.permissions)
    api(libs.androidx.compose.runtime)
    api(libs.google.hilt.android)

    implementation(project(":ui:design"))
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

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
