pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    }
}

rootProject.name = "positional"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
    ":app",
    ":model:compass",
    ":model:core",
    ":model:location",
    ":model:settings",
    ":model:sun",
    ":data:compass",
    ":data:location",
    ":data:settings",
    ":data:sun",
    ":ui:compass",
    ":ui:core",
    ":ui:design",
    ":ui:location",
    ":ui:sun",
    ":ui:settings"
)
