buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
    }
    dependencies {
        classpath(Plugins.androidGradle)
        classpath(Plugins.firebaseCrashlytics)
        classpath(Plugins.firebasePerf)
        classpath(Plugins.googleServices)
        classpath(Plugins.gradleVersions)
        classpath(Plugins.kotlin)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
