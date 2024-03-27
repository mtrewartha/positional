import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "io.trewartha.positional.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_21.toString()
        options.freeCompilerArgs.add("-Xcontext-receivers")
    }
}

dependencies {
    compileOnly(libs.android.tools.build)
    compileOnly(libs.kotlin.gradle)
    compileOnly(libs.kotlinx.kover)
    compileOnly(libs.testLogger)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "io.trewartha.positional.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "io.trewartha.positional.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("kotlinLibrary") {
            id = "io.trewartha.positional.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
    }
}
