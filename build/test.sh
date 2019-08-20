#!/usr/bin/env bash
set -euo pipefail

clojure -Adev -m cognitect.test-runner $@
