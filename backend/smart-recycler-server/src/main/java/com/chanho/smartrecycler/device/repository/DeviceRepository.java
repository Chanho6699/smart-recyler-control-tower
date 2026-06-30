package com.chanho.smartrecycler.device.repository;

import com.chanho.smartrecycler.device.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findByDeviceId(String deviceId);

    boolean existsByDeviceId(String deviceId);

    List<Device> findAllByOrderByUpdatedAtDesc();
}
