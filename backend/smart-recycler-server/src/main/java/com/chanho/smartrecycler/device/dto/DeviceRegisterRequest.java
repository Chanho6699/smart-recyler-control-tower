package com.chanho.smartrecycler.device.dto;

import jakarta.validation.constraints.NotBlank;

public class DeviceRegisterRequest {

    @NotBlank
    private String deviceId;

    @NotBlank
    private String location;

    public String getDeviceId() {
        return deviceId;
    }

    public String getLocation() {
        return location;
    }
}
