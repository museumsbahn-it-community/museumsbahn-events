plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.boudicca.commonmodel)
    implementation(libs.kotlinx.serialization.json)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}