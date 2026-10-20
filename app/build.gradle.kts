import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
    id("io.trewartha.positional.metro")
}

android {
    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        create("release") {
            val uploadKeystoreProperties = providers
                .fileContents(layout.projectDirectory.file("upload_keystore.properties"))
                .asText
                .map { text -> Properties().apply { load(text.reader()) } }
            uploadKeystoreProperties.orNull?.let { properties ->
                storeFile = file(properties.getProperty("storeFile"))
                storePassword = properties.getProperty("storePassword")
                keyAlias = properties.getProperty("keyAlias")
                keyPassword = properties.getProperty("keyPassword")
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
        // https://developer.android.com/studio/write/java11-minimal-support-table
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
        isCoreLibraryDesugaringEnabled = true
    }

    buildToolsVersion = libs.versions.android.buildTools.get()
    compileSdk = 37

    defaultConfig {
        applicationId = "io.trewartha.positional"

        versionCode = 21030103
        versionName = "3.1.3"

        minSdk = 28
        targetSdk = 37

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
    implementation(libs.metro.viewModel)
    implementation(libs.metro.viewModel.compose)
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
            "-jvm-default=no-compatibility",
        )
        jvmTarget = JvmTarget.JVM_21
        optIn.addAll(
            "androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi",
            "com.google.accompanist.permissions.ExperimentalPermissionsApi",
            "kotlin.ExperimentalStdlibApi",
            "kotlin.time.ExperimentalTime",
        )
    }
    jvmToolchain(libs.versions.jvm.toolchain.get().toInt())
}

// https://github.com/gradle/gradle/issues/33619
tasks.withType<Test> { failOnNoDiscoveredTests.set(false) }
