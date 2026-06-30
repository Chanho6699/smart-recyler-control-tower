package com.chanho.smartrecycler.error.service;

import com.chanho.smartrecycler.error.dto.ErrorEventCreateRequest;
import com.chanho.smartrecycler.error.dto.ErrorEventResponse;
import com.chanho.smartrecycler.error.entity.ErrorEvent;
import com.chanho.smartrecycler.error.entity.ErrorSeverity;
import com.chanho.smartrecycler.error.entity.ErrorType;
import com.chanho.smartrecycler.error.repository.ErrorEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ErrorEventService {

    private final ErrorEventRepository errorEventRepository;

    public ErrorEventService(ErrorEventRepository errorEventRepository) {
        this.errorEventRepository = errorEventRepository;
    }

    @Transactional
    public ErrorEventResponse createErrorEvent(ErrorEventCreateRequest request) {
        ErrorType errorType = parseErrorType(request.getErrorType());
        ErrorSeverity severity = parseSeverity(request.getSeverity());

        ErrorEvent errorEvent = new ErrorEvent(
                request.getDeviceId(),
                errorType,
                severity,
                request.getMessage()
        );

        ErrorEvent savedEvent = errorEventRepository.save(errorEvent);
        return new ErrorEventResponse(savedEvent);
    }

    @Transactional
    public ErrorEventResponse resolveErrorEvent(Long id) {
        ErrorEvent errorEvent = errorEventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Error event not found. id=" + id));

        errorEvent.resolve();

        return new ErrorEventResponse(errorEvent);
    }

    @Transactional(readOnly = true)
    public List<ErrorEventResponse> getErrorEvents() {
        return errorEventRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(ErrorEventResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ErrorEventResponse> getErrorEventsByDeviceId(String deviceId) {
        return errorEventRepository.findAllByDeviceIdOrderByCreatedAtDesc(deviceId)
                .stream()
                .map(ErrorEventResponse::new)
                .toList();
    }

    private ErrorType parseErrorType(String value) {
        try {
            return ErrorType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return ErrorType.UNKNOWN_ERROR;
        }
    }

    private ErrorSeverity parseSeverity(String value) {
        try {
            return ErrorSeverity.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return ErrorSeverity.WARNING;
        }
    }
}
