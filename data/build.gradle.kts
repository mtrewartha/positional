import com.google.protobuf.gradle.id

plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
    id("com.google.protobuf") version Versions.Plugins.googleProtoBuf
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(Dependencies.googleProtoBufKotlinLite)
    api(Dependencies.kotlinxDatetime)

    testImplementation(Dependencies.cashAppTurbine)
    testImplementation(Dependencies.kotestAssertionsCore)
    testImplementation(Dependencies.kotestProperty)
    testImplementation(Dependencies.kotestRunnerJUnit5)
    testImplementation(Dependencies.kotlinReflect)
}

sourceSets {
    getByName("main") {
        proto {
            protobuf {
                protoc {
                    artifact = "com.google.protobuf:protoc:${Versions.Dependencies.googleProtoBuf}"
                }
                generateProtoTasks {
                    all().forEach { task -> task.builtins { getByName("java") { option("lite") } } }
                }
            }
        }
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
