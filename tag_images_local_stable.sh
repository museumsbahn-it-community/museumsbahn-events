#!/bin/sh

podman tag localhost/museum-railway-events-backend git.twatzl.eu/twatzl/museum-railway-events-backend:stable
podman tag localhost/museum-railway-events-eventcollectors git.twatzl.eu/twatzl/museum-railway-events-eventcollectors:stable
podman tag localhost/museum-railway-events-web git.twatzl.eu/twatzl/museum-railway-events-web:stable
