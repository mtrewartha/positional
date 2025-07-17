plugins {
    id("io.trewartha.positional.android.library")
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "io.trewartha.positional.compass.ui"
}

dependencies {
    ksp(libs.google.hilt.compiler)

    api(project(":core:api"))
    api(project(":core:ui"))
    api(project(":feature:compass:api"))
    api(project(":feature:compass:impl"))
    api(project(":feature:location:impl"))
    api(project(":feature:settings:api"))
    api(project(":feature:settings:impl"))
    api(libs.google.hilt.android)

    implementation(project(":core:ui"))
    implementation(project(":feature:location:api"))
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
