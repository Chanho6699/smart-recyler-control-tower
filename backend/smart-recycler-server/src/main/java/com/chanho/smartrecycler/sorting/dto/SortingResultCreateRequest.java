package com.chanho.smartrecycler.sorting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SortingResultCreateRequest {

    @NotNull
    private Long classificationLogId;

    @NotBlank
    private String deviceId;

    @NotBlank
    private String label;

    @NotBlank
    private String targetBin;

    private String action;

    @NotBlank
    private String status;

    private Integer actuatorTimeMs;

    private String failureReason;

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

    public String getAction() {
        return action;
    }

    public String getStatus() {
        return status;
    }

    public Integer getActuatorTimeMs() {
        return actuatorTimeMs;
    }

    public String getFailureReason() {
        return failureReason;
    }
}
