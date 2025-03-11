plugins {
    id("org.graalvm.buildtools.native")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
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

    implementation(libs.jackson.core)
    implementation(libs.jackson.annotations)
    implementation(libs.jackson.module.kotlin)

    implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")
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

val containerEngine: String by rootProject.extra

task<Exec>("imageBuild") {
    inputs.file("src/main/docker/Dockerfile")
    inputs.files(tasks.named("bootJar"))
    dependsOn(tasks.named("assemble"))
    commandLine(containerEngine, "build", "-t", "localhost/museum-railway-events-backend", "-f", "src/main/docker/Dockerfile", ".")
}