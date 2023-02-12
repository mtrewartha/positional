plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
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
