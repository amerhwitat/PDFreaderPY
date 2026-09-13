#!/usr/bin/env bash
set -euo pipefail
if [[ $# -gt 0 ]]; then exec "$@"; fi
for f in /app/main.py /app/app.py /app/run.py; do if [[ -f "$f" ]]; then exec python3 "$f"; fi; done
exec python3 -m http.server 8000 --directory /app
