import org.gradle.api.JavaVersion

object Versions {

    const val kotlin = "1.5.21"

    object Android {
        const val compileSdk = 30
        const val minSdk = 21
        const val targetSdk = 30
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
        const val androidXActivityCompose = "1.3.0-rc02"
        const val androidXCompose = "1.0.1"
        const val androidXConstraintLayout = "2.1.0"
        const val androidXFragment = "1.4.0-alpha06"
        const val androidXHilt = "1.0.0"
        const val androidXHiltNavigationCompose = "1.0.0-alpha03"
        const val androidXHiltNavigationFragment = "1.0.0"
        const val androidXLegacySupportV4 = "1.0.0"
        const val androidXLifecycle = "2.4.0-alpha03"
        const val androidXLifecycleViewModel = "1.0.0-alpha07"
        const val androidXLifecycleViewModelCompose = "1.0.0-alpha07"
        const val androidXNavigation = "2.3.5"
        const val androidXNavigationCompose = "2.4.0-alpha05"
        const val androidXPreference = "1.1.1"
        const val androidXRecyclerView = "1.2.1"
        const val firebaseBoM = "28.3.0"
        const val geoCoordinatesConversion = "360781e"
        const val guava = "30.1.1-android"
        const val hilt = "2.38.1"
        const val kotlin = Versions.kotlin
        const val kotlinxCoroutines = "1.5.1"
        const val kotest = "4.6.1"
        const val markwon = "4.6.2"
        const val material = "1.5.0-alpha01"
        const val pageIndicatorView = "1.0.3"
        const val playServicesLocation = "18.0.0"
        const val sunriseSunset = "1.1.1"
        const val threeTenAbp = "1.3.1"
        const val timber = "4.7.1"
    }

    object Plugins {
        const val android = "7.0.1"
        const val firebaseCrashlytics = "2.7.1"
        const val firebasePerf = "1.4.0"
        const val googleServices = "4.3.8"
        const val gradleVersions = "0.36.0"
        const val hilt = Versions.Dependencies.hilt
        const val kotlin = Versions.kotlin
    }
}