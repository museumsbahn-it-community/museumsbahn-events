plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    kotlin("plugin.spring")
}

repositories {
    mavenLocal()
    mavenCentral()
}


group = "at.museumrailwayevents"
version = "0.3.0"

dependencies {
    api(libs.boudicca.commonmodel)
    api(libs.boudicca.fetcherlib)
    api(libs.kotlin.logging)
    api(project(":dateparser-lib"))
    api(libs.biweekly)
    api(libs.spring.boot.starter.web)
    api(libs.handlebars)
    api(libs.handlebars.springmvc)
    implementation(kotlin("reflect"))
    implementation(libs.otel.logback)
    implementation(libs.boudicca.publisher.client)
    implementation(libs.boudicca.ingest.client)
    implementation(libs.boudicca.enricher.client)
    implementation(libs.boudicca.remotecollector.client)
}

tasks.test {
    useJUnitPlatform()
}