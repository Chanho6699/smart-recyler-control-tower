package com.chanho.smartrecycler.classification.controller;

import com.chanho.smartrecycler.classification.dto.ClassificationLogCreateRequest;
import com.chanho.smartrecycler.classification.dto.ClassificationLogResponse;
import com.chanho.smartrecycler.classification.service.ClassificationLogService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/classification-logs")
public class ClassificationLogController {

    private final ClassificationLogService classificationLogService;

    public ClassificationLogController(ClassificationLogService classificationLogService) {
        this.classificationLogService = classificationLogService;
    }

    @PostMapping
    public ResponseEntity<ClassificationLogResponse> createLog(
            @Valid @RequestBody ClassificationLogCreateRequest request
    ) {
        ClassificationLogResponse response = classificationLogService.createLog(request);
        return ResponseEntity
                .created(URI.create("/api/classification-logs/" + response.getId()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ClassificationLogResponse>> getLogs() {
        List<ClassificationLogResponse> responses = classificationLogService.getLogs();
        return ResponseEntity.ok(responses);
    }
}
