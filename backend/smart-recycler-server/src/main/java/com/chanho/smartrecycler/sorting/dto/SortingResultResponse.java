package com.chanho.smartrecycler.sorting.dto;

import com.chanho.smartrecycler.sorting.entity.SortingAction;
import com.chanho.smartrecycler.sorting.entity.SortingResult;
import com.chanho.smartrecycler.sorting.entity.SortingResultStatus;

import java.time.LocalDateTime;

public class SortingResultResponse {

    private Long id;
    private Long classificationLogId;
    private String deviceId;
    private String label;
    private String targetBin;
    private SortingAction action;
    private SortingResultStatus status;
    private Integer actuatorTimeMs;
    private String failureReason;
    private LocalDateTime createdAt;

    public SortingResultResponse(SortingResult result) {
        this.id = result.getId();
        this.classificationLogId = result.getClassificationLogId();
        this.deviceId = result.getDeviceId();
        this.label = result.getLabel();
        this.targetBin = result.getTargetBin();
        this.action = result.getAction();
        this.status = result.getStatus();
        this.actuatorTimeMs = result.getActuatorTimeMs();
        this.failureReason = result.getFailureReason();
        this.createdAt = result.getCreatedAt();
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
