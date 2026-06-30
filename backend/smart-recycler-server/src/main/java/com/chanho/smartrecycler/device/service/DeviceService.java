package com.chanho.smartrecycler.device.service;

import com.chanho.smartrecycler.device.dto.DeviceRegisterRequest;
import com.chanho.smartrecycler.device.dto.DeviceResponse;
import com.chanho.smartrecycler.device.dto.HeartbeatRequest;
import com.chanho.smartrecycler.device.entity.Device;
import com.chanho.smartrecycler.device.repository.DeviceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Transactional
    public DeviceResponse register(DeviceRegisterRequest request) {
        if (deviceRepository.existsByDeviceId(request.getDeviceId())) {
            Device existingDevice = deviceRepository.findByDeviceId(request.getDeviceId())
                    .orElseThrow(() -> new IllegalStateException("Device not found"));
            return new DeviceResponse(existingDevice);
        }

        Device device = new Device(
                request.getDeviceId(),
                request.getLocation()
        );

        Device savedDevice = deviceRepository.save(device);
        return new DeviceResponse(savedDevice);
    }

    @Transactional
    public DeviceResponse heartbeat(HeartbeatRequest request) {
        Device device = deviceRepository.findByDeviceId(request.getDeviceId())
                .orElseGet(() -> new Device(request.getDeviceId(), "UNKNOWN"));

        device.updateHeartbeat();

        Device savedDevice = deviceRepository.save(device);
        return new DeviceResponse(savedDevice);
    }

    @Transactional(readOnly = true)
    public List<DeviceResponse> getDevices() {
        return deviceRepository.findAllByOrderByUpdatedAtDesc()
                .stream()
                .map(DeviceResponse::new)
                .toList();
    }
}
