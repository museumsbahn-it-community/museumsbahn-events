import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.springframework.boot")
    kotlin("plugin.spring")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.boudicca.eventdb.openapi)
    implementation(libs.boudicca.commonmodel)
    implementation(libs.otel.api)
    implementation(libs.boudicca.eventcollector.client)
    implementation(libs.kotlin.csv.jvm)
    implementation("org.jsoup:jsoup:1.16.1")
    implementation("org.mnode.ical4j:ical4j:3.2.14")
    implementation("com.rometools:rome:2.1.0")
    implementation("com.beust:klaxon:5.6")
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.engine.java)
//    implementation(libs.ktor.client.engine.cio)
//    implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")

    implementation(libs.logback)
    implementation("org.apache.commons:commons-text:1.12.0")
    implementation(project(mapOf("path" to ":museum-railway-api")))
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
    commandLine(
        containerEngine,
        "build",
        "-t",
        "localhost/museum-railway-events-eventcollectors",
        "-f",
        "src/main/docker/Dockerfile",
        "."
    )
}


tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<BootJar> {
    archiveFileName = "museum-railway-events-eventcollectors.jar"
}

springBoot {
    mainClass.set("at.museumrailwayevents.eventcollectors.MuseumRailwayEventCollectorsKt")
}
