package com.chanho.smartrecycler.devicecommand.service;

import com.chanho.smartrecycler.device.entity.Device;
import com.chanho.smartrecycler.device.entity.DeviceStatus;
import com.chanho.smartrecycler.device.repository.DeviceRepository;
import com.chanho.smartrecycler.devicecommand.dto.DeviceCommandCreateRequest;
import com.chanho.smartrecycler.devicecommand.dto.DeviceCommandResponse;
import com.chanho.smartrecycler.devicecommand.dto.DeviceCommandResultRequest;
import com.chanho.smartrecycler.devicecommand.entity.DeviceCommand;
import com.chanho.smartrecycler.devicecommand.entity.DeviceCommandStatus;
import com.chanho.smartrecycler.devicecommand.entity.DeviceCommandType;
import com.chanho.smartrecycler.devicecommand.repository.DeviceCommandRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceCommandService {

    private final DeviceCommandRepository deviceCommandRepository;
    private final DeviceRepository deviceRepository;

    public DeviceCommandService(
            DeviceCommandRepository deviceCommandRepository,
            DeviceRepository deviceRepository
    ) {
        this.deviceCommandRepository = deviceCommandRepository;
        this.deviceRepository = deviceRepository;
    }

    @Transactional
    public DeviceCommandResponse createCommand(DeviceCommandCreateRequest request) {
        DeviceCommandType commandType = parseCommandType(request.getCommandType());

        boolean hasPendingCommand = deviceCommandRepository.existsByDeviceIdAndStatus(
                request.getDeviceId(),
                DeviceCommandStatus.PENDING
        );

        if (hasPendingCommand) {
            throw new IllegalStateException(
                    "Pending command already exists for deviceId=" + request.getDeviceId()
            );
        }

        DeviceCommand command = new DeviceCommand(
                request.getDeviceId(),
                commandType,
                request.getPayload()
        );

        DeviceCommand savedCommand = deviceCommandRepository.save(command);

        return new DeviceCommandResponse(savedCommand);
    }

    @Transactional(readOnly = true)
    public List<DeviceCommandResponse> getCommands() {
        return deviceCommandRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(DeviceCommandResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DeviceCommandResponse> getCommandsByDeviceId(String deviceId) {
        return deviceCommandRepository.findAllByDeviceIdOrderByCreatedAtDesc(deviceId)
                .stream()
                .map(DeviceCommandResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<DeviceCommandResponse> getPendingCommand(String deviceId) {
        return deviceCommandRepository
                .findFirstByDeviceIdAndStatusOrderByCreatedAtAsc(
                        deviceId,
                        DeviceCommandStatus.PENDING
                )
                .map(DeviceCommandResponse::new);
    }

    @Transactional
    public DeviceCommandResponse reportCommandResult(
            Long commandId,
            DeviceCommandResultRequest request
    ) {
        DeviceCommand command = deviceCommandRepository.findById(commandId)
                .orElseThrow(() -> new IllegalArgumentException("Device command not found. id=" + commandId));

        DeviceCommandStatus resultStatus = parseResultStatus(request.getStatus());

        command.markResult(
                resultStatus,
                request.getResultMessage()
        );

        if (resultStatus == DeviceCommandStatus.COMPLETED) {
            updateDeviceStatusByCompletedCommand(command);
        }

        return new DeviceCommandResponse(command);
    }

    private void updateDeviceStatusByCompletedCommand(DeviceCommand command) {
        Optional<Device> optionalDevice = deviceRepository.findByDeviceId(command.getDeviceId());

        if (optionalDevice.isEmpty()) {
            return;
        }

        Device device = optionalDevice.get();

        switch (command.getCommandType()) {
            case EMERGENCY_STOP -> device.changeStatus(DeviceStatus.STOPPED);
            case RESUME_OPERATION -> device.changeStatus(DeviceStatus.RUNNING);
            case ENTER_MAINTENANCE -> device.changeStatus(DeviceStatus.MAINTENANCE);
            case EXIT_MAINTENANCE -> device.changeStatus(DeviceStatus.RUNNING);
            case RESTART_DEVICE -> device.changeStatus(DeviceStatus.RUNNING);
            case RESET_BIN, UPDATE_THRESHOLD -> {
                // 상태 변경 없음
            }
        }
    }

    private DeviceCommandType parseCommandType(String value) {
        try {
            return DeviceCommandType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid command type: " + value);
        }
    }

    private DeviceCommandStatus parseResultStatus(String value) {
        try {
            DeviceCommandStatus status = DeviceCommandStatus.valueOf(value.toUpperCase());

            if (status == DeviceCommandStatus.PENDING) {
                throw new IllegalArgumentException("Result status cannot be PENDING.");
            }

            return status;

        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid command result status: " + value);
        }
    }
}
