val ktorVersion = "3.0.0"
val kotlinVersion = "2.0.21"
val projectVersion = "1.0.0"

plugins {
    kotlin("jvm") version "2.0.20"
    `maven-publish`
}

group = "dev.hayden"
version = projectVersion

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(8)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from (components["java"])
                groupId = "dev.hayden"
                artifactId = "graceful-shutdown"
                version = projectVersion
            }
        }
    }
}