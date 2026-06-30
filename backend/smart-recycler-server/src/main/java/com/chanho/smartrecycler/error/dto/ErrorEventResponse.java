package com.chanho.smartrecycler.error.dto;

import com.chanho.smartrecycler.error.entity.ErrorEvent;
import com.chanho.smartrecycler.error.entity.ErrorSeverity;
import com.chanho.smartrecycler.error.entity.ErrorType;

import java.time.LocalDateTime;

public class ErrorEventResponse {

    private Long id;
    private String deviceId;
    private ErrorType errorType;
    private ErrorSeverity severity;
    private String message;
    private LocalDateTime createdAt;

    public ErrorEventResponse(ErrorEvent event) {
        this.id = event.getId();
        this.deviceId = event.getDeviceId();
        this.errorType = event.getErrorType();
        this.severity = event.getSeverity();
        this.message = event.getMessage();
        this.createdAt = event.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public ErrorSeverity getSeverity() {
        return severity;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
