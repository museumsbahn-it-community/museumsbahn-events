# Roadmap

> Last updated: 2026-07-11
>
> Prioritized upcoming work for museumsbahn-events.at. Each item gets its own
> PRD (via `/prd`) when it moves to "next up" — this file tracks sequencing,
> status, and cross-cutting decisions, not implementation detail.

## Vision / Problem

museumsbahn-events.at aggregates events from ~20 Austrian heritage railway
associations without requiring extra work from them. Current gaps limiting
data quality and maintainability:

- Eventcollectors are only partly migrated to Spring-managed registration,
  blocking dependency injection of services like Spring AI into collectors.
- Existing collectors don't capture all the data the UI can already display.
- Associations have no self-service way to submit events — every source needs
  a bespoke scraper.
- The frontend carries a heavy PrimeVue/PrimeFlex dependency, and the
  filtering UX needs a redesign.
- Events have no per-event location/route data, limiting trip-planning value.

## Items

### 1. Spring bean-based collector registration — **in progress**

Finish migrating all ~24 collectors from config-only registration to
`@BoudiccaEventCollector`-annotated Spring beans. 5 are done
(`OesekStrasshof`, `WienerTramwayMuseum`, `Atterseebahn`, `OegegShop`,
`MLVZwettl`); ~19 remain. Unlocks constructor injection of Spring-managed
services (e.g. Spring AI) into any collector.

**Done when:** no collector remains on the legacy registration path, and at
least one collector demonstrably injects a Spring-managed service.

### 2. Collector data completeness — **next up**

Audit each existing collector against everything the frontend can render
(categories, vehicle types, registration, tags, recurrence, …) and close the
gaps where displayable properties aren't being collected.

**Done when:** an audit document maps each collector to the UI-displayable
fields it does/doesn't populate, and identified gaps are closed.

### 3. Google Sheets collector — **planned**

New collector type reading events from a per-association Google Sheet.
Decisions made so far:

- **Manual provisioning**: ops duplicates a canonical template sheet per
  association and registers the sheet ID in collector config (like existing
  collectors in `application.yml`). No Drive-API provisioning automation.
- **Dedicated Sheets client in the collectors module** — structurally similar
  to the backend's `GoogleDataLoaderService` (service account,
  `SPREADSHEETS_READONLY`) but not shared code, since that service is
  hardcoded to one spreadsheet with fixed tabs.
- Dependency note: `gradle/libs.versions.toml:92-94` already defines
  `google-api-services-sheets` / `google-api-client` /
  `google-auth-library-oauth2-http`; only `museum-railway-backend` declares
  them, so the collectors module needs the `implementation(...)` lines added.

Also needs: a sheet **template** (column schema, validation, malformed-row
handling — see Open Questions) documented for handing to associations.

**Done when:** the collector reads a manually-registered per-association
sheet and pushes at least one real association's events into Boudicca, and a
documented template sheet exists.

### 4. Remove PrimeVue — **planned**

Remove `primevue`, `@primevue/nuxt-module`, `primeflex`, `primeicons` from
`museum-railway-web`. Footprint: 8 files use PrimeVue components (Button,
Checkbox, Chip, Card, Fieldset, Calendar, Drawer, …); PrimeFlex utility
classes are used for layout throughout; no Tailwind config exists today.
The replacement approach (headless primitives vs. hand-rolled vs. another
library) is deliberately undecided — resolve in `/architect` when this item
starts.

**Done when:** the four packages are gone from `package.json` and no template
references remain.

### 5. Restyle event filtering — **planned**

Redesign the filtering UX: `EventFilters.vue`, the mobile filter drawer in
`events.vue`, and the active-filter chips. Functionally equivalent (same
facets, no behavior regression).

**Sequencing warning:** `EventFilters.vue` is one of the heaviest PrimeVue
consumers — restyling before item 4 risks doing the work twice. Consider
merging items 4+5 into one frontend-overhaul effort, or ordering 4 → 5.

### 6. Event location & route — **planned**, two phases

- **Phase A (UI first):** display location (and route, if available) per
  event using data that already exists — `MuseumLocation.lat/lon` and
  `geoJsonUrl` (currently defined but unused anywhere in the codebase).
- **Phase B (collectors):** extend eventcollectors to capture per-event
  location data. Today events only carry `LOCATION_ID`/`OPERATOR_ID`
  references — no per-event coordinates or route exist in the model.

Phase A can proceed without resolving the route data model; Phase B is
blocked on it (see Open Questions).

**Done when:** (A) event location is visibly displayed for events that have
location data; (B) collectors populate per-event location data per the
resolved model.

## Cross-cutting done-conditions

Every item, on completion:

- Existing tests pass with 0 new failures (`./gradlew test`, frontend tests).
- Typecheck passes with 0 new errors (`./gradlew build`; frontend via
  `npm run build` — no dedicated `typecheck` script exists yet, consider
  adding `nuxt typecheck` during the frontend work).

## Out of scope (for now)

- Automating Google Sheet provisioning/sharing per association.
- Historical backfill of location/route data for past events.

## Open Questions

- **Route data model** (blocks item 6 Phase B): does "route" mean the static
  railway line track (reused across all events at an operator/location), or
  a per-event route that can differ (e.g. charter trips)?
- **Sequencing of items 4 and 5**: separate or merged frontend overhaul?
- **Sheet template design** (blocks item 3): column schema, validation rules,
  malformed-row handling — needs its own mini-spec, natural fit for the
  item-3 PRD.
