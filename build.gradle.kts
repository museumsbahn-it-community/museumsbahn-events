// val containerEngine by extra { "docker" } // or "podman"
val containerEngine by extra { "podman" }

repositories {
    mavenCentral()
    mavenLocal()
}

allprojects {
    group = "at.museumrailwayevents"
    version = "0.3.0"
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootBuildImage> {
    docker {
        host= "unix://${System.getenv("XDG_RUNTIME_DIR")}/podman/podman.sock"
        bindHostToBuilder.set(true)
    }
}