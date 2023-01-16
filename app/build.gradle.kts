plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    id("dagger.hilt.android.plugin")
}

android {
    sourceSets.forEach { it.java.setSrcDirs(listOf("src/${it.name}/kotlin")) }

    signingConfigs {
        create("release") {
            val keyStoreConfig = KeyStore(rootProject)
            storeFile = keyStoreConfig.file
            storePassword = keyStoreConfig.password
            keyAlias = keyStoreConfig.keyAlias
            keyPassword = keyStoreConfig.keyPassword
        }
    }
    compileSdk = Versions.Android.compileSdk
    defaultConfig {
        applicationId = "io.trewartha.positional"
        minSdk = Versions.Android.minSdk
        targetSdk = Versions.Android.targetSdk

        versionCode = Versions.Application.code
        versionName = Versions.Application.name
    }
    buildFeatures {
        viewBinding = true
        compose = true
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
        isCoreLibraryDesugaringEnabled = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.Dependencies.androidXComposeCompiler
    }
    kotlinOptions {
        jvmTarget = Versions.Compatibility.target.toString()
        freeCompilerArgs = freeCompilerArgs +
                "-Xinline-classes" +
                "-Xjvm-default=all" +
                "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi" +
                "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi" +
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api" +
                "-opt-in=com.google.accompanist.pager.ExperimentalPagerApi" +
                "-opt-in=com.google.accompanist.permissions.ExperimentalPermissionsApi" +
                "-opt-in=kotlin.ExperimentalStdlibApi" +
                "-opt-in=kotlin.RequiresOptIn" +
                "-opt-in=kotlin.time.ExperimentalTime" +
                "-opt-in=kotlin.ExperimentalUnsignedTypes" +
                "-opt-in=kotlinx.coroutines.DelicateCoroutinesApi" +
                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
    }
    packagingOptions {
        resources {
            excludes += "DebugProbesKt.bin"
        }
    }
    namespace = "io.trewartha.positional"
}

repositories {
    google()
    mavenCentral()
    maven(url = "https://jitpack.io")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    kapt(Dependencies.hiltCompiler)

    coreLibraryDesugaring(Dependencies.googleAndroidDesugarJdkLibs)

    implementation(project(":domain"))
    implementation(Dependencies.androidXActivityCompose)
    implementation(Dependencies.androidXComposeFoundation)
    implementation(Dependencies.androidXComposeMaterial3)
    implementation(Dependencies.androidXComposeMaterial3WindowSizeClass)
    implementation(Dependencies.androidXComposeMaterialIconsCore)
    implementation(Dependencies.androidXComposeMaterialIconsExtended)
    implementation(Dependencies.androidXComposeUI)
    implementation(Dependencies.androidXComposeUITooling)
    implementation(Dependencies.androidXConstraintLayoutCompose)
    implementation(Dependencies.androidXDataStore)
    implementation(Dependencies.androidXFragmentKtx)
    implementation(Dependencies.androidXHiltCompiler)
    implementation(Dependencies.androidXHiltNavigationCompose)
    implementation(Dependencies.androidXLegacySupportV4)
    implementation(Dependencies.androidXLifecycleRuntime)
    implementation(Dependencies.androidXLifecycleLiveDataKtx)
    implementation(Dependencies.androidXLifecycleViewModelCompose)
    implementation(Dependencies.androidXLifecycleViewModelKtx)
    implementation(Dependencies.androidXNavigationCompose)
    implementation(Dependencies.androidXPreferenceKtx)
    implementation(Dependencies.androidXWindow)
    implementation(platform(Dependencies.firebaseBoM))
    implementation(Dependencies.firebaseCrashlytics)
    implementation(Dependencies.firebaseAnalytics)
    implementation(Dependencies.firebasePerf)
    implementation(Dependencies.googleAccompanistInsetsUI)
    implementation(Dependencies.googleAccompanistPager)
    implementation(Dependencies.googleAccompanistPagerIndicators)
    implementation(Dependencies.googleAccompanistPermissions)
    implementation(Dependencies.googleAccompanistPlaceholder)
    implementation(Dependencies.googleAccompanistSystemUIController)
    implementation(Dependencies.googleMaterial)
    implementation(Dependencies.guava)
    implementation(Dependencies.hiltAndroid)
    implementation(Dependencies.hiltNavigationFragment)
    implementation(Dependencies.kotlinxCoroutinesAndroid)
    implementation(Dependencies.kotlinxCoroutinesPlayServices)
    implementation(Dependencies.markwon)
    implementation(Dependencies.pageIndicatorView)
    implementation(Dependencies.playServicesLocation)
    implementation(Dependencies.sunriseSunset)
    implementation(Dependencies.timber)

    testImplementation(Dependencies.cashAppTurbine)
    testImplementation(Dependencies.kotestAssertionsCore)
    testImplementation(Dependencies.kotestProperty)
    testImplementation(Dependencies.kotestRunnerJUnit5)
    testImplementation(Dependencies.kotlinReflect)

    androidTestImplementation(Dependencies.androidXComposeUITestJUnit4)
}

kapt {
    correctErrorTypes = true
}

hilt {
    enableExperimentalClasspathAggregation = true
}

apply(plugin = "com.google.gms.google-services")
