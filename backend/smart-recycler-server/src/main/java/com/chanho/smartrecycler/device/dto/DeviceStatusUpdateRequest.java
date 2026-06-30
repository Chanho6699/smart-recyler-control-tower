package com.chanho.smartrecycler.device.dto;

import jakarta.validation.constraints.NotBlank;

public class DeviceStatusUpdateRequest {

    @NotBlank
    private String status;

    public String getStatus() {
        return status;
    }
}
