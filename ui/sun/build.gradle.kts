plugins {
    id("io.trewartha.positional.android.library")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "io.trewartha.positional.ui.sun"
}

dependencies {
    ksp(libs.google.hilt.compiler)

    api(project(":data:location"))
    api(project(":data:sun"))
    api(project(":ui:core"))
    api(libs.androidx.compose.runtime)

    implementation(project(":model:core"))
    implementation(project(":ui:design"))
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

    implementation(project(":model:location"))
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.datetime)
    testImplementation(libs.turbine)
}
