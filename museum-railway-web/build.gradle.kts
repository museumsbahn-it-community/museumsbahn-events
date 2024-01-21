val museumrailwayEventsNpmRepoUrl: String by ext
val museumrailwayEventsNpmToken: String by ext

task<Exec>("imageBuild") {
    inputs.file("Dockerfile")
    commandLine("docker", "build",
        "-t", "localhost/museum-railway-events-web",
        "--build-arg=\"NPM_REPO_URL=$museumrailwayEventsNpmRepoUrl\"",
        "--build-arg=\"NPM_TOKEN=$museumrailwayEventsNpmToken\"",
        "-f", "Dockerfile", ".")
}