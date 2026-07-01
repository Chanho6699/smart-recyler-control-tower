import argparse
import random
import time
from datetime import datetime

import requests


SAMPLES = [
    {
        "label": "plastic_bottle",
        "targetBin": "PLASTIC",
        "action": "MOVE_TO_PLASTIC_BIN",
    },
    {
        "label": "plastic_cup",
        "targetBin": "PLASTIC",
        "action": "MOVE_TO_PLASTIC_BIN",
    },
    {
        "label": "paper_cup",
        "targetBin": "PAPER",
        "action": "MOVE_TO_PAPER_BIN",
    },
    {
        "label": "can",
        "targetBin": "CAN",
        "action": "MOVE_TO_CAN_BIN",
    },
    {
        "label": "unknown",
        "targetBin": "UNKNOWN",
        "action": "MOVE_TO_UNKNOWN_BIN",
    },
]


def post(server_url, path, payload):
    response = requests.post(
        f"{server_url}{path}",
        json=payload,
        timeout=5,
    )
    response.raise_for_status()
    return response.json()


def patch(server_url, path, payload):
    response = requests.patch(
        f"{server_url}{path}",
        json=payload,
        timeout=5,
    )
    response.raise_for_status()
    return response.json()


def get(server_url, path):
    response = requests.get(
        f"{server_url}{path}",
        timeout=5,
    )

    if response.status_code == 204:
        return None

    response.raise_for_status()
    return response.json()


def register_device(server_url, device_id, location):
    try:
        post(server_url, "/api/devices/register", {
            "deviceId": device_id,
            "location": location,
        })

        print(f"[REGISTER] {device_id} location={location}")

    except requests.RequestException as e:
        print(f"[REGISTER ERROR] {device_id} {e}")


def send_heartbeat(server_url, device_id):
    try:
        post(server_url, "/api/devices/heartbeat", {
            "deviceId": device_id,
        })

    except requests.RequestException as e:
        print(f"[HEARTBEAT ERROR] {device_id} {e}")


def get_pending_command(server_url, device_id):
    return get(
        server_url,
        f"/api/device-commands/devices/{device_id}/pending",
    )


def report_command_result(server_url, command_id, status, result_message):
    return patch(
        server_url,
        f"/api/device-commands/{command_id}/result",
        {
            "status": status,
            "resultMessage": result_message,
        },
    )


def execute_command(command, device_state):
    command_type = command["commandType"]
    payload = command.get("payload")

    if command_type == "EMERGENCY_STOP":
        device_state["stopped"] = True
        return "COMPLETED", "Emergency stop executed. Sorting work is now stopped."

    if command_type == "RESUME_OPERATION":
        device_state["stopped"] = False
        device_state["maintenance"] = False
        return "COMPLETED", "Device operation resumed."

    if command_type == "ENTER_MAINTENANCE":
        device_state["maintenance"] = True
        return "COMPLETED", "Device entered maintenance mode."

    if command_type == "EXIT_MAINTENANCE":
        device_state["maintenance"] = False
        return "COMPLETED", "Device exited maintenance mode."

    if command_type == "RESTART_DEVICE":
        time.sleep(1)
        device_state["stopped"] = False
        return "COMPLETED", "Virtual device restarted."

    if command_type == "RESET_BIN":
        return "COMPLETED", f"Virtual bin reset command accepted. payload={payload}"

    if command_type == "UPDATE_THRESHOLD":
        try:
            new_threshold = float(payload)
            device_state["confidence_threshold"] = new_threshold
            return "COMPLETED", f"Confidence threshold updated to {new_threshold}"
        except (TypeError, ValueError):
            return "FAILED", f"Invalid threshold payload: {payload}"

    return "FAILED", f"Unknown command type: {command_type}"


def process_pending_command(server_url, device_id, device_state):
    try:
        command = get_pending_command(server_url, device_id)

        if command is None:
            return None

        command_id = command["id"]
        command_type = command["commandType"]

        print(f"[COMMAND RECEIVED] {device_id} command={command_type} id={command_id}")

        status, result_message = execute_command(command, device_state)

        report_command_result(
            server_url,
            command_id,
            status,
            result_message,
        )

        print(f"[COMMAND RESULT] {device_id} command={command_type} status={status}")

        return status

    except requests.RequestException as e:
        print(f"[COMMAND ERROR] {device_id} {e}")
        return "FAILED"

    except KeyError as e:
        print(f"[COMMAND RESPONSE ERROR] {device_id} missing={e}")
        return "FAILED"


def send_classification_log(server_url, device_id, sample, device_state):
    confidence = round(random.uniform(0.70, 0.99), 2)
    threshold = device_state["confidence_threshold"]

    payload = {
        "deviceId": device_id,
        "label": sample["label"],
        "confidence": confidence,
        "targetBin": sample["targetBin"],
        "inferenceTimeMs": random.randint(20, 120),
        "runtimeType": "FAKE_AI",
    }

    return post(server_url, "/api/classification-logs", payload)


def send_sorting_result(
    server_url,
    device_id,
    classification_log_id,
    sample,
    success_rate,
):
    actuator_time_ms = random.randint(150, 800)
    time.sleep(actuator_time_ms / 1000)

    is_success = random.random() < success_rate

    if is_success:
        status = "COMPLETED"
        failure_reason = None
    else:
        status = "FAILED"
        failure_reason = random.choice([
            "Virtual actuator jam detected.",
            "Servo timeout while moving item.",
            "Item slipped during sorting motion.",
        ])

    payload = {
        "classificationLogId": classification_log_id,
        "deviceId": device_id,
        "label": sample["label"],
        "targetBin": sample["targetBin"],
        "action": sample["action"],
        "status": status,
        "actuatorTimeMs": actuator_time_ms,
        "failureReason": failure_reason,
    }

    post(server_url, "/api/sorting-results", payload)

    return status, failure_reason


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--server", default="http://localhost:8080")
    parser.add_argument("--devices", type=int, default=3)
    parser.add_argument("--duration", type=int, default=120)
    parser.add_argument("--interval", type=float, default=2.0)
    parser.add_argument("--success-rate", type=float, default=0.85)

    args = parser.parse_args()

    device_ids = [
        f"EDGE-PHYSICAL-{i:03d}"
        for i in range(1, args.devices + 1)
    ]

    device_states = {
        device_id: {
            "stopped": False,
            "maintenance": False,
            "confidence_threshold": 0.70,
        }
        for device_id in device_ids
    }

    print("======================================")
    print(" Smart Recycler Physical Edge Simulator")
    print(" With Device Command Polling")
    print("======================================")
    print(f"server       : {args.server}")
    print(f"devices      : {args.devices}")
    print(f"duration     : {args.duration}s")
    print(f"interval     : {args.interval}s")
    print(f"success rate : {args.success_rate}")
    print("======================================")

    for index, device_id in enumerate(device_ids, start=1):
        register_device(
            args.server,
            device_id,
            f"Physical AI Zone {index}",
        )

    start_time = time.time()

    total_classification = 0
    total_completed = 0
    total_failed = 0
    total_command_completed = 0
    total_command_failed = 0
    total_request_failed = 0

    while time.time() - start_time < args.duration:
        for device_id in device_ids:
            device_state = device_states[device_id]

            send_heartbeat(args.server, device_id)

            command_result = process_pending_command(
                args.server,
                device_id,
                device_state,
            )

            if command_result == "COMPLETED":
                total_command_completed += 1
            elif command_result == "FAILED":
                total_command_failed += 1

            if device_state["stopped"]:
                print(f"[SKIP] {device_id} is stopped by command.")
                continue

            if device_state["maintenance"]:
                print(f"[SKIP] {device_id} is in maintenance mode.")
                continue

            sample = random.choice(SAMPLES)

            try:
                classification_response = send_classification_log(
                    args.server,
                    device_id,
                    sample,
                    device_state,
                )

                classification_log_id = classification_response["id"]
                total_classification += 1

                status, failure_reason = send_sorting_result(
                    args.server,
                    device_id,
                    classification_log_id,
                    sample,
                    args.success_rate,
                )

                if status == "COMPLETED":
                    total_completed += 1
                    print(
                        f"[SORT OK] {device_id} "
                        f"{sample['label']} -> {sample['targetBin']}"
                    )
                else:
                    total_failed += 1
                    print(
                        f"[SORT FAIL] {device_id} "
                        f"{sample['label']} reason={failure_reason}"
                    )

            except requests.RequestException as e:
                total_request_failed += 1
                print(f"[REQUEST ERROR] {device_id} {e}")

            except KeyError as e:
                total_request_failed += 1
                print(f"[RESPONSE ERROR] {device_id} missing={e}")

        print()
        print("========== Simulator Stats ==========")
        print(f"time                 : {datetime.now()}")
        print(f"classification logs  : {total_classification}")
        print(f"sorting completed    : {total_completed}")
        print(f"sorting failed       : {total_failed}")
        print(f"command completed    : {total_command_completed}")
        print(f"command failed       : {total_command_failed}")
        print(f"request failed       : {total_request_failed}")
        print("=====================================")
        print()

        time.sleep(args.interval)

    print("[DONE] Physical edge simulation finished.")


if __name__ == "__main__":
    main()
