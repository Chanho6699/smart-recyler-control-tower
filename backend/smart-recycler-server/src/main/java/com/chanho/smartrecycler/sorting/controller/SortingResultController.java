package com.chanho.smartrecycler.sorting.controller;

import com.chanho.smartrecycler.sorting.dto.SortingResultCreateRequest;
import com.chanho.smartrecycler.sorting.dto.SortingResultResponse;
import com.chanho.smartrecycler.sorting.service.SortingResultService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/sorting-results")
public class SortingResultController {

    private final SortingResultService sortingResultService;

    public SortingResultController(SortingResultService sortingResultService) {
        this.sortingResultService = sortingResultService;
    }

    @PostMapping
    public ResponseEntity<SortingResultResponse> createSortingResult(
            @Valid @RequestBody SortingResultCreateRequest request
    ) {
        SortingResultResponse response = sortingResultService.createSortingResult(request);

        return ResponseEntity
                .created(URI.create("/api/sorting-results/" + response.getId()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<SortingResultResponse>> getSortingResults() {
        return ResponseEntity.ok(sortingResultService.getSortingResults());
    }

    @GetMapping("/devices/{deviceId}")
    public ResponseEntity<List<SortingResultResponse>> getSortingResultsByDeviceId(
            @PathVariable String deviceId
    ) {
        return ResponseEntity.ok(sortingResultService.getSortingResultsByDeviceId(deviceId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<SortingResultResponse>> getSortingResultsByStatus(
            @PathVariable String status
    ) {
        return ResponseEntity.ok(sortingResultService.getSortingResultsByStatus(status));
    }
}
