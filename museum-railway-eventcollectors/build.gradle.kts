plugins {
    kotlin("jvm")
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("events.boudicca.api:eventcollector-api:0.0.1-SNAPSHOT")
    implementation("events.boudicca.api:semantic-conventions:0.0.1-SNAPSHOT")
    implementation("org.jsoup:jsoup:1.16.1")
    implementation("org.mnode.ical4j:ical4j:3.2.10")
    implementation("com.rometools:rome:2.1.0")
    implementation("com.beust:klaxon:5.6")
}

group = "at.museumsbahnen"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
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