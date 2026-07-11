# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

museumsbahn-events.at is an event aggregation platform for Austrian heritage railways. It crawls events from ~20 associations without requiring extra work from them, providing a single entry point for trip planning.

## Architecture

The system integrates with **Boudicca.events** as a central event hub:

- **`museum-railway-eventcollectors`** — Spring Boot app with ~20 custom web scrapers (JSoup, RSS, iCal) that push events into Boudicca EventDB
- **`museum-railway-backend`** — Spring Boot REST API providing museum locations, operators, opening hours, contact details, and an ImgProxy-backed image caching proxy
- **`museum-railway-api`** — Shared Kotlin data models (`Location`, `MuseumOperator`, `OperatorId`, `MuseumLocationId`, `CommonKeys`, `CommonValues`) used by both backend and collectors
- **`museum-railway-web`** — Nuxt 4 / Vue 3 frontend consuming both the backend API and Boudicca's search/iCal APIs
- **`dateparser-lib`** — Custom date parser for extracting event dates from German-language text
- **`museum-railway-eventcollectors-base`** — Shared base classes for event collectors

Infrastructure runs via Docker Compose (Traefik reverse proxy, Boudicca EventDB/Search/iCal, ImgProxy).

## Build Commands

### Kotlin/Gradle (backend, eventcollectors, api, dateparser-lib)

```bash
./gradlew build                                          # Build all modules
./gradlew :museum-railway-backend:build
./gradlew :museum-railway-eventcollectors:build
./gradlew test                                           # Run all tests
./gradlew :museum-railway-backend:test
./gradlew :museum-railway-eventcollectors:test
./gradlew :dateparser-lib:test
./gradlew :museum-railway-backend:bootRun               # Run backend locally
./gradlew :museum-railway-eventcollectors:bootRun       # Run collectors locally
```

Container builds use **Podman** by default (configurable via `containerEngine` in root `build.gradle.kts`):
```bash
./gradlew :museum-railway-backend:bootBuildImage
./gradlew :museum-railway-eventcollectors:bootBuildImage
```

### Frontend (Nuxt 4)

```bash
cd museum-railway-web
npm install
npm run dev       # Development server with hot reload
npm run build     # Production build
npm run generate  # Static site generation
npm run preview   # Preview production build
```

## API Client Generation Workflow

When changing the backend API:
1. Bump version in `build.gradle.kts`
2. Modify Kotlin models in `museum-railway-api`
3. Start the backend; fetch OpenAPI spec at `http://localhost:8080/v3/api-docs.yaml`
4. Copy contents to `museum-railway-web/src/main/resources/museum-railway-backend.yaml`
5. Run `generateTypescriptClient` Gradle task
6. In the generated client: `npm install && npm run build && npm publish`

## Key Technical Details

- **Java 21** required for all Kotlin modules
- **Kotlin 2.1.0**, **Spring Boot 3.4.3**, **Ktor 3.1.1** (HTTP client)
- Testing: **JUnit 5**, **MockK**, **AssertK**
- Frontend: **PrimeVue 4** + PrimeFlex for UI, **Leaflet** for maps, **date-fns** for dates
- i18n: German is the primary language
- Event collectors use Boudicca's semantic key/value model via `CommonKeys`/`CommonValues` from the api module
- ImgProxy integration uses HMAC-SHA256 URL signing (see `ImgproxyUrlSigningService`)
- Museum metadata is loaded from Google Sheets via `GoogleDataLoaderService`
- Backend management endpoints are protected with Spring Security `ROLE_MANAGE`

## Local Development Data

- Boudicca EventDB stores data in `infrastructure/boudicca.store/boudicca.store` — delete this file to reset the event database
- Start all infrastructure with: `docker compose -f infrastructure/docker-compose.yml up`
