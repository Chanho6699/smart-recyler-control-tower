package com.chanho.smartrecycler.sorting.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sorting_results")
public class SortingResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long classificationLogId;

    private String deviceId;

    private String label;

    private String targetBin;

    @Enumerated(EnumType.STRING)
    private SortingAction action;

    @Enumerated(EnumType.STRING)
    private SortingResultStatus status;

    private Integer actuatorTimeMs;

    @Column(length = 1000)
    private String failureReason;

    private LocalDateTime createdAt;

    protected SortingResult() {
    }

    public SortingResult(
            Long classificationLogId,
            String deviceId,
            String label,
            String targetBin,
            SortingAction action,
            SortingResultStatus status,
            Integer actuatorTimeMs,
            String failureReason
    ) {
        this.classificationLogId = classificationLogId;
        this.deviceId = deviceId;
        this.label = label;
        this.targetBin = targetBin;
        this.action = action;
        this.status = status;
        this.actuatorTimeMs = actuatorTimeMs;
        this.failureReason = failureReason;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getClassificationLogId() {
        return classificationLogId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getLabel() {
        return label;
    }

    public String getTargetBin() {
        return targetBin;
    }

    public SortingAction getAction() {
        return action;
    }

    public SortingResultStatus getStatus() {
        return status;
    }

    public Integer getActuatorTimeMs() {
        return actuatorTimeMs;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
