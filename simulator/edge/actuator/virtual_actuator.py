import random
import time


class VirtualActuator:
    """success_rate를 기반으로 가상 분류 동작을 수행하는 액추에이터."""

    def __init__(self, success_rate):
        self.success_rate = success_rate

    def execute(self, action):
        actuator_time_ms = random.randint(150, 800)
        time.sleep(actuator_time_ms / 1000)

        is_success = random.random() < self.success_rate

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

        return {
            "status": status,
            "actuatorTimeMs": actuator_time_ms,
            "failureReason": failure_reason,
        }
