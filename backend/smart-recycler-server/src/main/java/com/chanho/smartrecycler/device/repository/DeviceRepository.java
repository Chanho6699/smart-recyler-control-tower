package com.chanho.smartrecycler.device.repository;

import com.chanho.smartrecycler.device.entity.Device;
import com.chanho.smartrecycler.device.entity.DeviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findByDeviceId(String deviceId);

    boolean existsByDeviceId(String deviceId);

    List<Device> findAllByOrderByUpdatedAtDesc();

    List<Device> findByLastHeartbeatAtBeforeAndStatus(
            LocalDateTime threshold,
            DeviceStatus status
    );

    long countByStatus(DeviceStatus status);
}
