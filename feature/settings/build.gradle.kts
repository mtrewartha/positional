plugins {
    alias(libs.plugins.google.ksp)
    alias(libs.plugins.positional.library.android)
    alias(libs.plugins.google.protobuf)
}

android {
    namespace = "io.trewartha.positional.settings"
}

dependencies {
    ksp(libs.google.hilt.compiler)

    api(projects.core.measurement)
    api(libs.kotlinx.coroutines.core)
    api(libs.protobuf.java.lite)

    implementation(libs.androidx.dataStore)
    implementation(libs.google.hilt.android)
    implementation(libs.javax.inject)
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
