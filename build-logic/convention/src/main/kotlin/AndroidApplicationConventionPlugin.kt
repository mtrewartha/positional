import com.android.build.api.dsl.ApplicationExtension
import io.trewartha.positional.configureAndroid
import io.trewartha.positional.configureKotlinAndroid
import io.trewartha.positional.configureKoverForAndroidLibrary
import io.trewartha.positional.configureTestLogger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("com.adarshr.test-logger")
                apply("org.jetbrains.kotlinx.kover")
            }

            extensions.configure<ApplicationExtension> {
                configureAndroid()
                defaultConfig.targetSdk = 34

                extensions.configure<KotlinAndroidProjectExtension> {
                    configureKotlinAndroid()
                }
            }

            configureKoverForAndroidLibrary()

            configureTestLogger()
        }
    }
}
