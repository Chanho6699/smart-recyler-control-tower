#!/bin/bash

API="http://localhost:8080"

echo "Creating demo device..."

curl -s -X POST "$API/api/devices/register" \
-H "Content-Type: application/json" \
-d '{
  "deviceId": "EDGE-DEMO-001",
  "location": "Demo Physical AI Zone"
}' | jq

echo "Creating successful classification log..."

SUCCESS_CLASSIFICATION_ID=$(curl -s -X POST "$API/api/classification-logs" \
-H "Content-Type: application/json" \
-d '{
  "deviceId": "EDGE-DEMO-001",
  "label": "plastic_bottle",
  "confidence": 0.96,
  "targetBin": "PLASTIC",
  "inferenceTimeMs": 35,
  "runtimeType": "FAKE_AI"
}' | jq -r '.id')

echo "SUCCESS_CLASSIFICATION_ID=$SUCCESS_CLASSIFICATION_ID"

echo "Creating successful sorting result..."

curl -s -X POST "$API/api/sorting-results" \
-H "Content-Type: application/json" \
-d "{
  \"classificationLogId\": $SUCCESS_CLASSIFICATION_ID,
  \"deviceId\": \"EDGE-DEMO-001\",
  \"label\": \"plastic_bottle\",
  \"targetBin\": \"PLASTIC\",
  \"action\": \"MOVE_TO_PLASTIC_BIN\",
  \"status\": \"COMPLETED\",
  \"actuatorTimeMs\": 320,
  \"failureReason\": null
}" | jq

echo "Creating failed classification log..."

FAILED_CLASSIFICATION_ID=$(curl -s -X POST "$API/api/classification-logs" \
-H "Content-Type: application/json" \
-d '{
  "deviceId": "EDGE-DEMO-001",
  "label": "can",
  "confidence": 0.91,
  "targetBin": "CAN",
  "inferenceTimeMs": 44,
  "runtimeType": "FAKE_AI"
}' | jq -r '.id')

echo "FAILED_CLASSIFICATION_ID=$FAILED_CLASSIFICATION_ID"

echo "Creating failed sorting result..."

curl -s -X POST "$API/api/sorting-results" \
-H "Content-Type: application/json" \
-d "{
  \"classificationLogId\": $FAILED_CLASSIFICATION_ID,
  \"deviceId\": \"EDGE-DEMO-001\",
  \"label\": \"can\",
  \"targetBin\": \"CAN\",
  \"action\": \"MOVE_TO_CAN_BIN\",
  \"status\": \"FAILED\",
  \"actuatorTimeMs\": 900,
  \"failureReason\": \"Virtual actuator jam detected.\"
}" | jq

echo "Demo data created."

echo "Summary:"
curl -s "$API/api/statistics/summary" | jq
