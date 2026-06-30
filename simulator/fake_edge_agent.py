import time
import random
import threading
from datetime import datetime

import requests


SERVER_URL = "http://localhost:8080"
DEVICE_ID = "EDGE-001"
LOCATION = "1F Cafe"

HEARTBEAT_INTERVAL_SECONDS = 5
CLASSIFICATION_INTERVAL_SECONDS = 3


SAMPLE_CLASSES = [
    {
        "label": "plastic_bottle",
        "targetBin": "PLASTIC"
    },
    {
        "label": "plastic_cup",
        "targetBin": "PLASTIC"
    },
    {
        "label": "paper_cup",
        "targetBin": "PAPER"
    },
    {
        "label": "can",
        "targetBin": "CAN"
    },
    {
        "label": "unknown",
        "targetBin": "UNKNOWN"
    }
]


def register_device():
    url = f"{SERVER_URL}/api/devices/register"

    payload = {
        "deviceId": DEVICE_ID,
        "location": LOCATION
    }

    try:
        response = requests.post(url, json=payload, timeout=3)
        print(f"[REGISTER] status={response.status_code}, body={response.text}")
    except requests.RequestException as e:
        print(f"[REGISTER ERROR] {e}")


def send_heartbeat():
    url = f"{SERVER_URL}/api/devices/heartbeat"

    while True:
        payload = {
            "deviceId": DEVICE_ID
        }

        try:
            response = requests.post(url, json=payload, timeout=3)
            print(f"[HEARTBEAT] {datetime.now()} status={response.status_code}")
        except requests.RequestException as e:
            print(f"[HEARTBEAT ERROR] {e}")

        time.sleep(HEARTBEAT_INTERVAL_SECONDS)


def send_classification_log():
    url = f"{SERVER_URL}/api/classification-logs"

    while True:
        sample = random.choice(SAMPLE_CLASSES)

        payload = {
            "deviceId": DEVICE_ID,
            "label": sample["label"],
            "confidence": round(random.uniform(0.75, 0.99), 2),
            "targetBin": sample["targetBin"],
            "inferenceTimeMs": random.randint(20, 120),
            "runtimeType": "FAKE"
        }

        try:
            response = requests.post(url, json=payload, timeout=3)
            print(
                f"[CLASSIFICATION] label={payload['label']}, "
                f"bin={payload['targetBin']}, "
                f"confidence={payload['confidence']}, "
                f"status={response.status_code}"
            )
        except requests.RequestException as e:
            print(f"[CLASSIFICATION ERROR] {e}")

        time.sleep(CLASSIFICATION_INTERVAL_SECONDS)


def main():
    print("======================================")
    print(" Smart Recycler Fake Edge Agent")
    print("======================================")
    print(f"DEVICE_ID = {DEVICE_ID}")
    print(f"SERVER_URL = {SERVER_URL}")
    print()

    register_device()

    heartbeat_thread = threading.Thread(target=send_heartbeat, daemon=True)
    classification_thread = threading.Thread(target=send_classification_log, daemon=True)

    heartbeat_thread.start()
    classification_thread.start()

    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        print("\n[STOP] Fake edge agent stopped.")


if __name__ == "__main__":
    main()
