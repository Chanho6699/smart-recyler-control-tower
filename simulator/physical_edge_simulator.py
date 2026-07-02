import argparse
import time
from datetime import datetime

import requests

from edge.client.control_tower_client import ControlTowerClient
from edge.ai.fake_classifier import FakeClassifier
from edge.actuator.virtual_actuator import VirtualActuator


def register_device(client, device_id, location):
    try:
        client.register_device({
            "deviceId": device_id,
            "location": location,
        })

        print(f"[REGISTER] {device_id} location={location}")

    except requests.RequestException as e:
        print(f"[REGISTER ERROR] {device_id} {e}")


def send_heartbeat(client, device_id):
    try:
        client.send_heartbeat({
            "deviceId": device_id,
        })

    except requests.RequestException as e:
        print(f"[HEARTBEAT ERROR] {device_id} {e}")


def report_command_result(client, command_id, status, result_message):
    return client.report_command_result(
        command_id,
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


def process_pending_command(client, device_id, device_state):
    try:
        command = client.get_pending_command(device_id)

        if command is None:
            return None

        command_id = command["id"]
        command_type = command["commandType"]

        print(f"[COMMAND RECEIVED] {device_id} command={command_type} id={command_id}")

        status, result_message = execute_command(command, device_state)

        report_command_result(
            client,
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


def send_classification_log(client, device_id, classification):
    payload = {
        "deviceId": device_id,
        "label": classification["label"],
        "confidence": classification["confidence"],
        "targetBin": classification["targetBin"],
        "inferenceTimeMs": classification["inferenceTimeMs"],
        "runtimeType": classification["runtimeType"],
    }

    return client.send_classification_log(payload)


def send_sorting_result(
    client,
    device_id,
    classification_log_id,
    classification,
    actuator_result,
):
    payload = {
        "classificationLogId": classification_log_id,
        "deviceId": device_id,
        "label": classification["label"],
        "targetBin": classification["targetBin"],
        "action": classification["action"],
        "status": actuator_result["status"],
        "actuatorTimeMs": actuator_result["actuatorTimeMs"],
        "failureReason": actuator_result["failureReason"],
    }

    client.send_sorting_result(payload)

    return actuator_result["status"], actuator_result["failureReason"]


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--server", default="http://localhost:8080")
    parser.add_argument("--devices", type=int, default=3)
    parser.add_argument("--duration", type=int, default=120)
    parser.add_argument("--interval", type=float, default=2.0)
    parser.add_argument("--success-rate", type=float, default=0.85)

    args = parser.parse_args()

    client = ControlTowerClient(args.server)
    classifier = FakeClassifier()
    actuator = VirtualActuator(success_rate=args.success_rate)

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
            client,
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

            send_heartbeat(client, device_id)

            command_result = process_pending_command(
                client,
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

            classification = classifier.classify()

            try:
                classification_response = send_classification_log(
                    client,
                    device_id,
                    classification,
                )

                classification_log_id = classification_response["id"]
                total_classification += 1

                actuator_result = actuator.execute(classification["action"])

                status, failure_reason = send_sorting_result(
                    client,
                    device_id,
                    classification_log_id,
                    classification,
                    actuator_result,
                )

                if status == "COMPLETED":
                    total_completed += 1
                    print(
                        f"[SORT OK] {device_id} "
                        f"{classification['label']} -> {classification['targetBin']}"
                    )
                else:
                    total_failed += 1
                    print(
                        f"[SORT FAIL] {device_id} "
                        f"{classification['label']} reason={failure_reason}"
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
