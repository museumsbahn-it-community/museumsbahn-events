import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    id("org.openapi.generator") version "7.2.0"
}

repositories {
    mavenCentral()
}

tasks.register<GenerateTask>("generateTypescriptClient") {
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