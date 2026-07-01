package com.chanho.smartrecycler.devicecommand.dto;

import com.chanho.smartrecycler.devicecommand.entity.DeviceCommand;
import com.chanho.smartrecycler.devicecommand.entity.DeviceCommandStatus;
import com.chanho.smartrecycler.devicecommand.entity.DeviceCommandType;

import java.time.LocalDateTime;

public class DeviceCommandResponse {

    private Long id;
    private String deviceId;
    private DeviceCommandType commandType;
    private DeviceCommandStatus status;
    private String payload;
    private String resultMessage;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public DeviceCommandResponse(DeviceCommand command) {
        this.id = command.getId();
        this.deviceId = command.getDeviceId();
        this.commandType = command.getCommandType();
        this.status = command.getStatus();
        this.payload = command.getPayload();
        this.resultMessage = command.getResultMessage();
        this.createdAt = command.getCreatedAt();
        this.completedAt = command.getCompletedAt();
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
