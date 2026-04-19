plugins {
    alias(libs.plugins.positional.library.jvm)
}

dependencies {
    api(projects.core.di)
    api(projects.core.measurement)
    api(libs.kotlinx.datetime)

    implementation(libs.sunriseSunset)

    testImplementation(testFixtures(projects.core.measurement))
}
