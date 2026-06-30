package com.chanho.smartrecycler.device.dto;

import com.chanho.smartrecycler.device.entity.Device;
import com.chanho.smartrecycler.device.entity.DeviceStatus;

import java.time.LocalDateTime;

public class DeviceResponse {

    private Long id;
    private String deviceId;
    private String location;
    private DeviceStatus status;
    private LocalDateTime lastHeartbeatAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public DeviceResponse(Device device) {
        this.id = device.getId();
        this.deviceId = device.getDeviceId();
        this.location = device.getLocation();
        this.status = device.getStatus();
        this.lastHeartbeatAt = device.getLastHeartbeatAt();
        this.createdAt = device.getCreatedAt();
        this.updatedAt = device.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getLocation() {
        return location;
    }

    public DeviceStatus getStatus() {
        return status;
    }

    public LocalDateTime getLastHeartbeatAt() {
        return lastHeartbeatAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
