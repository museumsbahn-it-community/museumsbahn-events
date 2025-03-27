plugins {
    id("org.graalvm.buildtools.native")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(project(":museum-railway-api"))

    implementation(libs.kotlin.reflect)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.security)
    developmentOnly(libs.spring.boot.devtools)
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.mockk)

    implementation(libs.kotlinx.serialization.json)

    implementation(libs.jackson.core)
    implementation(libs.jackson.annotations)
    implementation(libs.jackson.module.kotlin)

    implementation(libs.google.api.client) {
        exclude("commons-logging", "commons-logging")
    }
    implementation(libs.google.auth.library.oauth2.http) {
        exclude("commons-logging", "commons-logging")
    }
    implementation(libs.google.api.services.sheets) {
        exclude("commons-logging", "commons-logging")
    }

    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
    implementation(libs.kotlin.csv.jvm)
    //implementation("io.swagger:swagger-annotations")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.4.0")
    testImplementation("com.willowtreeapps.assertk:assertk:0.28.0")
    testImplementation(platform(libs.junit.jupiter.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

val containerEngine: String by rootProject.extra

task<Exec>("imageBuild") {
    inputs.file("src/main/docker/Dockerfile")
    inputs.files(tasks.named("bootJar"))
    dependsOn(tasks.named("assemble"))
    commandLine(containerEngine, "build", "-t", "localhost/museum-railway-events-backend", "-f", "src/main/docker/Dockerfile", ".")
}