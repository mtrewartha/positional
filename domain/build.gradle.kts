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
}

dependencies {
    api(project(":data"))
    api(files("libs/worldwind.jar")) // TODO: Make this an implementation dependency
    api(libs.kotlinx.coroutines.core)

    implementation(libs.androidx.dataStore)
    implementation(libs.protobuf.java.lite)
    implementation(libs.protobuf.kotlin.lite)

    testImplementation(libs.turbine)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.property)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotlin.reflect)
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

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
