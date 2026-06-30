package com.chanho.smartrecycler.classification.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "classification_logs")
public class ClassificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;

    private String label;

    private double confidence;

    private String targetBin;

    private long inferenceTimeMs;

    private String runtimeType;

    private LocalDateTime createdAt;

    protected ClassificationLog() {
    }

    public ClassificationLog(
            String deviceId,
            String label,
            double confidence,
            String targetBin,
            long inferenceTimeMs,
            String runtimeType
    ) {
        this.deviceId = deviceId;
        this.label = label;
        this.confidence = confidence;
        this.targetBin = targetBin;
        this.inferenceTimeMs = inferenceTimeMs;
        this.runtimeType = runtimeType;
        this.createdAt = LocalDateTime.now();
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
