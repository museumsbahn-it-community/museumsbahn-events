buildscript {
    repositories {
        maven(
            url = "https://plugins.gradle.org/m2/"
        )
    }
    dependencies {
        classpath("org.openapitools:openapi-generator-gradle-plugin:7.0.1")
    }
}

plugins {
    id("org.openapi.generator") version "7.2.0"
}

repositories {
    mavenCentral()
}

openApiGenerate {
    inputSpec.set("${project.projectDir}/src/main/resources/museum-railway-backend.yaml")
    outputDir.set("${project.projectDir}/src/main/generated/typescript")
    generatorName.set("typescript-axios")
    configOptions.putAll(
        mapOf(
            "npmName" to "@museumrailwayevents/museum-railway-client",
            "npmVersion" to "${project.version}",
            "supportsES6" to "true",
        )
    )
}