import java.nio.file.NoSuchFileException
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    alias(libs.plugins.metro)
}

private val PROPERTIES_FILE_PATH = "app/upload_keystore.properties"
private val PROPERTIES_KEY_ALIAS = "keyAlias"
private val PROPERTIES_KEY_PASSWORD = "keyPassword"
private val PROPERTIES_KEY_STORE_FILE = "storeFile"
private val PROPERTIES_KEY_STORE_PASSWORD = "storePassword"

android {
    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        create("release") {
            try {
                val uploadKeystoreProperties = loadProperties(PROPERTIES_FILE_PATH)
                storeFile = file(uploadKeystoreProperties.getProperty(PROPERTIES_KEY_STORE_FILE))
                storePassword = uploadKeystoreProperties.getProperty(PROPERTIES_KEY_STORE_PASSWORD)
                keyAlias = uploadKeystoreProperties.getProperty(PROPERTIES_KEY_ALIAS)
                keyPassword = uploadKeystoreProperties.getProperty(PROPERTIES_KEY_PASSWORD)
            } catch (_: NoSuchFileException) {
                // Builds can't be signed for upload to store unless properties file is loaded
            }
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
        }

        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        // Up to Java 17 APIs are available through desugaring
        // https://developer.android.com/studio/write/java11-minimal-support-table
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    compileSdk = 36

    defaultConfig {
        applicationId = "io.trewartha.positional"

        versionCode = 21030103
        versionName = "3.1.3"

        minSdk = 28
        targetSdk = 36

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
    }

    namespace = "io.trewartha.positional"

    flavorDimensions += "androidVariant"
    productFlavors {
        create("aosp") { dimension = "androidVariant" }
        create("gms") { dimension = "androidVariant" }
    }
}

dependencies {
    coreLibraryDesugaring(libs.android.tools.desugarJdkLibs)

    implementation(projects.core.di)
    implementation(projects.core.ui)
    implementation(projects.feature.compass.ui)
    implementation(projects.feature.location)
    implementation(projects.feature.location.ui)
    implementation(projects.feature.settings)
    implementation(projects.feature.settings.ui)
    implementation(projects.feature.sun.ui)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.uiTooling)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewModel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.metro.android)
    implementation(libs.metro.viewmodel)
    implementation(libs.metro.viewmodel.compose)
    implementation(libs.google.materialComponents)
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.crashlytics)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.timber)

    "gmsImplementation"(libs.google.firebase.analytics)

    androidTestRuntimeOnly(libs.androidx.test.runner)
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xjvm-default=all",
            "-Xinline-classes",
        )
        jvmTarget = JvmTarget.JVM_17
        optIn.addAll(
            "androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi",
            "com.google.accompanist.permissions.ExperimentalPermissionsApi",
            "kotlin.time.ExperimentalTime",
        )
    }
}

// https://github.com/gradle/gradle/issues/33619
tasks.withType<Test> { failOnNoDiscoveredTests.set(false) }
