package io.trewartha.positional

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinSingleTargetExtension

internal fun KotlinAndroidProjectExtension.configureKotlinAndroid() {
    configureKotlin()
    compilerOptions.configureWithDefaults {
        optIn.add("androidx.compose.material3.ExperimentalMaterial3Api")
    }
}

internal fun KotlinJvmProjectExtension.configureKotlinJvm() {
    configureKotlin()
    compilerOptions.configureWithDefaults()
}

private fun KotlinJvmCompilerOptions.configureWithDefaults(
    extraConfiguration: KotlinJvmCompilerOptions.() -> Unit = {},
) {
    jvmTarget.set(JvmTarget.JVM_17)

    optIn.addAll(
        "kotlin.ExperimentalStdlibApi",
        "kotlin.ExperimentalUnsignedTypes",
        "kotlin.RequiresOptIn",
        "kotlin.time.ExperimentalTime",
        "kotlinx.coroutines.DelicateCoroutinesApi",
        "kotlinx.coroutines.ExperimentalCoroutinesApi",
    )

    freeCompilerArgs.addAll(
        "-Xjvm-default=all",
        "-Xcontext-parameters",
        "-Xinline-classes",
    )

    extraConfiguration()
}

internal fun KotlinSingleTargetExtension<*>.configureKotlin() {
    explicitApi = ExplicitApiMode.Strict
}
