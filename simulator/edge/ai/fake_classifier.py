import random


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


class FakeClassifier:
    """label, confidence, targetBin, action, inferenceTimeMs, runtimeType을 생성하는 가짜 AI 분류기."""

    def classify(self):
        sample = random.choice(SAMPLES)

        return {
            "label": sample["label"],
            "targetBin": sample["targetBin"],
            "action": sample["action"],
            "confidence": round(random.uniform(0.70, 0.99), 2),
            "inferenceTimeMs": random.randint(20, 120),
            "runtimeType": "FAKE_AI",
        }
