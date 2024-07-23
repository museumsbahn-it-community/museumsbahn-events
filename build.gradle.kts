plugins {
    kotlin("jvm") version "1.9.22" apply false
    kotlin("kapt") version "1.9.22"
    kotlin("plugin.allopen") version "1.9.22" apply false
    kotlin("plugin.spring") version "1.9.22" apply false
    id("org.springframework.boot") version "3.2.0" apply false
    id("io.spring.dependency-management") version "1.1.4" apply false
}

// val containerEngine by extra { "docker" } // or "podman"
val containerEngine by extra { "podman" }

buildscript {
    dependencies {
        // can someone just burn openapi?
        // https://stackoverflow.com/questions/76629033/openapitools-does-not-work-anymore-after-update-from-springboot-2-x-to-springboo
        classpath("com.fasterxml.jackson.core:jackson-core:2.15.3")
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

allprojects {
    group = "at.museumrailwayevents"
    version = "0.2.0"
}
