plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    api(libs.kotlinx.datetime)

    testImplementation(libs.turbine)
    testImplementation(libs.kotest.assertions.core)
    testImplementation(libs.kotest.property)
    testImplementation(libs.kotest.runner.junit5)
    testImplementation(libs.kotlin.reflect)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
