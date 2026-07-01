package com.chanho.smartrecycler.devicecommand.repository;

import com.chanho.smartrecycler.devicecommand.entity.DeviceCommand;
import com.chanho.smartrecycler.devicecommand.entity.DeviceCommandStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceCommandRepository extends JpaRepository<DeviceCommand, Long> {

    List<DeviceCommand> findAllByOrderByCreatedAtDesc();

    List<DeviceCommand> findAllByDeviceIdOrderByCreatedAtDesc(String deviceId);

    Optional<DeviceCommand> findFirstByDeviceIdAndStatusOrderByCreatedAtAsc(
            String deviceId,
            DeviceCommandStatus status
    );

    boolean existsByDeviceIdAndStatus(
            String deviceId,
            DeviceCommandStatus status
    );
}
