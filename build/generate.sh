#!/usr/bin/env bash
set -euo pipefail

###
# Generate the web_api.edn descriptor file and a set of request specs from the
# OpenAPI specification provided by Slack.
###

open_api_spec=target/slack_web_openapi_v2.json

mkdir -p $(dirname $open_api_spec)

# Download the Slack's OpenAPI specification.
curl \
    --fail \
    --retry 3 \
    --output $open_api_spec \
    https://raw.githubusercontent.com/slackapi/slack-api-specs/master/web-api/slack_web_openapi_v2.json \

    clojure -Abuild -m slack-api.gen $open_api_spec
