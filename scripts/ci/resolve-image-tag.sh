#!/usr/bin/env bash
# Computes the container image tag for a CI run, per the CI/CD architecture plan's tag scheme.
#
# Usage:
#   resolve-image-tag.sh --mode release --ref <git-ref> [--gradle-version <version>]
#   resolve-image-tag.sh --mode manual [--sha <short-sha>] [--pr-number <n>]
#
# release mode: strips the leading "v" from a "refs/tags/vX.Y.Z" ref and validates it against
#   the Gradle project's `version` property, failing (exit 1) on mismatch instead of emitting a
#   tag. Prints the validated version on success.
#
# manual mode: prints "pr-<n>-<short-sha>" if --pr-number is given, else "staging-<short-sha>".
#   --sha defaults to `git rev-parse --short HEAD`; --gradle-version defaults to the output of
#   `./gradlew properties -q` (only used in release mode).
set -euo pipefail

mode=""
ref=""
gradle_version=""
sha=""
pr_number=""

while [[ $# -gt 0 ]]; do
  case "$1" in
    --mode) mode="$2"; shift 2 ;;
    --ref) ref="$2"; shift 2 ;;
    --gradle-version) gradle_version="$2"; shift 2 ;;
    --sha) sha="$2"; shift 2 ;;
    --pr-number) pr_number="$2"; shift 2 ;;
    *) echo "resolve-image-tag.sh: unknown argument '$1'" >&2; exit 2 ;;
  esac
done

if [[ "$mode" != "release" && "$mode" != "manual" ]]; then
  echo "resolve-image-tag.sh: --mode must be 'release' or 'manual' (got '$mode')" >&2
  exit 2
fi

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

if [[ "$mode" == "release" ]]; then
  if [[ -z "$ref" ]]; then
    ref="${GITHUB_REF:-}"
  fi
  if [[ -z "$ref" ]]; then
    echo "resolve-image-tag.sh: release mode requires --ref (or \$GITHUB_REF)" >&2
    exit 2
  fi

  tag_name="${ref#refs/tags/}"
  if [[ "$tag_name" == "$ref" ]]; then
    echo "resolve-image-tag.sh: ref '$ref' is not a tag ref (expected refs/tags/vX.Y.Z)" >&2
    exit 1
  fi
  if [[ "$tag_name" != v* ]]; then
    echo "resolve-image-tag.sh: tag '$tag_name' does not start with 'v'" >&2
    exit 1
  fi
  release_version="${tag_name#v}"

  if [[ -z "$gradle_version" ]]; then
    gradle_version="$("$repo_root/gradlew" -q properties --console=plain 2>/dev/null | awk -F': ' '/^version:/ {print $2; exit}')"
  fi
  if [[ -z "$gradle_version" ]]; then
    echo "resolve-image-tag.sh: could not determine Gradle 'version' property" >&2
    exit 1
  fi

  if [[ "$release_version" != "$gradle_version" ]]; then
    echo "resolve-image-tag.sh: tag '$tag_name' (version '$release_version') does not match Gradle version '$gradle_version'" >&2
    exit 1
  fi

  echo "$release_version"
  exit 0
fi

# manual mode
if [[ -z "$sha" ]]; then
  sha="$(git -C "$repo_root" rev-parse --short HEAD)"
fi

if [[ -n "$pr_number" ]]; then
  echo "pr-${pr_number}-${sha}"
else
  echo "staging-${sha}"
fi
