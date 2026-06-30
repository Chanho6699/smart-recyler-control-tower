package com.chanho.smartrecycler.error.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "error_events")
public class ErrorEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;

    @Enumerated(EnumType.STRING)
    private ErrorType errorType;

    @Enumerated(EnumType.STRING)
    private ErrorSeverity severity;

    @Enumerated(EnumType.STRING)
    private ErrorEventStatus eventStatus;

    @Column(length = 1000)
    private String message;

    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

    protected ErrorEvent() {
    }

    public ErrorEvent(
            String deviceId,
            ErrorType errorType,
            ErrorSeverity severity,
            String message
    ) {
        this.deviceId = deviceId;
        this.errorType = errorType;
        this.severity = severity;
        this.eventStatus = ErrorEventStatus.OPEN;
        this.message = message;
        this.createdAt = LocalDateTime.now();
    }

    public void resolve() {
        this.eventStatus = ErrorEventStatus.RESOLVED;
        this.resolvedAt = LocalDateTime.now();
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
        if (eventStatus == null) {
            return ErrorEventStatus.OPEN;
        }
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
