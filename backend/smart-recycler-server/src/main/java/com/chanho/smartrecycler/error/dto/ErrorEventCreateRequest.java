package com.chanho.smartrecycler.error.dto;

import jakarta.validation.constraints.NotBlank;

public class ErrorEventCreateRequest {

    @NotBlank
    private String deviceId;

    @NotBlank
    private String errorType;

    @NotBlank
    private String severity;

    private String message;

    public String getDeviceId() {
        return deviceId;
    }

    public String getErrorType() {
        return errorType;
    }

    public String getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }
}
