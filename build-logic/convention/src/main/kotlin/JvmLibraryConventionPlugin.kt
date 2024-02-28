import io.trewartha.positional.configureKotlinJvm
import io.trewartha.positional.configureKoverForJvmLibrary
import io.trewartha.positional.configureTestLogger
import org.gradle.api.Plugin
import org.gradle.api.Project

class JvmLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
                apply("com.adarshr.test-logger")
                apply("org.jetbrains.kotlinx.kover")
            }

            configureKotlinJvm()

            configureKoverForJvmLibrary()

            configureTestLogger()
        }
    }
}
