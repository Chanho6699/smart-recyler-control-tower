package com.chanho.smartrecycler.bin.dto;

import jakarta.validation.constraints.NotBlank;

public class BinResetRequest {

    @NotBlank
    private String deviceId;

    @NotBlank
    private String binType;

    public String getDeviceId() {
        return deviceId;
    }

    public String getBinType() {
        return binType;
    }
}
