package com.chanho.smartrecycler.devicecommand.controller;

import com.chanho.smartrecycler.devicecommand.dto.DeviceCommandCreateRequest;
import com.chanho.smartrecycler.devicecommand.dto.DeviceCommandResponse;
import com.chanho.smartrecycler.devicecommand.dto.DeviceCommandResultRequest;
import com.chanho.smartrecycler.devicecommand.service.DeviceCommandService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/device-commands")
public class DeviceCommandController {

    private final DeviceCommandService deviceCommandService;

    public DeviceCommandController(DeviceCommandService deviceCommandService) {
        this.deviceCommandService = deviceCommandService;
    }

    @PostMapping
    public ResponseEntity<DeviceCommandResponse> createCommand(
            @Valid @RequestBody DeviceCommandCreateRequest request
    ) {
        DeviceCommandResponse response = deviceCommandService.createCommand(request);

        return ResponseEntity
                .created(URI.create("/api/device-commands/" + response.getId()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<DeviceCommandResponse>> getCommands() {
        return ResponseEntity.ok(deviceCommandService.getCommands());
    }

    @GetMapping("/devices/{deviceId}")
    public ResponseEntity<List<DeviceCommandResponse>> getCommandsByDeviceId(
            @PathVariable String deviceId
    ) {
        return ResponseEntity.ok(deviceCommandService.getCommandsByDeviceId(deviceId));
    }

    @GetMapping("/devices/{deviceId}/pending")
    public ResponseEntity<DeviceCommandResponse> getPendingCommand(
            @PathVariable String deviceId
    ) {
        Optional<DeviceCommandResponse> response = deviceCommandService.getPendingCommand(deviceId);

        return response
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PatchMapping("/{commandId}/result")
    public ResponseEntity<DeviceCommandResponse> reportCommandResult(
            @PathVariable Long commandId,
            @Valid @RequestBody DeviceCommandResultRequest request
    ) {
        return ResponseEntity.ok(
                deviceCommandService.reportCommandResult(commandId, request)
        );
    }
}
