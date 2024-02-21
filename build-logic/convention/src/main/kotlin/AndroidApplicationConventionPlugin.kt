import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.LibraryExtension
import io.trewartha.positional.configureFlavors
import io.trewartha.positional.configureGradleManagedDevices
import io.trewartha.positional.configureKotlinAndroid
import io.trewartha.positional.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid()
                defaultConfig.targetSdk = 34
                configureFlavors()
                configureGradleManagedDevices()
            }
            dependencies {
                add("androidTestImplementation", libs.findLibrary("androidx.test.core").get())
                add("androidTestImplementation", libs.findLibrary("androidx.test.ext.junit.ktx").get())
                add("androidTestImplementation", libs.findLibrary("androidx.test.runner").get())
                add("androidTestImplementation", libs.findLibrary("kotest.assertions.core").get())
                add("androidTestImplementation", libs.findLibrary("kotlin.test").get())
                add("androidTestImplementation", libs.findLibrary("kotlin.test").get())
                add("testImplementation", libs.findLibrary("kotest.assertions.core").get())
                add("testImplementation", libs.findLibrary("kotlin.test").get())
            }
        }
    }
}
