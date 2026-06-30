package com.chanho.smartrecycler.device.controller;

import com.chanho.smartrecycler.device.dto.DeviceRegisterRequest;
import com.chanho.smartrecycler.device.dto.DeviceResponse;
import com.chanho.smartrecycler.device.dto.HeartbeatRequest;
import com.chanho.smartrecycler.device.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping("/register")
    public ResponseEntity<DeviceResponse> register(
            @Valid @RequestBody DeviceRegisterRequest request
    ) {
        DeviceResponse response = deviceService.register(request);
        return ResponseEntity
                .created(URI.create("/api/devices/" + response.getId()))
                .body(response);
    }

    @PostMapping("/heartbeat")
    public ResponseEntity<DeviceResponse> heartbeat(
            @Valid @RequestBody HeartbeatRequest request
    ) {
        DeviceResponse response = deviceService.heartbeat(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<DeviceResponse>> getDevices() {
        List<DeviceResponse> responses = deviceService.getDevices();
        return ResponseEntity.ok(responses);
    }
}
