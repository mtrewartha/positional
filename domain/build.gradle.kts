plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    api(files("libs/worldwind.jar")) // TODO: Make this an implementation dependency
    api(Dependencies.kotlinxCoroutinesCore)
    api(Dependencies.kotlinxDatetime)

    testImplementation(Dependencies.cashAppTurbine)
    testImplementation(Dependencies.kotestAssertionsCore)
    testImplementation(Dependencies.kotestProperty)
    testImplementation(Dependencies.kotestRunnerJUnit5)
    testImplementation(Dependencies.kotlinReflect)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}