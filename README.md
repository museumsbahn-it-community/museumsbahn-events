# museumsbahn-events

This is the code behind the page museumsbahn-events.at, an event aggregation platform for Austrian heritage railways.

## Goal

The goal of the museumsbahn-events plattform is to crawl events from all associations across Austria
which are involved in preserving and running heritage trains.

The main idea is that this project does not cause any additional work for the associations, but
gives a single point of entry for people who are planning their next trips and want to know
which events are happening.

## Architecture

As a central hub we use boudicca.events and the eventdb provided by boudicca.
We implemented some custom eventcollectors, which are fully compatible with boudicca.

Additionally we use a separate backend to provide additional information like museum 
locations, opening hours, contact details and so on.

See `CLAUDE.md` for the full module breakdown, build commands, and technical details.

## Current Limitations

## Development

### Requirements

All required tool versions (Java, Node, Python/uv) are declared in the root `mise.toml`. With
[mise](https://mise.jdt.io/) installed, run:

```bash
mise install
```

to provision the full toolchain in one shot. If you're not using mise, you need at minimum:

- Java 21
- Node (see `mise.toml` for the pinned version)
- Gradle (via the included `./gradlew` wrapper, no separate install needed)

It's recommended to use IntelliJ or VsCode as IDE. Nuxt support in both is unfortunately not great.

### Knowledge graph (graphify)

This repo maintains a queryable knowledge graph of the codebase under `graphify-out/` (gitignored,
regenerate locally — it's not shipped with a fresh clone). It's built and queried through the
`graphify` skill inside Claude Code:

1. First run: `uv tool install graphifyy` (needs `uv`, provisioned by `mise install` above) — or
   just invoke `/graphify` once, which installs it automatically if missing.
2. Rebuild after code changes: `/graphify --update` (incremental, re-extracts only changed files).
3. Re-cluster without re-extracting: `/graphify --cluster-only`.
4. Ask questions about the codebase directly: `/graphify query "<question>"`.

See `CLAUDE.md` for more detail on how this is expected to be used during agent-driven work.

### Modifying the API and releasing a new version

1. bump the version in build.gradle.kts
2. modify the kotlin api in museum-railway-api
3. adapt and start the backend
4. find the openapi.yaml at http://localhost:8080/v3/api-docs.yaml
5. copy the contents to `museum-railway-web/src/main/resources/museum-railway-backend.yaml`
6. run `generateTypescriptClient`

in the generated typescript client follow the README.md, but generally you can run
```
npm install
npm run build
npm publish
```

for dry run you can use
```
npm publish --dry-run
```

### Local Setup Links

When running the full stack via `docker compose -f infrastructure/docker-compose.yml up`, these are
reachable locally:

| What | URL | Notes |
| --- | --- | --- |
| Main application (frontend, via Traefik) | http://localhost:3050 | Entry point, proxies `/api`, `/ingest`, `/entries`, `/ical` etc. to the backing services |
| Traefik dashboard | http://localhost:3090/dashboard/ | Routing/service overview (`--api.insecure=true`) |
| Frontend (direct, bypassing Traefik) | http://localhost:3010 | Nuxt dev container |
| Backend API | http://localhost:8080 | Also serves the OpenAPI spec at `/v3/api-docs.yaml` |
| Boudicca EventDB | http://localhost:8081 | Raw event storage |
| Boudicca Search | http://localhost:8082 | `/api/search` behind Traefik |
| Eventcollectors monitoring webui | http://localhost:8083 | Shows collected events/errors per run. Always on (Spring Boot auto-configuration, no profile needed), reached via its own published port directly — not routed through Traefik. Not to be confused with the separate `debug` Spring profile (`LocalCollectorDebug.kt`), which swaps in a one-shot local collector test harness instead of the real scheduled/ingesting app |
| ImgProxy | http://localhost:8090 | Image caching/signing backend |

### Local Store File

When running EventDB with the supplied docker-compose it saves its data into the file `boudicca.store` in the boudicca.store/ folder of the project. So if you want to
clean the EventDB, stop it, delete the file and restart it.

# Deployment

The best way to deploy is to use a docker compose

## generate signing keys for imgproxy

If you need a random key/salt pair in a hurry, you can quickly generate one using the following snippet:

```
echo $(xxd -g 2 -l 64 -p /dev/random | tr -d '\n')
```

for details see: https://docs.imgproxy.net/configuration/options#url-signature

## GDPR conforming matomo configuration

https://matomo.org/faq/how-to/how-do-i-configure-matomo-without-tracking-consent-for-french-visitors-cnil-exemption/

https://matomo.org/wp-content/uploads/2021/10/Matomo-Analytics-Exemption-from-Tracking-Consent-in-France-CNIL.pdf

https://matomo.org/faq/general/configure-privacy-settings-in-matomo/

https://matomo.org/faq/how-to/how-do-i-enforce-tracking-without-cookies/

How do I go cookieless (disable all cookies) for a visitor?

https://matomo.org/faq/general/faq_157/

### Checklist

- have user opt out
- Automatically Anonymise Visitor IPs and replace User IDs
- use anonymized IP for geolocation
- have no user id / no login
- regularly delete old raw visitor data - 90 days
- delete old aggregated report data - 6 months
