package com.chanho.smartrecycler.device.dto;

import jakarta.validation.constraints.NotBlank;

public class HeartbeatRequest {

    @NotBlank
    private String deviceId;

    public String getDeviceId() {
        return deviceId;
    }
}
