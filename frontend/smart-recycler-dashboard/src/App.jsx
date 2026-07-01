import { useEffect, useState } from 'react';
import axios from 'axios';
import './App.css';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

function App() {
  const [summary, setSummary] = useState(null);
  const [devices, setDevices] = useState([]);
  const [bins, setBins] = useState([]);
  const [errorEvents, setErrorEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [lastUpdatedAt, setLastUpdatedAt] = useState(null);
  const [errorMessage, setErrorMessage] = useState('');
  const [sortingResults, setSortingResults] = useState([]);
  const [deviceCommands, setDeviceCommands] = useState([]);
  const [commandSubmitting, setCommandSubmitting] = useState(false);

  const fetchDashboardData = async () => {
    try {
      const [
  summaryResponse,
  devicesResponse,
  binsResponse,
  errorEventsResponse,
  sortingResultsResponse,
  deviceCommandsResponse,
] = await Promise.all([
  axios.get(`${API_BASE_URL}/api/statistics/summary`),
  axios.get(`${API_BASE_URL}/api/devices`),
  axios.get(`${API_BASE_URL}/api/bins`),
  axios.get(`${API_BASE_URL}/api/error-events`),
  axios.get(`${API_BASE_URL}/api/sorting-results`),
  axios.get(`${API_BASE_URL}/api/device-commands`),
]);

setSummary(summaryResponse.data);
setDevices(devicesResponse.data);
setBins(binsResponse.data);
setErrorEvents(errorEventsResponse.data);
setSortingResults(sortingResultsResponse.data);
setDeviceCommands(deviceCommandsResponse.data);

    } catch (error) {
      console.error(error);
      setErrorMessage('서버 데이터를 불러오지 못했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const createDeviceCommand = async (deviceId, commandType, payload) => {
  try {
    setCommandSubmitting(true);

    await axios.post(`${API_BASE_URL}/api/device-commands`, {
      deviceId,
      commandType,
      payload,
    });

    await fetchDashboardData();
  } catch (error) {
    console.error('Failed to create device command:', error);
    alert('단말기 명령 생성에 실패했습니다.');
  } finally {
    setCommandSubmitting(false);
  }
};

  const resetBin = async (deviceId, binType) => {
    const confirmed = window.confirm(`${deviceId}의 ${binType} 수거함을 비우시겠습니까?`);

    if (!confirmed) {
      return;
    }

    try {
      await axios.post(`${API_BASE_URL}/api/bins/reset`, {
        deviceId,
        binType,
      });

      await fetchDashboardData();
    } catch (error) {
      console.error(error);
      alert('수거함 초기화에 실패했습니다.');
    }
  };

  const resolveErrorEvent = async (eventId) => {
    const confirmed = window.confirm('이 에러 이벤트를 처리 완료하시겠습니까?');

    if (!confirmed) {
      return;
    }

    try {
      await axios.patch(`${API_BASE_URL}/api/error-events/${eventId}/resolve`);
      await fetchDashboardData();
    } catch (error) {
      console.error(error);
      alert('에러 이벤트 처리 완료에 실패했습니다.');
    }
  };

const updateDeviceStatus = async (deviceId, status) => {
  const confirmed = window.confirm(`${deviceId} 상태를 ${status}로 변경하시겠습니까?`);

  if (!confirmed) {
    return;
  }

  try {
    await axios.patch(`${API_BASE_URL}/api/devices/${deviceId}/status`, {
      status,
    });

    await fetchDashboardData();
  } catch (error) {
    console.error(error);
    alert('단말기 상태 변경에 실패했습니다.');
  }
  };


  
  useEffect(() => {
    fetchDashboardData();

    const intervalId = setInterval(() => {
      fetchDashboardData();
    }, 5000);

    return () => clearInterval(intervalId);
  }, []);

  const formatDateTime = (value) => {
    if (!value) return '-';
    return new Date(value).toLocaleString();
  };

  const getBinUsagePercentage = (bin) => {
    if (bin.usagePercentage !== undefined && bin.usagePercentage !== null) {
      return bin.usagePercentage;
    }

    if (!bin.capacity || bin.capacity === 0) {
      return 0;
    }

    return Math.round((bin.itemCount / bin.capacity) * 100);
  };

  if (loading) {
    return (
      <main className="page">
        <h1>Smart Recycler Control Tower</h1>
        <p>대시보드 데이터를 불러오는 중입니다...</p>
      </main>
    );
  }

const pendingCommandCount = deviceCommands.filter(
  (command) => command.status === 'PENDING'
).length;

const completedCommandCount = deviceCommands.filter(
  (command) => command.status === 'COMPLETED'
).length;

const failedCommandCount = deviceCommands.filter(
  (command) => command.status === 'FAILED'
).length;

  const hasPendingCommand = (deviceId) => {
  return deviceCommands.some(
    (command) =>
      command.deviceId === deviceId &&
      command.status === 'PENDING'
  );
};

const isCommandButtonDisabled = (device, commandType) => {
  if (commandSubmitting) {
    return true;
  }

  if (hasPendingCommand(device.deviceId)) {
    return true;
  }

  if (device.status === 'OFFLINE') {
    return true;
  }

  if (commandType === 'EMERGENCY_STOP') {
    return device.status === 'STOPPED';
  }

  if (commandType === 'RESUME_OPERATION') {
    return device.status === 'RUNNING';
  }

  if (commandType === 'ENTER_MAINTENANCE') {
    return device.status === 'MAINTENANCE';
  }

  if (commandType === 'EXIT_MAINTENANCE') {
    return device.status !== 'MAINTENANCE';
  }

  return false;
};

  return (
    <main className="page">
      <header className="dashboard-header">
        <div>
          <h1>Smart Recycler Control Tower</h1>
          <p>가상 단말기 기반 재활용 분류 관제 대시보드</p>
        </div>

        <button className="refresh-button" onClick={fetchDashboardData}>
          새로고침
        </button>
      </header>

      {errorMessage && <div className="error-banner">{errorMessage}</div>}

      <section className="summary-grid">
        <SummaryCard title="전체 단말기" value={summary?.totalDevices ?? 0} />
        <SummaryCard title="작동 중 단말기" value={summary?.runningDevices ?? 0} />
        <SummaryCard title="오프라인 상태 단말기" value={summary?.offlineDevices ?? 0} />
        <SummaryCard title="에러 상태 단말기" value={summary?.errorDevices ?? 0} />
        <SummaryCard title="점검 중 단말기" value={summary?.maintenanceDevices ?? 0} />
        <SummaryCard title="정지 단말기" value={summary?.stoppedDevices ?? 0} />
        <SummaryCard title="오늘 분류 수" value={summary?.todayClassificationLogs ?? 0} />
        <SummaryCard title="전체 분류 수" value={summary?.totalClassificationLogs ?? 0} />
        <SummaryCard title="전체 에러" value={summary?.totalErrorEvents ?? 0} />
        <SummaryCard title="미처리 에러" value={summary?.openErrorEvents ?? 0} />
        <SummaryCard title="처리 완료 에러" value={summary?.resolvedErrorEvents ?? 0} />
        <SummaryCard title="CRITICAL 에러" value={summary?.criticalErrorEvents ?? 0} />
        <SummaryCard title="수거함 누적량" value={summary?.totalItemsInBins ?? 0} />
        <SummaryCard title="동작 결과 전체" value={summary?.totalSortingResults ?? 0} />
<SummaryCard title="동작 성공" value={summary?.completedSortingResults ?? 0} />
<SummaryCard title="동작 실패" value={summary?.failedSortingResults ?? 0} />
<SummaryCard
  title="동작 성공률"
  value={`${(summary?.sortingSuccessRate ?? 0).toFixed(1)}%`}
/>
<SummaryCard title="대기 명령" value={pendingCommandCount} />
<SummaryCard title="완료 명령" value={completedCommandCount} />
<SummaryCard title="실패 명령" value={failedCommandCount} />
      </section>

      <section className="content-grid">
        <section className="panel">
          <div className="panel-header">
            <h2>단말기 상태</h2>
            <span>{devices.length} devices</span>
          </div>

          <div className="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Device ID</th>
                  <th>Location</th>
                  <th>Status</th>
                  <th>Last Heartbeat</th>
                  <th>Commands</th>
                </tr>
              </thead>
              <tbody>
                {devices.map((device) => (
                  <tr key={device.id}>
                    <td>{device.deviceId}</td>
                    <td>{device.location}</td>
                    <td>
                      <StatusBadge status={device.status} />
                    </td>
                    <td>{formatDateTime(device.lastHeartbeatAt)}</td>
                    

  <td>
  <div className="command-buttons">
    <button
      className="command-button danger"
      disabled={isCommandButtonDisabled(device, 'EMERGENCY_STOP')}
      onClick={() =>
        createDeviceCommand(
          device.deviceId,
          'EMERGENCY_STOP',
          'Emergency stop from dashboard.'
        )
      }
    >
      긴급 정지
    </button>

    <button
      className="command-button"
      disabled={isCommandButtonDisabled(device, 'RESUME_OPERATION')}
      onClick={() =>
        createDeviceCommand(
          device.deviceId,
          'RESUME_OPERATION',
          'Resume operation from dashboard.'
        )
      }
    >
      운영 재개
    </button>

    <button
      className="command-button secondary"
      disabled={isCommandButtonDisabled(device, 'ENTER_MAINTENANCE')}
      onClick={() =>
        createDeviceCommand(
          device.deviceId,
          'ENTER_MAINTENANCE',
          'Enter maintenance mode from dashboard.'
        )
      }
    >
      점검
    </button>

    <button
      className="command-button secondary"
      disabled={isCommandButtonDisabled(device, 'EXIT_MAINTENANCE')}
      onClick={() =>
        createDeviceCommand(
          device.deviceId,
          'EXIT_MAINTENANCE',
          'Exit maintenance mode from dashboard.'
        )
      }
    >
      점검 해제
    </button>

    <button
      className="command-button"
      disabled={isCommandButtonDisabled(device, 'RESTART_DEVICE')}
      onClick={() =>
        createDeviceCommand(
          device.deviceId,
          'RESTART_DEVICE',
          'Restart device from dashboard.'
        )
      }
    >
      재시작
    </button>
  </div>

      {hasPendingCommand(device.deviceId) && (
  <div className="pending-command-label">
    명령 대기중
  </div>
)}

</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>

        <section className="panel">
  <div className="panel-header">
    <h2>최근 단말기 명령</h2>
    <span>{deviceCommands.length} commands</span>
  </div>

  <div className="table-wrapper">
    <table>
      <thead>
        <tr>
          <th>Device ID</th>
          <th>Command Type</th>
          <th>Status</th>
          <th>Payload</th>
          <th>Result Message</th>
          <th>Created At</th>
          <th>Completed At</th>
        </tr>
      </thead>
      <tbody>
        {deviceCommands.slice(0, 10).map((command) => (
          <tr key={command.id}>
            <td>{command.deviceId}</td>
            <td>{command.commandType}</td>
            <td>
              <StatusBadge status={command.status} />
            </td>
            <td>{command.payload ?? '-'}</td>
            <td>{command.resultMessage ?? '-'}</td>
            <td>{formatDateTime(command.createdAt)}</td>
            <td>{formatDateTime(command.completedAt)}</td>
          </tr>
        ))}
      </tbody>
    </table>
  </div>
</section>

        <section className="panel">
          <div className="panel-header">
            <h2>품목별 누적 수거량</h2>
          </div>

          <div className="bin-summary-list">
            {summary?.binItemCounts &&
              Object.entries(summary.binItemCounts).map(([binType, count]) => (
                <div className="bin-summary-item" key={binType}>
                  <span>{binType}</span>
                  <strong>{count}</strong>
                </div>
              ))}
          </div>
        </section>
      </section>

      <section className="panel">
        <div className="panel-header">
          <h2>수거함 상태</h2>
          <span>{bins.length} bins</span>
        </div>

        <div className="bin-grid">
          {bins.map((bin) => {
            const usage = getBinUsagePercentage(bin);

            return (
              <article className="bin-card" key={bin.id}>
                <div className="bin-card-header">
                  <strong>{bin.deviceId}</strong>
                  <StatusBadge status={bin.status} />
                </div>

                <p>{bin.binType}</p>

                <div className="progress-track">
                  <div
                    className="progress-bar"
                    style={{ width: `${Math.min(usage, 100)}%` }}
                  />
                </div>

                <div className="bin-card-footer">
                  <span>
                    {bin.itemCount} / {bin.capacity}
                  </span>
                  <span>{usage}%</span>
                </div>

                <button
                  className="reset-bin-button"
                  onClick={() => resetBin(bin.deviceId, bin.binType)}
                >
                  수거함 비우기
                </button>
              </article>
            );
          })}
        </div>
      </section>

      <section className="panel">
  <div className="panel-header">
    <h2>최근 분류 동작 결과</h2>
    <span>{sortingResults.length} results</span>
  </div>

  <div className="table-wrapper">
    <table>
      <thead>
        <tr>
          <th>Device ID</th>
          <th>Label</th>
          <th>Target Bin</th>
          <th>Action</th>
          <th>Status</th>
          <th>Actuator Time</th>
          <th>Failure Reason</th>
          <th>Created At</th>
        </tr>
      </thead>
      <tbody>
        {sortingResults.slice(0, 10).map((result) => (
          <tr key={result.id}>
            <td>{result.deviceId}</td>
            <td>{result.label}</td>
            <td>{result.targetBin}</td>
            <td>{result.action}</td>
            <td>
              <StatusBadge status={result.status} />
            </td>
            <td>{result.actuatorTimeMs ?? '-'} ms</td>
            <td>{result.failureReason ?? '-'}</td>
            <td>{formatDateTime(result.createdAt)}</td>
          </tr>
        ))}
      </tbody>
    </table>
  </div>
</section>

      <section className="panel">
        <div className="panel-header">
          <h2>최근 에러 이벤트</h2>
          <span>{errorEvents.length} events</span>
        </div>

        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>Device ID</th>
                <th>Type</th>
                <th>Severity</th>
                <th>Status</th>
                <th>Message</th>
                <th>Created At</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {errorEvents.slice(0, 10).map((event) => {
                const eventStatus = event.eventStatus ?? 'OPEN';

                return (
                  <tr key={event.id}>
                    <td>{event.deviceId}</td>
                    <td>{event.errorType}</td>
                    <td>
                      <StatusBadge status={event.severity} />
                    </td>
                    <td>
                      <StatusBadge status={eventStatus} />
                    </td>
                    <td>{event.message}</td>
                    <td>{formatDateTime(event.createdAt)}</td>
                    <td>
                      {eventStatus === 'OPEN' ? (
                        <button
                          className="resolve-event-button"
                          onClick={() => resolveErrorEvent(event.id)}
                        >
                          처리 완료
                        </button>
                      ) : (
                        <span className="resolved-text">완료됨</span>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        </div>
      </section>

      <footer className="footer">
        마지막 갱신: {lastUpdatedAt ? lastUpdatedAt.toLocaleTimeString() : '-'}
      </footer>
    </main>
  );
}

function SummaryCard({ title, value }) {
  return (
    <article className="summary-card">
      <p>{title}</p>
      <strong>{value}</strong>
    </article>
  );
}

function StatusBadge({ status }) {
  return (
    <span className={`status-badge status-${String(status).toLowerCase()}`}>
      {status}
    </span>
  );
}

export default App;
