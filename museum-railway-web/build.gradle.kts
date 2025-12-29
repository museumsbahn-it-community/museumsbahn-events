val containerEngine: String by rootProject.extra

task<Exec>("imageBuild") {
    inputs.file("Dockerfile")
    commandLine(
        containerEngine, "build",
        "-t", "localhost/museum-railway-events-web",
        "-f", "Dockerfile", "."
    )
}