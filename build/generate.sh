#!/usr/bin/env bash
set -euo pipefail

mkdir -p target

# Download the Slack's OpenAPI specification.
curl \
    --fail \
    --retry 3 \
    --output target/slack_web.json \
    https://api.slack.com/specs/openapi/v2/slack_web.json
