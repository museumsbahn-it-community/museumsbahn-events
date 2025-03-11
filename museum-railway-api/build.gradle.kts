plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.boudicca.commonmodel)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}