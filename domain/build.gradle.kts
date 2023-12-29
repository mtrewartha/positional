plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.android.library)
}

android {
    namespace = "io.trewartha.positional.domain"
    compileSdk = libs.versions.android.sdk.compile.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.sdk.min.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs +
                "-opt-in=kotlin.ExperimentalStdlibApi" +
                "-opt-in=kotlin.time.ExperimentalTime" +
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
    }
}

dependencies {
    api(project(":data"))

    implementation(libs.javax.inject)
    implementation(libs.timber)

    testImplementation(libs.turbine)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotlin.reflect)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}
