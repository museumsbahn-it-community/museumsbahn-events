// val containerEngine by extra { "docker" } // or "podman"
val containerEngine by extra { "podman" }

repositories {
    mavenCentral()
    mavenLocal()
}

allprojects {
    group = "at.museumrailwayevents"
    version = "0.2.0"
}
