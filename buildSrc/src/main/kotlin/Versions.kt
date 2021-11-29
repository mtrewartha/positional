import org.gradle.api.JavaVersion

object Versions {

    const val kotlin = "1.5.31"

    object Android {
        const val compileSdk = 31
        const val minSdk = 21
        const val targetSdk = 31
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
        const val androidXActivityCompose = "1.4.0"
        const val androidXCompose = "1.1.0-beta03"
        const val androidXComposeMaterial3 = "1.0.0-alpha01"
        const val androidXFragment = "1.4.0"
        const val androidXHilt = "1.0.0"
        const val androidXHiltNavigationCompose = "1.0.0-alpha03"
        const val androidXHiltNavigationFragment = "1.0.0"
        const val androidXLegacySupportV4 = "1.0.0"
        const val androidXLifecycle = "2.4.0"
        const val androidXLifecycleViewModelCompose = "2.4.0"
        const val androidXNavigation = "2.4.0-beta02"
        const val androidXPreference = "1.1.1"
        const val firebaseBoM = "29.0.0"
        const val geoCoordinatesConversion = "360781e"
        const val googleAccompanist = "0.21.3-beta"
        const val guava = "30.1.1-android"
        const val hilt = "2.40.1"
        const val kotlin = Versions.kotlin
        const val kotlinxCoroutines = "1.5.2"
        const val kotest = "4.6.3"
        const val markwon = "4.6.2"
        const val pageIndicatorView = "1.0.3"
        const val playServicesLocation = "18.0.0"
        const val sunriseSunset = "1.1.1"
        const val threeTenAbp = "1.3.1"
        const val timber = "4.7.1"
    }

    object Plugins {
        const val android = "7.1.0-beta04"
        const val firebaseCrashlytics = "2.7.1"
        const val firebasePerf = "1.4.0"
        const val googleServices = "4.3.10"
        const val gradleVersions = "0.36.0"
        const val hilt = Versions.Dependencies.hilt
        const val kotlin = Versions.kotlin
    }
}