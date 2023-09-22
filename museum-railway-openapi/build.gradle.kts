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
    id("org.openapi.generator") version "7.0.1"
}

group = "at.museumsbahnen"
version = "0.0.1"

repositories {
    mavenCentral()
}

openApiGenerate {
    generatorName.set("typescript-axios")
    inputSpec.set("${project.projectDir}/src/main/resources/swagger.yaml")
    outputDir.set("${project.projectDir}/src/main/generated")
    apiPackage.set("at.museumsbahnen.api")
    invokerPackage.set("at.museumsbahnen.invoker")
    modelPackage.set("at.museumsbahnen.model")

    additionalProperties.put("npmName", "museum-railway-client")
    additionalProperties.put("npmVersion", version)
    additionalProperties.put("providedInRoot", "true")
}