plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    setSourceCompatibility(Versions.Compatibility.source)
    setTargetCompatibility(Versions.Compatibility.target)
}

kotlin {
    jvmToolchain(Versions.Compatibility.source)
}

dependencies {
    api(project(":data"))
    api(files("libs/worldwind.jar")) // TODO: Make this an implementation dependency
    api(Dependencies.kotlinxCoroutinesCore)

    testImplementation(Dependencies.cashAppTurbine)
    testImplementation(Dependencies.kotestAssertionsCore)
    testImplementation(Dependencies.kotestProperty)
    testImplementation(Dependencies.kotestRunnerJUnit5)
    testImplementation(Dependencies.kotlinReflect)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
