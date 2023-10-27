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

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs +
                "-opt-in=kotlin.ExperimentalStdlibApi"
    }
}

dependencies {
    api(project(":data"))

    implementation(libs.javax.inject)
    implementation(libs.timber)

    testImplementation(libs.turbine)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.property)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotlin.reflect)
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
