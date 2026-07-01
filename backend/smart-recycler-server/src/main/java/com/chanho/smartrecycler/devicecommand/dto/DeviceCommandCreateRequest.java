package com.chanho.smartrecycler.devicecommand.dto;

import jakarta.validation.constraints.NotBlank;

public class DeviceCommandCreateRequest {

    @NotBlank
    private String deviceId;

    @NotBlank
    private String commandType;

    private String payload;

    public String getDeviceId() {
        return deviceId;
    }

    public String getCommandType() {
        return commandType;
    }

    public String getPayload() {
        return payload;
    }
}
