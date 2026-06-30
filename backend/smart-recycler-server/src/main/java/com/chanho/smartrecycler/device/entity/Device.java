package com.chanho.smartrecycler.device.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "devices")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String deviceId;

    private String location;

    @Enumerated(EnumType.STRING)
    private DeviceStatus status;

    private LocalDateTime lastHeartbeatAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    protected Device() {
    }

    public Device(String deviceId, String location) {
        this.deviceId = deviceId;
        this.location = location;
        this.status = DeviceStatus.RUNNING;
        this.lastHeartbeatAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateHeartbeat() {
        this.lastHeartbeatAt = LocalDateTime.now();

        if (this.status == DeviceStatus.OFFLINE) {
            this.status = DeviceStatus.RUNNING;
        }

        if (this.status == null) {
            this.status = DeviceStatus.RUNNING;
        }

        this.updatedAt = LocalDateTime.now();
    }

    public void updateStatus(DeviceStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
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
