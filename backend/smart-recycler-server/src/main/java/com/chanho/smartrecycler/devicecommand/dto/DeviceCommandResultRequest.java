package com.chanho.smartrecycler.devicecommand.dto;

import jakarta.validation.constraints.NotBlank;

public class DeviceCommandResultRequest {

    @NotBlank
    private String status;

    private String resultMessage;

    public String getStatus() {
        return status;
    }

    public String getResultMessage() {
        return resultMessage;
    }
}
