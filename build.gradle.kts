buildscript {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
    dependencies {
        classpath(Plugins.androidGradle)
        classpath(Plugins.firebaseCrashlytics)
        classpath(Plugins.firebasePerf)
        classpath(Plugins.googleServices)
        classpath(Plugins.hilt)
        classpath(Plugins.kotlin)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
