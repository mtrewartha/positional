import io.trewartha.positional.configureJava
import io.trewartha.positional.configureKotlinJvm
import io.trewartha.positional.configureTestLogger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
                apply("com.adarshr.test-logger")
            }

            extensions.configure<JavaPluginExtension> {
                configureJava()
            }

            extensions.configure<KotlinJvmProjectExtension> {
                configureKotlinJvm()
            }

            configureTestLogger()
        }
    }
}
