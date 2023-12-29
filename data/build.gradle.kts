plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.protobuf)
    alias(libs.plugins.android.library)
}

android {
    namespace = "io.trewartha.positional.data"
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
                "-opt-in=kotlin.ExperimentalStdlibApi"
    }
}

dependencies {
    api(files("libs/worldwind.jar")) // TODO: Make this an implementation dependency and use new Kotlin version
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.datetime)

    implementation(libs.androidx.core)
    implementation(libs.androidx.dataStore)
    implementation(libs.javax.inject)
    implementation(libs.protobuf.java.lite)
    implementation(libs.protobuf.kotlin.lite)
    implementation(libs.sunriseSunset)
    implementation(libs.timber)

    "gmsApi"(libs.google.playServices.location)
    "gmsImplementation"(libs.kotlinx.coroutines.playServices)

    testImplementation(libs.turbine)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotlin.reflect)
    testImplementation(libs.kotlin.test)
}

kotlin {
    jvmToolchain(libs.versions.java.get().toInt())
}

protobuf {
    protoc {
        artifact = if (osdetector.os == "osx") {
            val architectureSuffix =
                if (System.getProperty("os.arch") == "x86_64") "x86_64" else "aarch_64"
            "${libs.protobuf.protoc.get()}:osx-$architectureSuffix"
        } else {
            libs.protobuf.protoc.get().toString()
        }
    }
    plugins {
        generateProtoTasks {
            all().forEach {
                it.builtins {
                    create("java") { option("lite") }
                    create("kotlin") { option("lite") }
                }
            }
        }
    }
}
