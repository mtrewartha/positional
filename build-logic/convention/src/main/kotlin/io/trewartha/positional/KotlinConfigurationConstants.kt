package io.trewartha.positional

import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val EXPLICIT_API_MODE = ExplicitApiMode.Strict

val FREE_COMPILER_ARGS = listOf(
    "-Xcontext-parameters",
    "-Xinline-classes",
    "-Xjvm-default=all",
)

val JVM_SOURCE_COMPATIBILITY = JavaVersion.VERSION_17

val JVM_TARGET = JvmTarget.JVM_17
