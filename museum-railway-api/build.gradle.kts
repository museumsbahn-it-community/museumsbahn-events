plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("events.boudicca:common-model:0.5.0")
}

kotlin {
    jvmToolchain(21)
}