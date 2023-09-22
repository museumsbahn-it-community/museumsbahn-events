include("museum-railway-eventcollectors")
include("museum-railway-backend")
include("museum-railway-openapi")
include("museum-railway-web")

pluginManagement {
    val quarkusPluginVersion: String by settings
    val quarkusPluginId: String by settings
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
    plugins {
        id(quarkusPluginId) version quarkusPluginVersion
    }
}
rootProject.name = "museum-railway-events"
include("museum-railway-openapi")
