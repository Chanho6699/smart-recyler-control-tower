package com.chanho.smartrecycler.bin.controller;

import com.chanho.smartrecycler.bin.dto.BinResetRequest;
import com.chanho.smartrecycler.bin.dto.BinResponse;
import com.chanho.smartrecycler.bin.service.BinService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bins")
public class BinController {

    private final BinService binService;

    public BinController(BinService binService) {
        this.binService = binService;
    }

    @GetMapping
    public ResponseEntity<List<BinResponse>> getBins() {
        List<BinResponse> responses = binService.getBins();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/devices/{deviceId}")
    public ResponseEntity<List<BinResponse>> getBinsByDeviceId(
            @PathVariable String deviceId
    ) {
        List<BinResponse> responses = binService.getBinsByDeviceId(deviceId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/reset")
    public ResponseEntity<BinResponse> resetBin(
            @Valid @RequestBody BinResetRequest request
    ) {
        BinResponse response = binService.resetBin(request);
        return ResponseEntity.ok(response);
    }
}
