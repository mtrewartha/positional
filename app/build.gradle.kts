plugins {
    id("com.android.application")
    id("com.google.firebase.firebase-perf")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("io.fabric")
}

android {
    signingConfigs {
        create("release") {
            val keyStoreConfig = KeyStore(rootProject)
            storeFile = keyStoreConfig.file
            storePassword = keyStoreConfig.password
            keyAlias = keyStoreConfig.keyAlias
            keyPassword = keyStoreConfig.keyPassword
        }
    }
    compileSdkVersion(Versions.Android.compileSdk)
    defaultConfig {
        applicationId = "io.trewartha.positional"
        minSdkVersion(Versions.Android.minSdk)
        targetSdkVersion(Versions.Android.targetSdk)

        versionCode = Versions.Application.code
        versionName = Versions.Application.name
    }
    buildTypes {
        getByName("debug").apply {
            isDebuggable = true
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }

        getByName("release").apply {
            isDebuggable = false
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
    }
    productFlavors {
    }
    compileOptions {
        sourceCompatibility = Versions.Compatibility.source
        targetCompatibility = Versions.Compatibility.target
    }
}

repositories {
    google()
    jcenter()
    mavenCentral()
    maven(url = "https://jitpack.io")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    implementation(Dependencies.androidXConstraintLayout)
    implementation(Dependencies.androidXFragmentKtx)
    implementation(Dependencies.androidXLegacySupportV4)
    implementation(Dependencies.androidXLifecycleCommonJava8)
    implementation(Dependencies.androidXLifecycleExtensions)
    implementation(Dependencies.androidXNavigationFragmentKtx)
    implementation(Dependencies.androidXNavigationUIKtx)
    implementation(Dependencies.androidXPreferenceKtx)
    implementation(Dependencies.androidXRecyclerView)
    implementation(Dependencies.crashlytics)
    implementation(Dependencies.firebaseCore)
    implementation(Dependencies.firebasePerf)
    implementation(Dependencies.geoCoordinatesConversion)
    implementation(Dependencies.kotlinStdLibJdk8)
    implementation(Dependencies.loaderView)
    implementation(Dependencies.material)
    implementation(Dependencies.pageIndicatorView)
    implementation(Dependencies.playServicesLocation)
    implementation(Dependencies.sunriseSunset)
    implementation(Dependencies.threeTenAbp)
    implementation(Dependencies.timber)
}

apply(plugin = "com.google.gms.google-services")
