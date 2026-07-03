package com.chanho.smartrecycler.devicecommand.service;

import com.chanho.smartrecycler.device.entity.Device;
import com.chanho.smartrecycler.device.entity.DeviceStatus;
import com.chanho.smartrecycler.device.repository.DeviceRepository;
import com.chanho.smartrecycler.devicecommand.dto.DeviceCommandCreateRequest;
import com.chanho.smartrecycler.devicecommand.dto.DeviceCommandResultRequest;
import com.chanho.smartrecycler.devicecommand.entity.DeviceCommand;
import com.chanho.smartrecycler.devicecommand.entity.DeviceCommandStatus;
import com.chanho.smartrecycler.devicecommand.entity.DeviceCommandType;
import com.chanho.smartrecycler.devicecommand.repository.DeviceCommandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceCommandServiceTest {

    @Mock
    private DeviceCommandRepository deviceCommandRepository;

    @Mock
    private DeviceRepository deviceRepository;

    private DeviceCommandService deviceCommandService;

    @BeforeEach
    void setUp() {
        deviceCommandService = new DeviceCommandService(deviceCommandRepository, deviceRepository);
    }

    @Test
    void createCommand_whenPendingExists_throwsIllegalStateException() {
        // given
        DeviceCommandCreateRequest request = createDeviceCommandRequest(
                "EDGE-TEST-001",
                DeviceCommandType.EMERGENCY_STOP,
                "test"
        );

        when(deviceCommandRepository.existsByDeviceIdAndStatus("EDGE-TEST-001", DeviceCommandStatus.PENDING))
                .thenReturn(true);

        // when / then
        assertThatThrownBy(() -> deviceCommandService.createCommand(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("EDGE-TEST-001");

        verify(deviceCommandRepository, never()).save(any());
    }

    @ParameterizedTest(name = "{0} completes -> device status becomes {1}")
    @CsvSource({
            "EMERGENCY_STOP, STOPPED",
            "RESUME_OPERATION, RUNNING",
            "ENTER_MAINTENANCE, MAINTENANCE",
            "EXIT_MAINTENANCE, RUNNING",
            "RESTART_DEVICE, RUNNING"
    })
    void reportCommandResult_completed_updatesDeviceStatus(
            DeviceCommandType commandType,
            DeviceStatus expectedStatus
    ) {
        // given
        DeviceCommand command = new DeviceCommand("EDGE-TEST-001", commandType, null);
        Device device = new Device("EDGE-TEST-001", "Test Zone");

        when(deviceCommandRepository.findById(1L)).thenReturn(Optional.of(command));
        when(deviceRepository.findByDeviceId("EDGE-TEST-001")).thenReturn(Optional.of(device));

        DeviceCommandResultRequest request = createDeviceCommandResultRequest(
                DeviceCommandStatus.COMPLETED,
                "done"
        );

        // when
        deviceCommandService.reportCommandResult(1L, request);

        // then
        assertThat(device.getStatus()).isEqualTo(expectedStatus);
    }

    @ParameterizedTest
    @EnumSource(value = DeviceCommandType.class, names = {"RESET_BIN", "UPDATE_THRESHOLD"})
    void reportCommandResult_completed_resetBinAndUpdateThreshold_doNotChangeDeviceStatus(
            DeviceCommandType commandType
    ) {
        // given
        DeviceCommand command = new DeviceCommand("EDGE-TEST-002", commandType, null);
        Device device = new Device("EDGE-TEST-002", "Test Zone");
        DeviceStatus statusBeforeCommand = device.getStatus();

        when(deviceCommandRepository.findById(2L)).thenReturn(Optional.of(command));
        when(deviceRepository.findByDeviceId("EDGE-TEST-002")).thenReturn(Optional.of(device));

        DeviceCommandResultRequest request = createDeviceCommandResultRequest(
                DeviceCommandStatus.COMPLETED,
                "done"
        );

        // when
        deviceCommandService.reportCommandResult(2L, request);

        // then
        assertThat(device.getStatus()).isEqualTo(statusBeforeCommand);
    }

    private static DeviceCommandCreateRequest createDeviceCommandRequest(
            String deviceId,
            DeviceCommandType commandType,
            String payload
    ) {
        DeviceCommandCreateRequest request = new DeviceCommandCreateRequest();
        setField(request, "deviceId", deviceId);
        setField(request, "commandType", commandType.name());
        setField(request, "payload", payload);
        return request;
    }

    private static DeviceCommandResultRequest createDeviceCommandResultRequest(
            DeviceCommandStatus status,
            String resultMessage
    ) {
        DeviceCommandResultRequest request = new DeviceCommandResultRequest();
        setField(request, "status", status.name());
        setField(request, "resultMessage", resultMessage);
        return request;
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
