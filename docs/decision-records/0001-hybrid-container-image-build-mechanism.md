# 0001: Hybrid container image build mechanism (bootBuildImage + Exec)

- **Status**: Accepted
- **Date**: 2026-09-13
- **Supersedes**: Decision 1 in an earlier internal CI/CD architecture draft ("Chosen: the
  existing custom `imageBuild` Gradle task ... for all modules"), which predates this record. That
  draft lives in local planning notes, not in this repo.

## Context

Three Gradle modules each produce a container image today via a hand-written, per-module
`imageBuild` task (type `Exec`) that shells out to `<containerEngine> build -f <Dockerfile>`:

- `museum-railway-backend` (Spring Boot)
- `museum-railway-eventcollectors` (Spring Boot)
- `museum-railway-web` (Nuxt/npm — **not** a Spring Boot project)

Root `build.gradle.kts` also has partial wiring for Spring Boot's `bootBuildImage` (Cloud Native
Buildpacks) task — a `tasks.withType<BootBuildImage> { docker { host = ... } }` block — but no
module actually configures `imageName`/tags for it, and nothing invokes it. An earlier version of
this plan treated that as evidence `bootBuildImage` was abandoned in favor of `imageBuild`
everywhere, and chose to standardize on `imageBuild` for all three modules.

That earlier framing didn't account for the structural difference between the modules:
`museum-railway-web` has no Spring Boot plugin applied and never can use `bootBuildImage` — it
has no `bootJar`/buildpacks entry point. Backend and eventcollectors, by contrast, both apply the
Spring Boot Gradle plugin and are exactly what `bootBuildImage` is built for. Standardizing all
three on the hand-rolled `Exec` task means backend and eventcollectors carry a maintained
Dockerfile and a manual `Exec` wrapper for something Spring Boot can do natively with less code
and buildpack-managed base-image/CVE patching.

## Decision

Use a **hybrid** build mechanism, split by whether a module is Spring Boot:

- **`museum-railway-backend`, `museum-railway-eventcollectors`** (Spring Boot): switch to the
  `bootBuildImage` task (Cloud Native Buildpacks). Each module gets an explicit `imageName`
  (parameterized by `-PimageVersion`, default `latest`, so the no-argument case is unchanged in
  spirit) instead of relying on the buildpacks default name. Their existing hand-written
  Dockerfiles (`src/main/docker/Dockerfile`) and custom `imageBuild` Exec tasks are removed once
  `bootBuildImage` is proven equivalent (see the merged architecture plan's build phases for the
  exact cutover sequencing).
- **`museum-railway-web`** (Nuxt/npm, no Spring Boot): keeps its existing custom `imageBuild` Exec
  task against its checked-in `Dockerfile`. There is no buildpacks equivalent available to it, and
  no reason to invent one — a working Dockerfile-based build already exists.

Both paths are parameterized consistently (`-PimageVersion=<tag>`) so a single CI job can invoke
`./gradlew :museum-railway-backend:bootBuildImage :museum-railway-eventcollectors:bootBuildImage :museum-railway-web:imageBuild -PimageVersion=<tag>`
and get all three images built and named the same way, regardless of which mechanism produced
them.

## Consequences

- Backend and eventcollectors no longer need their `src/main/docker/Dockerfile` maintained by
  hand once the cutover is verified — buildpacks manages the base image, layering, and (over time)
  security patching of the OS/JRE layer.
- The root `build.gradle.kts` `BootBuildImage` docker-host socket block (currently wired for a
  single rootless Podman socket path) becomes load-bearing rather than dead configuration. Its
  correctness under a **podman-in-podman** self-hosted CI runner (rather than a plain local
  podman socket) is not yet verified — tracked as a risk in the CI/CD architecture plan, not
  assumed away by this record.
- `museum-railway-web` remains on a structurally different build path than its two siblings. This
  is intentional and permanent, not a temporary gap — do not attempt to force it onto
  `bootBuildImage` in a future cleanup; it has no Spring Boot plugin and cannot use it.
- CLAUDE.md's "Key Technical Details" section, which currently doesn't mention either mechanism by
  name, and any future contributor docs describing "the container build" should point at this
  record rather than re-deriving the reasoning from the Gradle files.

## Rejected alternatives

- **Standardize all three modules on the custom `imageBuild` Exec task** (the original framing):
  rejected because it ignores that two of the three modules are Spring Boot projects for which
  buildpacks is the more maintained, less bespoke path — keeping backend/eventcollectors on a
  hand-rolled Dockerfile is debt for no benefit once CI is being rebuilt anyway.
- **Standardize all three on `bootBuildImage`**: impossible — `museum-railway-web` has no Spring
  Boot plugin and no JVM entry point for buildpacks to target. Not a real option, just noted to
  make clear it was considered and ruled out structurally, not by preference.
