import org.gradle.api.JavaVersion

object Versions {

    const val kotlin = "1.5.0"

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
        const val androidXConstraintLayout = "2.0.4"
        const val androidXFragment = "1.3.1"
        const val androidXLegacySupportV4 = "1.0.0"
        const val androidXLifecycle = "2.3.0"
        const val androidXNavigation = "2.3.4"
        const val androidXPreference = "1.1.1"
        const val androidXRecyclerView = "1.2.0-beta02"
        const val firebaseBoM = "26.7.0"
        const val geoCoordinatesConversion = "360781e"
        const val guava = "30.1.1-android"
        const val hilt = "2.33-beta"
        const val hiltNavigationFragment = "1.0.0-beta01"
        const val kotlin = Versions.kotlin
        const val kotlinxCoroutines = "1.4.2"
        const val kotest = "4.3.1"
        const val markwon = "4.6.0"
        const val material = "1.3.0"
        const val pageIndicatorView = "1.0.3"
        const val playServicesLocation = "18.0.0"
        const val sunriseSunset = "1.1.1"
        const val threeTenAbp = "1.3.0"
        const val timber = "4.7.1"
    }

    object Plugins {
        const val android = "7.0.0-alpha10"
        const val firebaseCrashlytics = "2.5.1"
        const val firebasePerf = "1.3.5"
        const val googleServices = "4.3.5"
        const val gradleVersions = "0.36.0"
        const val hilt = Versions.Dependencies.hilt
        const val kotlin = Versions.kotlin
    }
}