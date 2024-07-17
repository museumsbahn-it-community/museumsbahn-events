val museumrailwayEventsNpmRepoUrl: String by ext
val museumrailwayEventsNpmToken: String by ext

val containerEngine: String by rootProject.extra

task<Exec>("imageBuild") {
    inputs.file("Dockerfile")
    commandLine(
        containerEngine, "build",
        "-t", "localhost/museum-railway-events-web",
        "--build-arg=\"NPM_REPO_URL=$museumrailwayEventsNpmRepoUrl\"",
        "--build-arg=\"NPM_TOKEN=$museumrailwayEventsNpmToken\"",
        "-f", "Dockerfile", "."
    )
}