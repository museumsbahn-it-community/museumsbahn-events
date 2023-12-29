buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.openapitools:openapi-generator-gradle-plugin:7.0.1")
    }
}

plugins {
    id("org.openapi.generator") version "7.0.1"
}

group = "base.boudicca"
version = "0.0.1"

repositories {
    mavenCentral()
}

openApiGenerate {
    generatorName.set("typescript-axios")
    inputSpec.set("${project.projectDir}/src/main/resources/search-api.yaml")
    outputDir.set("${project.projectDir}/src/main/generated")
    apiPackage.set("at.museumsbahnen.api")
    invokerPackage.set("at.museumsbahnen.invoker")
    modelPackage.set("at.museumsbahnen.model")

    additionalProperties.put("npmName", "boudicca-search-api-client")
    additionalProperties.put("npmVersion", version)
}