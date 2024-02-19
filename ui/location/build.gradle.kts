plugins {
    id("io.trewartha.positional.android.library")
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "io.trewartha.positional.ui.location"

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
    }

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs +
                "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi" +
                "-opt-in=com.google.accompanist.permissions.ExperimentalPermissionsApi"
    }
}

dependencies {
    ksp(libs.google.hilt.compiler)

    api(project(":model:location"))
    api(libs.androidx.compose.runtime)

    "gmsApi"(libs.google.playServices.location)
    "gmsImplementation"(libs.kotlinx.coroutines.playServices)

    implementation(project(":data:location"))
    implementation(project(":data:settings"))
    implementation(project(":model:core"))
    implementation(project(":model:settings"))
    implementation(project(":ui:core"))
    implementation(project(":ui:design"))
    implementation(files("libs/worldwind.jar")) // TODO: Make this an implementation dependency and use new Kotlin version
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.uiTooling)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewModel.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.google.hilt.android)
    implementation(libs.timber)

    androidTestImplementation(libs.junit)

    testImplementation(project(":data:settings"))
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
}
