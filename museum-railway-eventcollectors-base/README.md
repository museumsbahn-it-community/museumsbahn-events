This is just a stopgap, because boudicca's eventcollector base module is not published to maven.

Vendored from boudicca's monorepo: https://github.com/boudicca-events/boudicca.events (package `base.boudicca.api.eventcollector` is kept as-is from upstream).

Remove this module and depend on the published artifact once boudicca publishes it (see the `// TODO: remove again when published by boudicca` comment in `settings.gradle.kts` and the commented-out `boudicca-dateparserlib`/eventcollector-base entries in `gradle/libs.versions.toml`).

License GPLv3
