#!/bin/bash

API="http://localhost:8080"
SIMULATOR_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../simulator" && pwd)"

PASS_COUNT=0
FAIL_COUNT=0

pass() {
  echo "[PASS] $1"
  PASS_COUNT=$((PASS_COUNT + 1))
}

fail() {
  echo "[FAIL] $1"
  FAIL_COUNT=$((FAIL_COUNT + 1))
}

count_json_array() {
  python -c "import json,sys; print(len(json.load(sys.stdin)))"
}

echo "======================================"
echo " Smart Recycler Control Tower Smoke Test"
echo "======================================"
echo "server: $API"
echo

echo "--- 1. Backend server check ---"
STATUS_CODE=$(curl -s -o /dev/null -w "%{http_code}" --max-time 5 "$API/api/devices")

if [ "$STATUS_CODE" = "200" ]; then
  pass "Backend server is reachable at $API"
else
  fail "Backend server is not reachable at $API (http_code=$STATUS_CODE)"
  echo
  echo "Backend must be running before the rest of the smoke test can continue."
  echo "Summary: $PASS_COUNT passed, $FAIL_COUNT failed"
  exit 1
fi

echo
echo "--- 2. Key API endpoint checks ---"

check_endpoint() {
  local path="$1"
  local code
  code=$(curl -s -o /dev/null -w "%{http_code}" --max-time 5 "$API$path")

  if [ "$code" = "200" ]; then
    pass "GET $path -> $code"
  else
    fail "GET $path -> $code"
  fi
}

check_endpoint "/api/devices"
check_endpoint "/api/classification-logs"
check_endpoint "/api/sorting-results"
check_endpoint "/api/bins"
check_endpoint "/api/error-events"
check_endpoint "/api/device-commands"
check_endpoint "/api/statistics/summary"

echo
echo "--- 3. Simulator syntax check (py_compile) ---"

if (
  cd "$SIMULATOR_DIR" &&
  python -m py_compile \
    physical_edge_simulator.py \
    edge/ai/fake_classifier.py \
    edge/ai/yolo_classifier.py \
    edge/actuator/virtual_actuator.py \
    edge/client/control_tower_client.py
); then
  pass "Simulator modules compile without syntax errors"
else
  fail "Simulator modules failed to compile"
fi

echo
echo "--- 4. Short simulator run ---"

BEFORE_CLASSIFICATION=$(curl -s "$API/api/classification-logs" | count_json_array)
BEFORE_SORTING=$(curl -s "$API/api/sorting-results" | count_json_array)

echo "classification_logs before: $BEFORE_CLASSIFICATION"
echo "sorting_results before    : $BEFORE_SORTING"
echo

if (
  cd "$SIMULATOR_DIR" &&
  python physical_edge_simulator.py \
    --ai-runtime fake \
    --devices 1 \
    --duration 10 \
    --interval 2 \
    --success-rate 0.85
); then
  pass "Simulator ran to completion"
else
  fail "Simulator exited with an error"
fi

echo
echo "--- 5. classification_logs generation check ---"

AFTER_CLASSIFICATION=$(curl -s "$API/api/classification-logs" | count_json_array)
echo "classification_logs after: $AFTER_CLASSIFICATION"

if [ "$AFTER_CLASSIFICATION" -gt "$BEFORE_CLASSIFICATION" ]; then
  pass "classification_logs increased ($BEFORE_CLASSIFICATION -> $AFTER_CLASSIFICATION)"
else
  fail "classification_logs did not increase ($BEFORE_CLASSIFICATION -> $AFTER_CLASSIFICATION)"
fi

echo
echo "--- 6. sorting_results generation check ---"

AFTER_SORTING=$(curl -s "$API/api/sorting-results" | count_json_array)
echo "sorting_results after: $AFTER_SORTING"

if [ "$AFTER_SORTING" -gt "$BEFORE_SORTING" ]; then
  pass "sorting_results increased ($BEFORE_SORTING -> $AFTER_SORTING)"
else
  fail "sorting_results did not increase ($BEFORE_SORTING -> $AFTER_SORTING)"
fi

echo
echo "--- 7. Statistics summary ---"
curl -s "$API/api/statistics/summary" | jq

echo
echo "======================================"
echo " Smoke Test Summary: $PASS_COUNT passed, $FAIL_COUNT failed"
echo "======================================"

if [ "$FAIL_COUNT" -gt 0 ]; then
  exit 1
fi
