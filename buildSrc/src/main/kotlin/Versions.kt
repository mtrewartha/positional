import org.gradle.api.JavaVersion

object Versions {

    const val kotlin = "1.6.21"

    object Android {
        const val compileSdk = 32
        const val minSdk = 21
        const val targetSdk = 32
    }

    object Application {
        val code by lazy {
            val format = "%02d"
            val minSdk = String.format(format, Android.minSdk)
            val versionMajor = String.format(format, major)
            val versionMinor = String.format(format, minor)
            val versionPatch = String.format(format, patch)
            "${minSdk}${versionMajor}${versionMinor}${versionPatch}".toInt()
        }
        val name by lazy { "$major.$minor.$patch" }

        private const val major = 2
        private const val minor = 14
        private const val patch = 0
    }

    object Compatibility {
        val source: JavaVersion = JavaVersion.VERSION_1_8
        val target: JavaVersion = JavaVersion.VERSION_1_8
    }

    object Dependencies {
        const val androidXActivityCompose = "1.5.0-rc01"
        const val androidXCompose = "1.2.0-beta03"
        const val androidXComposeMaterial3 = "1.0.0-alpha14"
        const val androidXFragment = "1.5.0-rc01"
        const val androidXHilt = "1.0.0"
        const val androidXLegacySupportV4 = "1.0.0"
        const val androidXLifecycle = "2.5.0-rc01"
        const val androidXNavigation = "2.5.0-rc01"
        const val androidXPreference = "1.2.0"
        const val androidXWindow = "1.1.0-alpha02"
        const val cashAppTurbine = "0.8.0"
        const val firebaseBoM = "30.1.0"
        const val googleAccompanist = "0.24.10-beta"
        const val guava = "31.1-android"
        const val hilt = "2.42"
        const val kotlin = Versions.kotlin
        const val kotlinxCoroutines = "1.6.2"
        const val kotlinxDatetime = "0.4.0"
        const val kotest = "5.3.2"
        const val markwon = "4.6.2"
        const val pageIndicatorView = "v.1.0.3"
        const val playServicesLocation = "19.0.1"
        const val sunriseSunset = "1.1.1"
        const val timber = "5.0.1"
    }

    object Plugins {
        const val android = "7.2.1"
        const val firebaseCrashlytics = "2.9.0"
        const val firebasePerf = "1.4.1"
        const val googleServices = "4.3.10"
        const val hilt = Versions.Dependencies.hilt
        const val kotlin = Versions.kotlin
    }
}