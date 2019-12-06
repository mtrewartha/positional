buildscript {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven(url = "https://maven.fabric.io/public")
    }
    dependencies {
        classpath(Plugins.android)
        classpath(Plugins.firebasePerf)
        classpath(Plugins.googleServices)
        classpath(Plugins.fabric)
        classpath(Plugins.jUnit5)
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
