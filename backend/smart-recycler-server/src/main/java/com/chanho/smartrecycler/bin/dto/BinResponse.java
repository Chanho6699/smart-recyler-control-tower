package com.chanho.smartrecycler.bin.dto;

import com.chanho.smartrecycler.bin.entity.Bin;
import com.chanho.smartrecycler.bin.entity.BinStatus;
import com.chanho.smartrecycler.bin.entity.BinType;

import java.time.LocalDateTime;

public class BinResponse {

    private Long id;
    private String deviceId;
    private BinType binType;
    private int itemCount;
    private int capacity;
    private double usagePercentage;
    private BinStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public BinResponse(Bin bin) {
        this.id = bin.getId();
        this.deviceId = bin.getDeviceId();
        this.binType = bin.getBinType();
        this.itemCount = bin.getItemCount();
        this.capacity = bin.getCapacity();
        this.usagePercentage = bin.getUsagePercentage();
        this.status = bin.getStatus();
        this.createdAt = bin.getCreatedAt();
        this.updatedAt = bin.getUpdatedAt();
    }

    public Long getId() {
        return id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public BinType getBinType() {
        return binType;
    }

    public int getItemCount() {
        return itemCount;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getUsagePercentage() {
        return usagePercentage;
    }

    public BinStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
