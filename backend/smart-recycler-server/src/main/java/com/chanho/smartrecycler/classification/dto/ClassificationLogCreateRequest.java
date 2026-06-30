package com.chanho.smartrecycler.classification.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public class ClassificationLogCreateRequest {

    @NotBlank
    private String deviceId;

    @NotBlank
    private String label;

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private double confidence;

    @NotBlank
    private String targetBin;

    @PositiveOrZero
    private long inferenceTimeMs;

    @NotBlank
    private String runtimeType;

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
}
