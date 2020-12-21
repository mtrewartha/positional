plugins {
    id("com.android.application")
    id("com.github.ben-manes.versions")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    kotlin("android")
    kotlin("kapt")
}

android {
    sourceSets.forEach {
        it.java.setSrcDirs(listOf("src/${it.name}/kotlin"))
    }

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
    buildFeatures {
        viewBinding = true
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
    kotlinOptions {
        jvmTarget = Versions.Compatibility.target.toString()
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
    packagingOptions {
        resources {
            excludes += "DebugProbesKt.bin"
        }
    }
}

repositories {
    google()
    jcenter()
    mavenCentral()
    maven(url = "https://jitpack.io")
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    implementation(Dependencies.androidXConstraintLayout)
    implementation(Dependencies.androidXConstraintLayout)
    implementation(Dependencies.androidXFragmentKtx)
    implementation(Dependencies.androidXLegacySupportV4)
    implementation(Dependencies.androidXLifecycleRuntime)
    implementation(Dependencies.androidXLifecycleLiveDataKtx)
    implementation(Dependencies.androidXLifecycleViewModelKtx)
    implementation(Dependencies.androidXNavigationFragmentKtx)
    implementation(Dependencies.androidXNavigationUIKtx)
    implementation(Dependencies.androidXPreferenceKtx)
    implementation(Dependencies.androidXRecyclerView)
    implementation(platform(Dependencies.firebaseBoM))
    implementation(Dependencies.firebaseCrashlytics)
    implementation(Dependencies.firebaseAnalytics)
    implementation(Dependencies.firebasePerf)
    implementation(Dependencies.geoCoordinatesConversion)
    implementation(Dependencies.guava)
    implementation(Dependencies.kotlinxCoroutinesCore)
    implementation(Dependencies.kotlinxCoroutinesAndroid)
    implementation(Dependencies.markwon)
    implementation(Dependencies.material)
    implementation(Dependencies.pageIndicatorView)
    implementation(Dependencies.playServicesLocation)
    implementation(Dependencies.sunriseSunset)
    implementation(Dependencies.threeTenAbp)
    implementation(Dependencies.timber)

    testImplementation(Dependencies.kotestAssertionsCoreJvm)
    testImplementation(Dependencies.kotestPropertyJvm)
    testImplementation(Dependencies.kotestRunnerJUnit5)
}

apply(plugin = "com.google.gms.google-services")
