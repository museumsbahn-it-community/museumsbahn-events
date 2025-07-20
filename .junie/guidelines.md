# Museum Railway Events Project Guidelines

This document provides essential information for developers working on the Museum Railway Events project.

## Frameworks and Technologies

### museum-railway-web
- **Nuxt.js** (v3.15.0) - Vue.js framework for server-side rendering and static site generation
- **Vue.js** (v3.5.16) - Progressive JavaScript framework for building user interfaces
- **PrimeVue** (v4.3.4) - UI component library for Vue
- **Leaflet** - JavaScript library for interactive maps
- **i18n** - Internationalization library

### museum-railway-backend
- **Spring Boot** - Java/Kotlin framework for building web applications
- **Kotlin** (JDK 21) - Programming language
- **Google API Client** - For integration with Google services (Sheets, etc.)
- **SpringDoc OpenAPI** - For API documentation

### museum-railway-api
- **Kotlin** (JDK 21) - Programming language
- **Kotlin Serialization** - For JSON serialization/deserialization

### museum-railway-eventcollectors
- **Kotlin** (JDK 21) - Programming language
- **Spring Boot** - Java/Kotlin framework for building web applications
- **Ktor Client** - HTTP client for Kotlin
- **JSoup** - HTML parser for Java/Kotlin
- **iCal4j** - For iCalendar processing
- **Rome** - For RSS/Atom feed processing

## Build and Configuration Instructions

### Prerequisites
- JDK 21
- Node.js (for web module)
- Podman or Docker (configurable in build.gradle.kts)

### Building the Project
1. Clone the repository
2. Configure the container engine in the root build.gradle.kts file:
   ```kotlin
   val containerEngine by extra { "podman" } // or "docker"
   ```
3. Build the backend modules:
   ```bash
   ./gradlew build
   ```
4. Build the web module:
   ```bash
   cd museum-railway-web
   npm install
   npm run build
   ```

### Running the Project
The project uses Docker Compose for local development:
```bash
docker-compose up
```

This will start all the necessary services:
- Boudicca EventDB
- Boudicca Search
- Museum Railway Backend
- Museum Railway Web
- Traefik (reverse proxy)
- imgproxy

## Testing Information

### Backend Testing
The backend modules use JUnit 5 for testing and AssertK for assertions.

#### Running Backend Tests
```bash
./gradlew test
```

Or to run a specific test:
```bash
./gradlew test --tests "at.museumrailwayevents.util.StringUtilsTest"
```

#### Creating a New Test
1. Create a test class in the appropriate test directory
2. Use JUnit 5 annotations (@Test, etc.)
3. Use AssertK for assertions

Example:
```kotlin
class MyServiceTest {
    @Test
    fun `calling myFunction with testParams should return expectedValue`() {
       // Given
       val service = MyService()
       val testParams = Params(param1 = "value1", param2 = 123) // initialize test params

        // When
        val result = service.myFunction(testParams)

        // Then
        assertThat(result).isEqualTo(expectedValue)
    }
}
```

Note: You'll need to import the necessary classes:
- `org.junit.jupiter.api.Test`
- `assertk.assertThat`
- `assertk.assertions.isEqualTo`

### Web Testing
The web module doesn't have explicit tests. Manual testing through the UI is currently the primary method.
It is not necessary to build or start since nuxt dev is running and provides hot reload.

## General Guidelines
* Ensure code follows the existing style and conventions
* Run appropriate tests to verify changes before submitting

## Code Style and Development Guidelines

### Kotlin Code Style
- Follow Kotlin coding conventions
- Use meaningful names for classes, methods, and variables
- Write comprehensive documentation for public APIs
- Use nullable types only when necessary
- Prefer immutable data (val over var)

### Web Code Style
- Follow Vue.js style guide
- Use composition API for new components
- Use TypeScript for type safety
- Organize components in a logical structure
- Make style changes in small, incremental steps
- Always get approval before making extensive UI/layout changes
- When modifying UI components, focus on one aspect at a time (positioning, styling, functionality)
- Use PrimeVue components whenever sensible instead of creating custom components
- Use PrimeFlex classes instead of writing custom CSS rules
- Reduce written CSS rules to a minimum and go with the defaults provided by PrimeVue whenever possible
- Only add custom CSS when absolutely necessary and no suitable PrimeVue component or PrimeFlex class exists

### Git Workflow
- Create feature branches for new features
- Create bugfix branches for bug fixes
- Use descriptive commit messages
- Squash commits before merging

## Project Structure
- **museum-railway-api**: Shared models and interfaces
- **museum-railway-backend**: Backend services and REST APIs
- **museum-railway-eventcollectors**: Services for collecting event data from various sources
- **museum-railway-web**: Frontend web application

## Deployment
The project uses Docker for deployment. Images are built and tagged using the provided scripts:
- build_and_publish_images.ps1
- deploy_images_latest.sh
- deploy_images_stable.sh
- tag_images_local.sh
