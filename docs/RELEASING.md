# Releasing container images

museumsbahn-events.at publishes 3 container images (`backend`, `eventcollectors`, `web`) through
`.github/workflows/release.yml`. See
`docs/decision-records/0001-hybrid-container-image-build-mechanism.md` for how each image is
actually built.

## Tag scheme

Two kinds of tag, one workflow:

- **Release tag** — pushing a `v<version>` git tag (e.g. `v0.3.0`) builds and publishes all 3
  images tagged `<version>` (the leading `v` stripped). The workflow validates the tag against the
  Gradle `version` property first and fails without publishing anything if they don't match.
- **On-demand / staging tag** — manually running the workflow (`workflow_dispatch`) builds and
  publishes all 3 images tagged `staging-<short-sha>`, or `pr-<n>-<short-sha>` if a PR number is
  given as input. Use this to get a testable image for a branch or PR without cutting a release.

Neither path runs on an ordinary push or PR open — image builds are always either a deliberate tag
push or a deliberate manual trigger.

## Triggering a release

1. Bump `version` in the root `build.gradle.kts`.
2. Push a matching `v<version>` tag to the private mirror (the remote with the self-hosted
   runner — image builds only ever run there; see the architecture notes for why the workflow
   itself doesn't need to name that remote).
3. Watch the `release.yml` run; on success, all 3 images are pullable at
   `<registry>/twatzl/museum-railway-events-<service>:<version>`.

## Triggering an on-demand / staging build

Run `release.yml` via `workflow_dispatch` against the private mirror (from the branch you want
built — e.g. a PR's source branch), optionally supplying the PR number as input. This produces a
distinctly-tagged image for that branch without touching the release tag scheme.

## Local deployment

`infrastructure/docker-compose.yml` (local/dev use only, not a production deployment target — see
`infrastructure/README.md`) references the 3 images via `${REGISTRY_HOST}` and `${IMAGE_TAG}`
Compose variables instead of a hardcoded registry/tag. Set both in a local `.env` file (gitignored)
alongside `docker-compose.yml` before running `docker compose up`, e.g.:

```
REGISTRY_HOST=<your registry host>
IMAGE_TAG=<a tag published above>
```

The actual registry hostname is intentionally not documented in this public repo — ask whoever
administers the private mirror, or check the private, gitignored ops notes if you have access to
them.
