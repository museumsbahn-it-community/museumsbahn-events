plugins {
    kotlin("jvm")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("base.boudicca:eventdb-openapi:0.0.1-SNAPSHOT")
    implementation("base.boudicca:semantic-conventions:0.0.1-SNAPSHOT")
    implementation("base.boudicca:eventcollector-client:0.0.1-SNAPSHOT")
    implementation("org.jsoup:jsoup:1.16.1")
    implementation("org.mnode.ical4j:ical4j:3.2.10")
    implementation("com.rometools:rome:2.1.0")
    implementation("com.beust:klaxon:5.6")
    implementation(project(mapOf("path" to ":museum-railway-api")))
}

group = "at.museumsbahnen"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_21.toString()
    kotlinOptions.javaParameters = true
}

task<Exec>("imageBuild") {
    dependsOn(tasks.withType<Jar>())
    commandLine("docker", "build", "-t", "boudicca-eventcollectors", "-f", "src/main/docker/Dockerfile", ".")
}

tasks.withType<Jar> {
    archiveFileName.set("boudicca-eventcollectors.jar")

    manifest {
        attributes["Main-Class"] = "events.boudicca.eventcollector.BoudiccaEventCollectorsKt"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    inputs.files(configurations.runtimeClasspath)
    from(configurations.runtimeClasspath.get().files.map { if (it.isDirectory()) it else zipTree(it) })
}