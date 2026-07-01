package com.chanho.smartrecycler.classification.service;

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

    public ClassificationLogService(
            ClassificationLogRepository classificationLogRepository
    ) {
        this.classificationLogRepository = classificationLogRepository;
    }

    @Transactional
    public ClassificationLogResponse createLog(ClassificationLogCreateRequest request) {
        ClassificationLog classificationLog = new ClassificationLog(
                request.getDeviceId(),
                request.getLabel(),
                request.getConfidence(),
                request.getTargetBin(),
                request.getInferenceTimeMs(),
                request.getRuntimeType()
        );

        ClassificationLog savedLog = classificationLogRepository.save(classificationLog);

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
