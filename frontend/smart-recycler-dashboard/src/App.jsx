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

  const fetchDashboardData = async () => {
    try {
      const [summaryResponse, devicesResponse, binsResponse, errorEventsResponse] =
        await Promise.all([
          axios.get(`${API_BASE_URL}/api/statistics/summary`),
          axios.get(`${API_BASE_URL}/api/devices`),
          axios.get(`${API_BASE_URL}/api/bins`),
          axios.get(`${API_BASE_URL}/api/error-events`),
        ]);

      setSummary(summaryResponse.data);
      setDevices(devicesResponse.data);
      setBins(binsResponse.data);
      setErrorEvents(errorEventsResponse.data);
      setLastUpdatedAt(new Date());
      setErrorMessage('');
    } catch (error) {
      console.error(error);
      setErrorMessage('서버 데이터를 불러오지 못했습니다. 백엔드 서버와 CORS 설정을 확인하세요.');
    } finally {
      setLoading(false);
    }
  };

const resetBin = async (deviceId, binType) => {
  const confirmed = window.confirm(
    `${deviceId}의 ${binType} 수거함을 비우시겠습니까?`
  );

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

      {errorMessage && (
        <div className="error-banner">
          {errorMessage}
        </div>
      )}

      <section className="summary-grid">
        <SummaryCard title="전체 단말기" value={summary?.totalDevices ?? 0} />
        <SummaryCard title="RUNNING" value={summary?.runningDevices ?? 0} />
        <SummaryCard title="OFFLINE" value={summary?.offlineDevices ?? 0} />
        <SummaryCard title="오늘 분류 수" value={summary?.todayClassificationLogs ?? 0} />
        <SummaryCard title="전체 분류 수" value={summary?.totalClassificationLogs ?? 0} />
        <SummaryCard title="전체 에러" value={summary?.totalErrorEvents ?? 0} />
        <SummaryCard title="CRITICAL 에러" value={summary?.criticalErrorEvents ?? 0} />
        <SummaryCard title="수거함 누적량" value={summary?.totalItemsInBins ?? 0} />
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
                  <span>{bin.itemCount} / {bin.capacity}</span>
                  <span>{usage}%</span>
                </div>
                
                <button className="reset-bin-button" onClick={() => resetBin(bin.deviceId, bin.binType)}>
                수거함 비우기
                </button>
              </article>
            );
          })}
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
                <th>Message</th>
                <th>Created At</th>
              </tr>
            </thead>
            <tbody>
              {errorEvents.slice(0, 10).map((event) => (
                <tr key={event.id}>
                  <td>{event.deviceId}</td>
                  <td>{event.errorType}</td>
                  <td>
                    <StatusBadge status={event.severity} />
                  </td>
                  <td>{event.message}</td>
                  <td>{formatDateTime(event.createdAt)}</td>
                </tr>
              ))}
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
