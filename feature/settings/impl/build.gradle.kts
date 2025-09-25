plugins {
    id("io.trewartha.positional.android.library")
    alias(libs.plugins.protobuf)
}

android {
    namespace = "io.trewartha.positional.settings"
}

dependencies {
    api(projects.core.api)
    api(projects.feature.settings.api)
    api(libs.kotlinx.coroutines.core)
    api(libs.javax.inject)
    api(libs.protobuf.java.lite)

    implementation(libs.androidx.dataStore)
    implementation(libs.protobuf.kotlin.lite)

    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric.core)
    testImplementation(libs.turbine)
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
