plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("io.cloudflight.autoconfigure.swagger-api-configure")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("kapt")
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(project(":museum-railway-api"))
    implementation(platform(libs.cloudflight.platform.spring.bom))
    annotationProcessor(platform(libs.cloudflight.platform.spring.bom))
    kapt(platform(libs.cloudflight.platform.spring.bom))
    implementation("io.github.microutils:kotlin-logging-jvm:2.0.11")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.swagger:swagger-annotations")
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_21.toString()
    kotlinOptions.javaParameters = true
}

task<Exec>("imageBuild") {
    inputs.file("src/main/docker/Dockerfile")
    inputs.files(tasks.named("bootJar"))
    dependsOn(tasks.named("assemble"))
    commandLine("docker", "build", "-t", "localhost/museum-railway-events-backend", "-f", "src/main/docker/Dockerfile", ".")
}