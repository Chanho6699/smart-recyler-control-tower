# Smart Recycler Control Tower

> Autonomous Edge AI 기반 스마트 재활용 분류 관제 시스템  
> Edge Device가 스스로 분류하고 행동하며, Control Tower는 상태와 결과를 통합 관리합니다.

---

## Overview

Smart Recycler Control Tower는 자율 Edge Device 기반 재활용 분류 관제 시스템입니다.

Edge Device는 AI 분류와 가상 sorting action을 직접 수행하고, Spring Boot 서버는 단말기 상태, 분류 결과, 수거함 현황, 에러 이벤트, 운영 명령을 관리합니다.

현재는 Python Edge Simulator 기반으로 동작하며, 향후 YOLO, ROS2, TensoRT 기반 실제 Edge AI 시스템으로 Isaacsim, 실제 단말기로 확장하는 것을 목표로 합니다.

---

## Architecture

```text
[React Dashboard]
        |
        v
[Spring Boot Control Tower]
        |
        v
[Python Edge Simulator]
  ├─ FakeClassifier
  ├─ VirtualActuator
  └─ ControlTowerClient
```

---

## Core Features

- Device register / heartbeat
- AI classification log 저장
- Sorting result 저장
- Sorting 성공 시 bin count 증가
- Sorting 실패 시 error event 생성
- Device command 발행 및 결과 추적
- React Dashboard 기반 모니터링
- Python Edge Simulator 모듈화

---

## Core Flow

```text
Edge Device
→ classify
→ sort
→ report result

Control Tower
→ monitor
→ store logs
→ manage errors
→ send commands
```

서버는 개별 sorting task를 직접 지시하지 않습니다.  
Edge Device가 자율적으로 분류와 동작을 수행하고, 서버는 그 결과를 관제합니다.

---

## Device Commands

| Command             | Result                       |
| ------------------- | ---------------------------- |
| `EMERGENCY_STOP`    | device becomes `STOPPED`     |
| `RESUME_OPERATION`  | device becomes `RUNNING`     |
| `ENTER_MAINTENANCE` | device becomes `MAINTENANCE` |
| `EXIT_MAINTENANCE`  | device becomes `RUNNING`     |
| `RESTART_DEVICE`    | device becomes `RUNNING`     |

---

## Edge Simulator Structure

```text
simulator/
  physical_edge_simulator.py
  edge/
    ai/
      fake_classifier.py
      yolo_classifier.py
    actuator/
      virtual_actuator.py
    client/
      control_tower_client.py
```

- `FakeClassifier`: 현재 사용하는 가상 AI 분류 모듈
- `VirtualActuator`: 가상 sorting action 실행 모듈
- `ControlTowerClient`: Spring Boot 서버와 HTTP 통신
- `YoloClassifier`: 향후 YOLO 연동을 위한 stub

---

## Tech Stack

| Area      | Stack                              |
| --------- | ---------------------------------- |
| Backend   | Java, Spring Boot, Spring Data JPA |
| Frontend  | React, Vite, Axios                 |
| Database  | MySQL                              |
| Simulator | Python                             |
| Infra     | Docker Compose                     |

---

## How to Run

```bash
docker-compose up -d
```

```bash
cd backend/smart-recycler-server
./gradlew bootRun
```

```bash
cd frontend/smart-recycler-dashboard
npm install
npm run dev
```

```bash
cd simulator
source .venv/bin/activate
python physical_edge_simulator.py --devices 3 --duration 120 --interval 2 --success-rate 0.85
```

---

## Current Status

Implemented:

- Spring Boot Control Tower
- React Dashboard
- MySQL persistence
- Python Edge Simulator
- Classification / Sorting / Command flow
- Edge Simulator module separation

Not implemented yet:

- Real YOLO inference
- Real camera input
- Real actuator control
- ROS2 integration
- Raspberry Pi hardware integration

---

## Roadmap

- Add YOLO-based classification
- Add ROS2 node-based edge runtime
- Integrate Raspberry Pi camera and actuator
- Add real-time dashboard updates
- Add authentication and authorization
