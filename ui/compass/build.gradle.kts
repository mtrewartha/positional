plugins {
    id("io.trewartha.positional.android.library")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "io.trewartha.positional.ui.compass"
}

dependencies {
    ksp(libs.google.hilt.compiler)

    api(project(":data:compass"))
    api(project(":data:location"))
    api(project(":data:settings"))
    api(project(":model:compass"))
    api(project(":model:core"))
    api(project(":model:settings"))
    api(project(":ui:core"))
    api(libs.google.hilt.android)

    implementation(project(":model:location"))
    implementation(project(":ui:design"))
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.uiTooling)
    implementation(libs.androidx.constraintLayout.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewModel.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.google.materialComponents)
    implementation(libs.kotlinx.coroutines.core)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlinx.datetime)
    testImplementation(libs.turbine)
}
