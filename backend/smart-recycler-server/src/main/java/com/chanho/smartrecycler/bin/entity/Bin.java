package com.chanho.smartrecycler.bin.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "bins",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"deviceId", "binType"})
        }
)
public class Bin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;

    @Enumerated(EnumType.STRING)
    private BinType binType;

    private int itemCount;

    private int capacity;

    @Enumerated(EnumType.STRING)
    private BinStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    protected Bin() {
    }

    public Bin(String deviceId, BinType binType, int capacity) {
        this.deviceId = deviceId;
        this.binType = binType;
        this.capacity = capacity;
        this.itemCount = 0;
        this.status = BinStatus.NORMAL;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseCount() {
        this.itemCount += 1;
        updateStatus();
        this.updatedAt = LocalDateTime.now();
    }

    public void reset() {
        this.itemCount = 0;
        this.status = BinStatus.NORMAL;
        this.updatedAt = LocalDateTime.now();
    }

    private void updateStatus() {
        double usagePercentage = getUsagePercentage();

        if (usagePercentage >= 100.0) {
            this.status = BinStatus.FULL;
        } else if (usagePercentage >= 80.0) {
            this.status = BinStatus.WARNING;
        } else {
            this.status = BinStatus.NORMAL;
        }
    }

    public double getUsagePercentage() {
        if (capacity == 0) {
            return 0.0;
        }
        return (itemCount * 100.0) / capacity;
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
