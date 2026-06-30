package com.chanho.smartrecycler.classification.service;

import com.chanho.smartrecycler.bin.service.BinService;
import com.chanho.smartrecycler.classification.dto.ClassificationLogCreateRequest;
import com.chanho.smartrecycler.classification.dto.ClassificationLogResponse;
import com.chanho.smartrecycler.classification.entity.ClassificationLog;
import com.chanho.smartrecycler.classification.repository.ClassificationLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClassificationLogService {

    private final ClassificationLogRepository classificationLogRepository;
    private final BinService binService;

    public ClassificationLogService(
            ClassificationLogRepository classificationLogRepository,
            BinService binService
    ) {
        this.classificationLogRepository = classificationLogRepository;
        this.binService = binService;
    }

    @Transactional
    public ClassificationLogResponse createLog(ClassificationLogCreateRequest request) {
        ClassificationLog log = new ClassificationLog(
                request.getDeviceId(),
                request.getLabel(),
                request.getConfidence(),
                request.getTargetBin(),
                request.getInferenceTimeMs(),
                request.getRuntimeType()
        );

        ClassificationLog savedLog = classificationLogRepository.save(log);

        binService.increaseBinCount(
                request.getDeviceId(),
                request.getTargetBin()
        );

        return new ClassificationLogResponse(savedLog);
    }

    @Transactional(readOnly = true)
    public List<ClassificationLogResponse> getLogs() {
        return classificationLogRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(ClassificationLogResponse::new)
                .toList();
    }
}
