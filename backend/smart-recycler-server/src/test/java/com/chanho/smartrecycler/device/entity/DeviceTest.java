package com.chanho.smartrecycler.device.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceTest {

    @Test
    void heartbeat_doesNotOverwriteStoppedStatus() {
        // given
        Device device = new Device("EDGE-TEST-001", "Test Zone");
        device.updateStatus(DeviceStatus.STOPPED);

        // when
        device.updateHeartbeat();

        // then
        assertThat(device.getStatus()).isEqualTo(DeviceStatus.STOPPED);
    }

    @Test
    void heartbeat_doesNotOverwriteMaintenanceStatus() {
        // given
        Device device = new Device("EDGE-TEST-002", "Test Zone");
        device.updateStatus(DeviceStatus.MAINTENANCE);

        // when
        device.updateHeartbeat();

        // then
        assertThat(device.getStatus()).isEqualTo(DeviceStatus.MAINTENANCE);
    }

    @Test
    void heartbeat_recoversOfflineDeviceToRunning() {
        // given
        Device device = new Device("EDGE-TEST-003", "Test Zone");
        device.updateStatus(DeviceStatus.OFFLINE);

        // when
        device.updateHeartbeat();

        // then
        assertThat(device.getStatus()).isEqualTo(DeviceStatus.RUNNING);
    }
}
