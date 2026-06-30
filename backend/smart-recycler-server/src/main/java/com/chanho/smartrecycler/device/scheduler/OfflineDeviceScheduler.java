package com.chanho.smartrecycler.device.scheduler;

import com.chanho.smartrecycler.device.service.DeviceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OfflineDeviceScheduler {

    private static final long OFFLINE_THRESHOLD_SECONDS = 30;

    private final DeviceService deviceService;

    public OfflineDeviceScheduler(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Scheduled(fixedRate = 10000)
    public void detectOfflineDevices() {
        int offlineCount = deviceService.markOfflineDevices(OFFLINE_THRESHOLD_SECONDS);

        if (offlineCount > 0) {
            System.out.println("[OFFLINE DETECTED] offline devices count = " + offlineCount);
        }
    }
}
