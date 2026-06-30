package com.chanho.smartrecycler.classification.dto;

import com.chanho.smartrecycler.classification.entity.ClassificationLog;
import java.time.LocalDateTime;

public class ClassificationLogResponse {

    private Long id;
    private String deviceId;
    private String label;
    private double confidence;
    private String targetBin;
    private long inferenceTimeMs;
    private String runtimeType;
    private LocalDateTime createdAt;

    public ClassificationLogResponse(ClassificationLog log) {
        this.id = log.getId();
        this.deviceId = log.getDeviceId();
        this.label = log.getLabel();
        this.confidence = log.getConfidence();
        this.targetBin = log.getTargetBin();
        this.inferenceTimeMs = log.getInferenceTimeMs();
        this.runtimeType = log.getRuntimeType();
        this.createdAt = log.getCreatedAt();
    }

    public Long getId() {
        return id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getLabel() {
        return label;
    }

    public double getConfidence() {
        return confidence;
    }

    public String getTargetBin() {
        return targetBin;
    }

    public long getInferenceTimeMs() {
        return inferenceTimeMs;
    }

    public String getRuntimeType() {
        return runtimeType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
