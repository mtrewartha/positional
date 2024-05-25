import com.android.build.gradle.LibraryExtension
import io.trewartha.positional.configureAndroid
import io.trewartha.positional.configureKotlinAndroid
import io.trewartha.positional.configureKoverForAndroidLibrary
import io.trewartha.positional.configureTestLogger
import io.trewartha.positional.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class AndroidLibraryConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("com.adarshr.test-logger")
                apply("org.jetbrains.kotlinx.kover")
            }

            extensions.configure<LibraryExtension> {
                configureAndroid()
                defaultConfig.targetSdk = 34
                // The resource prefix is derived from the module name,
                // so resources inside ":core:module1" must be prefixed with "core_module1_"
                resourcePrefix =
                    path.split("""\W""".toRegex()).drop(1).distinct().joinToString(separator = "_")
                        .lowercase() + "_"

                extensions.configure<KotlinAndroidProjectExtension> {
                    configureKotlinAndroid()
                }
            }

            configureKoverForAndroidLibrary()

            configureTestLogger()

            dependencies {
                add("androidTestImplementation", libs.findLibrary("kotlin.test").get())
                add("androidTestRuntimeOnly", libs.findLibrary("androidx.test.core").get())
                add("androidTestRuntimeOnly", libs.findLibrary("androidx.test.runner").get())
                add("testImplementation", libs.findLibrary("kotest.assertions.core").get())
                add("testImplementation", libs.findLibrary("kotlin.test").get())
                add("testImplementation", libs.findLibrary("robolectric.core").get())
            }
        }
    }
}
