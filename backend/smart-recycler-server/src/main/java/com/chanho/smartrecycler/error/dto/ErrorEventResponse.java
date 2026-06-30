package com.chanho.smartrecycler.error.dto;

import com.chanho.smartrecycler.error.entity.ErrorEvent;
import com.chanho.smartrecycler.error.entity.ErrorEventStatus;
import com.chanho.smartrecycler.error.entity.ErrorSeverity;
import com.chanho.smartrecycler.error.entity.ErrorType;

import java.time.LocalDateTime;

public class ErrorEventResponse {

    private Long id;
    private String deviceId;
    private ErrorType errorType;
    private ErrorSeverity severity;
    private ErrorEventStatus eventStatus;
    private String message;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;

    public ErrorEventResponse(ErrorEvent event) {
        this.id = event.getId();
        this.deviceId = event.getDeviceId();
        this.errorType = event.getErrorType();
        this.severity = event.getSeverity();
        this.eventStatus = event.getEventStatus();
        this.message = event.getMessage();
        this.createdAt = event.getCreatedAt();
        this.resolvedAt = event.getResolvedAt();
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

    public ErrorEventStatus getEventStatus() {
        return eventStatus;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }
}
