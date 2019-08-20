#!/usr/bin/env bash
set -euo pipefail

open_api_spec=target/slack_web.json

mkdir -p $(dirname $open_api_spec)

# Download the Slack's OpenAPI specification.
curl \
    --fail \
    --retry 3 \
    --output $open_api_spec \
    https://api.slack.com/specs/openapi/v2/slack_web.json

clojure -Abuild -m slack-api.gen $open_api_spec
