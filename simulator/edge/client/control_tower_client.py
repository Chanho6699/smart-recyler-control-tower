import requests


class ControlTowerClient:
    """Control Tower 서버와 통신하는 순수 HTTP client.

    payload 생성 로직은 갖지 않으며, 완성된 payload를 그대로 전송한다.
    """

    def __init__(self, server_url):
        self.server_url = server_url

    def _post(self, path, payload):
        response = requests.post(
            f"{self.server_url}{path}",
            json=payload,
            timeout=5,
        )
        response.raise_for_status()
        return response.json()

    def _patch(self, path, payload):
        response = requests.patch(
            f"{self.server_url}{path}",
            json=payload,
            timeout=5,
        )
        response.raise_for_status()
        return response.json()

    def _get(self, path):
        response = requests.get(
            f"{self.server_url}{path}",
            timeout=5,
        )

        if response.status_code == 204:
            return None

        response.raise_for_status()
        return response.json()

    def register_device(self, payload):
        return self._post("/api/devices/register", payload)

    def send_heartbeat(self, payload):
        return self._post("/api/devices/heartbeat", payload)

    def get_pending_command(self, device_id):
        return self._get(f"/api/device-commands/devices/{device_id}/pending")

    def report_command_result(self, command_id, payload):
        return self._patch(
            f"/api/device-commands/{command_id}/result",
            payload,
        )

    def send_classification_log(self, payload):
        return self._post("/api/classification-logs", payload)

    def send_sorting_result(self, payload):
        return self._post("/api/sorting-results", payload)
