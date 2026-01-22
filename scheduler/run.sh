#!/bin/sh
set -e
curl -sS -X POST "$BACKEND_URL/api/bookings/daily-job" \
  -H "X-Internal-Token: $INTERNAL_JOB_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{}' >/dev/null
