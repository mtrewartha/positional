plugins {
    alias(libs.plugins.positional.library.jvm)
    alias(libs.plugins.google.ksp)
}

dependencies {
    ksp(libs.google.hilt.compiler)

    api(projects.core.measurement)
    api(libs.kotlinx.datetime)

    implementation(libs.google.hilt.core)
    implementation(libs.javax.inject)
    implementation(libs.sunriseSunset)
}
