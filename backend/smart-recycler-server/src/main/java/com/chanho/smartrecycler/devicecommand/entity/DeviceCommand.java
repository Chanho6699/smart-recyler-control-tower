package com.chanho.smartrecycler.devicecommand.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "device_commands")
public class DeviceCommand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "command_type", columnDefinition = "varchar(255)")
    private DeviceCommandType commandType;

    @Enumerated(EnumType.STRING)
    @Column(name = "command_status", columnDefinition = "varchar(255)")
    private DeviceCommandStatus status;

    @Column(length = 1000)
    private String payload;

    @Column(length = 1000)
    private String resultMessage;

    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    protected DeviceCommand() {
    }

    public DeviceCommand(
            String deviceId,
            DeviceCommandType commandType,
            String payload
    ) {
        this.deviceId = deviceId;
        this.commandType = commandType;
        this.status = DeviceCommandStatus.PENDING;
        this.payload = payload;
        this.createdAt = LocalDateTime.now();
    }

    public void markResult(DeviceCommandStatus status, String resultMessage) {
        this.status = status;
        this.resultMessage = resultMessage;
        this.completedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public DeviceCommandType getCommandType() {
        return commandType;
    }

    public DeviceCommandStatus getStatus() {
        return status;
    }

    public String getPayload() {
        return payload;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}
