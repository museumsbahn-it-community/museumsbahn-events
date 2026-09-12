# Tech Debt Inventory

Compiled 2026-09-12. This is a working list, not a plan — pick items up as capacity allows.
Where a claim was independently verified (e.g. against Maven Central) that's noted so future-you
knows it's not just a guess.

## 1. Vendored Boudicca modules

`dateparser-lib` and `museum-railway-eventcollectors-base` are vendored copies of unpublished
boudicca modules (see `CLAUDE.md` → "Vendored Boudicca modules"). Checked Maven Central directly
(2026-09-12):

- **`events.boudicca:dateparser-lib` is now published — latest `0.7.0`.** The vendored
  `dateparser-lib` module can be dropped. Steps already spelled out in the repo:
  remove the `include("dateparser-lib")` line in `settings.gradle.kts`, delete the module,
  uncomment `boudicca-dateparserlib` in `gradle/libs.versions.toml`, and repoint dependants at it.
- **No published equivalent found for `museum-railway-eventcollectors-base`.** Checked the full
  `events/boudicca/*` artifact listing on Maven Central — nothing resembling an "eventcollector
  base" module exists there (closest are `fetcher-lib`, `eventcollector-client`,
  `remote-collector-client`, none of which look like a drop-in replacement without reading what
  the vendored base module actually wraps). Needs a manual check against the boudicca repo/team
  before assuming it's also ready — don't remove this one on the dateparser-lib precedent alone.
- **All boudicca artifacts actually in use are pinned to `0.6.0` in `gradle/libs.versions.toml`,
  while `0.7.0` is available for every one of them** (`common-model`, `fetcher-lib`,
  `eventdb-openapi`, `publisher-client`, `ingest-client`, `enricher-client`,
  `eventcollector-client`, `remote-collector-client` — checked each on Maven Central). This bump
  is independent of the vendoring cleanup and can happen regardless.

## 2. CI/CD (Gitea pipeline)

`origin` is GitHub (`.github/workflows/ci.yml` exists: Gradle build+test, frontend build — no lint,
no container build, no publish step). The `gitea` remote (`git.cloud.twatzl.eu`) has **no pipeline
at all** — no `.gitea/workflows/`. To add one:

- **Build job**: mirror what `ci.yml` already does (Gradle build/test, frontend build) so Gitea
  Actions/CI catches regressions independently of GitHub.
- **Container build job**: `bootBuildImage` Gradle task already exists per module
  (`museum-railway-backend`, `museum-railway-eventcollectors`) and builds via Podman
  (`build.gradle.kts` → `containerEngine`). Nothing currently invokes it in any pipeline. A gitea
  job needs to run `bootBuildImage`, tag the resulting image, and push it to a registry.
- **Version tag source**: `allprojects { version = "0.3.0" }` in root `build.gradle.kts` is
  hand-bumped and not tied to git tags. Before wiring container tagging, decide whether the image
  tag should come from that Gradle `version`, from a git tag pushed to trigger the pipeline, or
  from both (Gradle version as the "release" tag, commit SHA as a floating dev tag).
- Gitea's built-in CI is Actions-compatible (`.gitea/workflows/*.yml`, same syntax as GitHub
  Actions) if the instance has Actions enabled and a runner registered — worth confirming before
  assuming feature parity with the GitHub workflow.

## 3. Dependency updates (Dependabot equivalent)

No `dependabot.yml`, no Renovate config anywhere in the repo. Gitea has no native Dependabot;
options if staying on Gitea:
- **Renovate** self-hosted against the Gitea instance (Renovate has first-class Gitea platform
  support) — covers both Gradle (`gradle/libs.versions.toml`) and npm (`museum-railway-web`).
- If GitHub `origin` is meant to stay authoritative for dependency PRs, a plain
  `.github/dependabot.yml` (gradle + npm ecosystems) is the lower-effort option but only fires on
  GitHub, not Gitea.
Either way, this project has two dependency manifests (Gradle version catalog + npm) that
currently get bumped manually.

## 4. Kotlin lint/static analysis (detekt/ktlint)

Confirmed absent: no detekt or ktlint plugin in any `build.gradle.kts`, `buildSrc`, or
`gradle/libs.versions.toml`, no `detekt.yml`/`.editorconfig` ktlint rules. Nothing enforces Kotlin
style or catches common issues (unused imports, magic numbers, etc.) across `museum-railway-api`,
`museum-railway-backend`, `museum-railway-eventcollectors`. Adding either would also want a CI step
(`.github/workflows/ci.yml` and the new Gitea pipeline) so it's actually enforced, not just runnable
locally.

## 5. ESLint (frontend)

Present but effectively inert:
- `eslint.config.mjs` just re-exports the Nuxt-generated default config with no project rules added.
- `museum-railway-web/package.json` has no `lint` script at all — eslint has to be invoked manually
  with `npx eslint .`.
- `.github/workflows/ci.yml`'s `web-build` job runs `npm run build` only; lint never runs in CI.
- No `typecheck` script either, despite the project being TypeScript (`tsconfig.json` present).

Fix: add `lint`/`typecheck` scripts, wire them into `web-build` (or a new CI job), and decide if any
custom rules are wanted beyond Nuxt's defaults.

## 6. Other tech debt found while auditing

- **`DateParserTest.kt:94`** hardcodes `val currentYear = 2026 // TODO: increase this on 01.01.2027
  to fix the tests ;)` with a sibling TODO admitting a real bug for 2025 dates
  (`DateParserTest.kt:101`). Today is 2026-09-12 — this is ~3.5 months from silently going stale.
  Worth fixing properly (derive from `LocalDate.now()` or parametrize) rather than bumping the
  hardcoded year again next January.
- **No frontend tests at all.** `museum-railway-web` has no test framework installed (no
  vitest/jest/vue-test-utils in `package.json`) and no `*.test.*`/`*.spec.*` files. Backend/
  collectors have JUnit5/MockK/AssertK; the frontend has nothing.
- **No coverage tooling** (no Jacoco or equivalent) on any Gradle module, so test coverage is
  unmeasured.
- **Analytics config looks like an unresolved placeholder, committed as-is:**
  `nuxt.config.ts` hardcodes `umami: { id: 'my-w3b517e-id', host: 'http://localhost:3020',
  autoTrack: true }` — a literal `localhost` host and what reads like a placeholder ID, committed
  directly rather than sourced from runtime env config. As shipped, production analytics either
  point at localhost or rely on someone editing this file per-environment.
- **Analytics tracking (`autoTrack: true`) isn't mentioned anywhere in `impressum.vue`** (checked —
  no reference to umami/analytics/cookies/tracking on that page). Given the site targets an
  Austrian/German-speaking audience, GDPR requires disclosing and typically consent-gating
  analytics tracking; this looks unaddressed.
- **Inconsistent repository declarations across Gradle modules**: most modules declare both
  `mavenCentral()` and `mavenLocal()`; `museum-railway-api/build.gradle.kts` declares only
  `mavenCentral()`. Minor, but worth normalizing so module behavior doesn't silently diverge if a
  local-only boudicca snapshot is ever needed.
- **Ad hoc dependency versions bypassing the version catalog**: e.g.
  `museum-railway-eventcollectors/build.gradle.kts` pulls in `org.jsoup:jsoup:1.16.1`,
  `org.mnode.ical4j:ical4j:3.2.14`, `com.rometools:rome:2.1.0`, `com.beust:klaxon:5.6` as inline
  string coordinates instead of through `gradle/libs.versions.toml` like the rest of the project's
  dependencies — inconsistent and makes these easy to miss during upgrades.
- **`@nuxt/types` devDependency** in `museum-railway-web/package.json` is a legacy Nuxt 2 typings
  package; looks stale under Nuxt 4 and is likely dead weight.
- **Existing inline TODO worth promoting here**: `gradle/libs.versions.toml` already has
  `# todo: kinda wanna get rid of jackson in favor of kotlinx.serialization` — a real architectural
  decision (both Jackson and kotlinx.serialization are currently dependencies) that's easy to lose
  track of buried in a comment.
- **Full TODO/FIXME grep across first-party source** (vendored `dateparser-lib` /
  `museum-railway-eventcollectors-base` TODOs excluded as upstream boudicca code, not ours to fix):
  - `museum-railway-eventcollectors/.../OesekStrasshofCollector.kt:36` — date offset handling
  - `museum-railway-eventcollectors/.../NostalgiebahnenKärntenCollector.kt:55` — blacklisted keyword check missing
  - `museum-railway-backend/.../ImageCachingProxyController.kt:63` — consider switching to RestClient
  - `museum-railway-eventcollectors/.../WälderbähnleCollector.kt:102` — durations not properly handled
  - `museum-railway-backend/.../GoogleDataLoaderService.kt:158` — no caching/reload option for Google Sheets calls
  - `museum-railway-eventcollectors/.../dateParser/DateParser.kt:176` — likely crashes on malformed strings
  - `museum-railway-web/app/apiModel/apiModel.ts:1,40,57,75` — hand-maintained types that should come from the generated OpenAPI client
  - `museum-railway-eventcollectors/.../WackelsteinexpressCollector.kt:27,61,103` and `JsoupCrawlerTestMockImpl.kt:14` — recurring/"Sommerfahrten" events not collected, parsing not working
  - `museum-railway-eventcollectors/.../sternundhafferl/model/SternUndHafferlEvent.kt:61,129` — recurring event info unhandled
  - `museum-railway-eventcollectors/.../erzbergbahn/RegiondoCollector.kt:24` — hardcoded value needs to be configurable
  - `museum-railway-eventcollectors/.../TramwaymuseumGrazCollector.kt:38` — no caching
  - `museum-railway-web/app/composables/eventDataFunctions.ts:121,396` — grouping-key hack and dead keyword-matching code
  - `museum-railway-web/app/assets/theme.ts:17` — meaningless theme tokens to remove
  - `museum-railway-eventcollectors/src/test/kotlin/DateParserRegexTest.kt:81,134` — known-broken regex cases currently commented out of the test

## Not investigated further (flag if it becomes relevant)

- Whether Gitea Actions is actually enabled/has a runner on `git.cloud.twatzl.eu` — needs
  confirming before committing to the pipeline design in §2.
- Whether `museum-railway-eventcollectors-base`'s functionality is already covered by the
  now-published `fetcher-lib`/`eventcollector-client` (would need reading both sides to know).
