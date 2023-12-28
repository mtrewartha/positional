import org.jetbrains.kotlin.konan.properties.loadProperties
import java.nio.file.NoSuchFileException

plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

private val PROPERTIES_FILE_PATH = "app/upload_keystore.properties"
private val PROPERTIES_KEY_ALIAS = "keyAlias"
private val PROPERTIES_KEY_PASSWORD = "keyPassword"
private val PROPERTIES_KEY_STORE_FILE = "storeFile"
private val PROPERTIES_KEY_STORE_PASSWORD = "storePassword"

android {
    sourceSets.forEach { it.java.setSrcDirs(listOf("src/${it.name}/kotlin")) }

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

    namespace = "io.trewartha.positional"

    compileSdk = libs.versions.android.sdk.compile.get().toInt()

    defaultConfig {
        applicationId = "io.trewartha.positional"
        minSdk = libs.versions.android.sdk.min.get().toInt()
        targetSdk = libs.versions.android.sdk.target.get().toInt()
        versionCode = 21030007
        versionName = "3.0.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    buildTypes {
        getByName("debug").apply {
            isDebuggable = true
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }

        getByName("release").apply {
            isDebuggable = false
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }

    flavorDimensions += "androidVariant"
    productFlavors {
        create("aosp") {
            dimension = "androidVariant"
        }
        create("gms") {
            dimension = "androidVariant"
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
    }

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs +
                "-Xinline-classes" +
                "-Xjvm-default=all" +
                "-opt-in=androidx.compose.animation.ExperimentalAnimationApi" +
                "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi" +
                "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi" +
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api" +
                "-opt-in=androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi" +
                "-opt-in=com.google.accompanist.permissions.ExperimentalPermissionsApi" +
                "-opt-in=kotlin.ExperimentalStdlibApi" +
                "-opt-in=kotlin.RequiresOptIn" +
                "-opt-in=kotlin.time.ExperimentalTime" +
                "-opt-in=kotlin.ExperimentalUnsignedTypes" +
                "-opt-in=kotlinx.coroutines.DelicateCoroutinesApi" +
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
    }

    testOptions {
        managedDevices {
            localDevices {
                create("pixel7AospApi33") {
                    device = "Pixel 7"
                    apiLevel = 33
                    systemImageSource = "aosp-atd"
                }
                create("pixel7GmsApi33") {
                    device = "Pixel 7"
                    apiLevel = 33
                    systemImageSource = "google-atd"
                }
                groups {
                    create("aosp") {
                        targetDevices.add(devices["pixel7AospApi33"])
                    }
                    create("gms") {
                        targetDevices.add(devices["pixel7GmsApi33"])
                    }
                }
            }
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    ksp(libs.hilt.compiler)

    coreLibraryDesugaring(libs.android.desugarJdkLibs)

    implementation(project(":domain"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.activity.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material3.windowSizeClass)
    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.uiTooling)
    implementation(libs.androidx.constraintLayout.compose)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.hilt.navigation.fragment)
    implementation(libs.androidx.legacySupport.v4)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.liveData.ktx)
    implementation(libs.androidx.lifecycle.viewModel.compose)
    implementation(libs.androidx.lifecycle.viewModel.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.window)
    implementation(libs.accompanist.pager.indicators)
    implementation(libs.accompanist.permissions)
    implementation(libs.composePlaceholder.material3)
    implementation(libs.hilt.android)
    implementation(libs.google.materialComponents)
    implementation(platform(libs.google.firebase.bom))
    implementation(libs.google.firebase.crashlytics)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.markwon)
    implementation(libs.timber)

    "gmsImplementation"(libs.google.firebase.analytics)

    testImplementation(libs.turbine)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.property)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotlin.reflect)

    androidTestImplementation(libs.kotest.assertions.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.uiTest.junit4)
}

hilt {
    enableAggregatingTask = true
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}
