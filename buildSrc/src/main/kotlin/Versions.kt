import org.gradle.api.JavaVersion

object Versions {

    const val kotlin = "1.4.20"

    object Android {
        const val compileSdk = 29
        const val minSdk = 21
        const val targetSdk = 29
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
        private const val minor = 13
        private const val patch = 0
    }

    object Compatibility {
        val source: JavaVersion = JavaVersion.VERSION_1_8
        val target: JavaVersion = JavaVersion.VERSION_1_8
    }

    object Dependencies {
        const val androidXConstraintLayout = "2.0.0"
        const val androidXFragment = "1.3.0-beta01"
        const val androidXLegacySupportV4 = "1.0.0"
        const val androidXLifecycle = "2.3.0-beta01"
        const val androidXNavigation = "2.3.1"
        const val androidXPreference = "1.1.1"
        const val androidXRecyclerView = "1.2.0-alpha06"
        const val firebaseBoM = "26.1.0"
        const val geoCoordinatesConversion = "360781e"
        const val guava = "30.0-android"
        const val kotlin = Versions.kotlin
        const val kotlinxCoroutines = "1.4.2"
        const val kotest = "4.3.1"
        const val loaderView = "2.0.0"
        const val material = "1.3.0-alpha04"
        const val pageIndicatorView = "1.0.3"
        const val playServicesLocation = "17.1.0"
        const val sunriseSunset = "1.1.1"
        const val threeTenAbp = "1.3.0"
        const val timber = "4.7.1"
    }

    object Plugins {
        const val android = "4.1.1"
        const val firebaseCrashlytics = "2.4.1"
        const val firebasePerf = "1.3.4"
        const val googleServices = "4.3.4"
        const val gradleVersions = "0.36.0"
        const val kotlin = Versions.kotlin
    }
}