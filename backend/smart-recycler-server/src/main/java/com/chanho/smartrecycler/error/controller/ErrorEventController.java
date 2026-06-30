package com.chanho.smartrecycler.error.controller;

import com.chanho.smartrecycler.error.dto.ErrorEventCreateRequest;
import com.chanho.smartrecycler.error.dto.ErrorEventResponse;
import com.chanho.smartrecycler.error.service.ErrorEventService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/error-events")
public class ErrorEventController {

    private final ErrorEventService errorEventService;

    public ErrorEventController(ErrorEventService errorEventService) {
        this.errorEventService = errorEventService;
    }

    @PostMapping
    public ResponseEntity<ErrorEventResponse> createErrorEvent(
            @Valid @RequestBody ErrorEventCreateRequest request
    ) {
        ErrorEventResponse response = errorEventService.createErrorEvent(request);
        return ResponseEntity
                .created(URI.create("/api/error-events/" + response.getId()))
                .body(response);
    }

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<ErrorEventResponse> resolveErrorEvent(
            @PathVariable Long id
    ) {
        ErrorEventResponse response = errorEventService.resolveErrorEvent(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ErrorEventResponse>> getErrorEvents() {
        List<ErrorEventResponse> responses = errorEventService.getErrorEvents();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/devices/{deviceId}")
    public ResponseEntity<List<ErrorEventResponse>> getErrorEventsByDeviceId(
            @PathVariable String deviceId
    ) {
        List<ErrorEventResponse> responses = errorEventService.getErrorEventsByDeviceId(deviceId);
        return ResponseEntity.ok(responses);
    }
}
