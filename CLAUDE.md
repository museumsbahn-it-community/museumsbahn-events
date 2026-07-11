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

### Vendored Boudicca modules

`dateparser-lib` and `museum-railway-eventcollectors-base` are **not original code** — they are vendored copies of modules from [boudicca-events/boudicca.events](https://github.com/boudicca-events/boudicca.events) that boudicca hasn't published to Maven yet. Their upstream package names (`base.boudicca...`) are kept as-is rather than renamed. This is a stopgap:

- `settings.gradle.kts` has `// TODO: remove again when published by boudicca` above the `dateparser-lib` include
- `gradle/libs.versions.toml` has the real coordinates commented out (`# boudicca-dateparserlib = ...`)
- each vendored module's own `README.md` explains the same thing

Once boudicca publishes these artifacts: drop the local modules, remove the `include(...)` lines, and uncomment/add the real dependency coordinates in `gradle/libs.versions.toml`. Do not "clean up" these modules by renaming packages or restructuring them — that would make re-syncing with upstream harder, not easier.

## Tooling (mise)

All tool versions required to build and run this project (Java, Node, Python, uv) are declared in the root `mise.toml`. Run `mise install` to provision everything in one shot instead of relying on ambient/system installs. When a change introduces a new required tool or bumps a version, add it to `mise.toml` rather than only documenting it in prose.

## Knowledge Graph (graphify)

This repo has a graphify knowledge graph checked into `graphify-out/` (gitignored — it's a local build artifact, regenerate it, don't expect it to be there after a fresh clone). It indexes the whole codebase for architecture/relationship queries.

- First-time setup: `uv tool install graphifyy` (needs `uv`, provisioned via `mise install` above). The `/graphify` skill also auto-installs it on first run if missing.
- Rebuild after code changes: invoke the `/graphify` skill with `--update` (incremental — only re-extracts changed files, no LLM cost for code-only changes)
- Re-cluster without re-extracting (e.g. after just tuning cluster settings): invoke `/graphify` with `--cluster-only`, which runs `graphify cluster-only .` and regenerates `GRAPH_REPORT.md`, `graph.json`, and `graph.html` from the existing graph
- Before answering questions about codebase architecture or file relationships, treat `graphify-out/GRAPH_REPORT.md` as a first stop if it exists and is reasonably fresh (compare its "Built from commit" line to `git rev-parse HEAD`)

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

## Autonomous Agent Operation

This repo runs the Citadel harness (`.citadel/`, `.claude/`) for unattended/autonomous agent work — hooks, telemetry, and campaign orchestration (`/do`, `/archon`, `/marshal`, etc.) are already wired up, not something to bootstrap from scratch.

- `.planning/` holds harness state (campaigns, fleet sessions, intake, research, postmortems) — it is operational state, not project documentation. Don't treat files in there as authoritative project docs, and don't hand-edit them outside the harness's own scripts.
- `.claude/agent-context/rules-summary.md` is auto-injected into sub-agents spawned by the harness (scope discipline, typecheck-after-change, HANDOFF format). It's a harness mechanism, not something this file needs to duplicate.
- For "what does this codebase look like / how do these pieces connect" questions, check `graphify-out/GRAPH_REPORT.md` first (see **Knowledge Graph** above) before doing a manual file-by-file exploration — it's cheaper and usually current.
- Known inconsistency an agent might trip over: `dateparser-lib` and `museum-railway-eventcollectors-base` look like first-party modules but are vendored boudicca code (see **Vendored Boudicca modules** above) — don't refactor their internals to match this repo's conventions.
