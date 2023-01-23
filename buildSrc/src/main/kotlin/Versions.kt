import org.gradle.api.JavaVersion

object Versions {

    const val kotlin = "1.7.21"

    object Android {
        const val compileSdk = 33
        const val minSdk = 21
        const val targetSdk = 33
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
        const val androidXActivityCompose = "1.6.1"
        const val androidXCompose = "1.4.0-alpha03"
        const val androidXComposeCompiler = "1.4.0-alpha02"
        const val androidXComposeMaterial3 = "1.1.0-alpha04"
        const val androidXConstraintLayoutCompose = "1.1.0-alpha05"
        const val androidXDataStore = "1.1.0-alpha01"
        const val androidXFragment = "1.5.4"
        const val androidXHilt = "1.0.0"
        const val androidXLegacySupportV4 = "1.0.0"
        const val androidXLifecycle = "2.5.1"
        const val androidXNavigation = "2.5.3"
        const val androidXPreference = "1.2.0"
        const val androidXWindow = "1.1.0-alpha04"
        const val cashAppTurbine = "0.12.1"
        const val firebaseBoM = "31.1.1"
        const val googleAccompanist = "0.28.0"
        const val googleAndroidDesugarJdkLibs = "1.2.2"
        const val googleMaterial = "1.7.0"
        const val googleProtoBuf = "3.21.9"
        const val guava = "31.1-android"
        const val hilt = "2.44.2"
        const val kotlin = Versions.kotlin
        const val kotlinxCoroutines = "1.6.4"
        const val kotlinxDatetime = "0.4.0"
        const val kotest = "5.5.4"
        const val markwon = "4.6.2"
        const val pageIndicatorView = "v.1.0.3"
        const val playServicesLocation = "21.0.1"
        const val sunriseSunset = "1.2"
        const val timber = "5.0.1"
    }

    object Plugins {
        const val android = "7.3.1"
        const val firebaseCrashlytics = "2.9.0"
        const val firebasePerf = "1.4.1"
        const val googleProtoBuf = "0.9.1"
        const val googleServices = "4.3.10"
        const val hilt = Versions.Dependencies.hilt
        const val kotlin = Versions.kotlin
    }
}
