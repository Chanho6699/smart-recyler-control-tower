package com.chanho.smartrecycler.sorting.service;

import com.chanho.smartrecycler.bin.service.BinService;
import com.chanho.smartrecycler.error.entity.ErrorSeverity;
import com.chanho.smartrecycler.error.entity.ErrorType;
import com.chanho.smartrecycler.error.service.ErrorEventService;
import com.chanho.smartrecycler.sorting.dto.SortingResultCreateRequest;
import com.chanho.smartrecycler.sorting.dto.SortingResultResponse;
import com.chanho.smartrecycler.sorting.entity.SortingAction;
import com.chanho.smartrecycler.sorting.entity.SortingResult;
import com.chanho.smartrecycler.sorting.entity.SortingResultStatus;
import com.chanho.smartrecycler.sorting.repository.SortingResultRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SortingResultService {

    private final SortingResultRepository sortingResultRepository;
    private final BinService binService;
    private final ErrorEventService errorEventService;

    public SortingResultService(
            SortingResultRepository sortingResultRepository,
            BinService binService,
            ErrorEventService errorEventService
    ) {
        this.sortingResultRepository = sortingResultRepository;
        this.binService = binService;
        this.errorEventService = errorEventService;
    }

    @Transactional
    public SortingResultResponse createSortingResult(SortingResultCreateRequest request) {
        SortingResultStatus status = parseStatus(request.getStatus());
        SortingAction action = parseAction(request.getAction(), request.getTargetBin());

        SortingResult sortingResult = new SortingResult(
                request.getClassificationLogId(),
                request.getDeviceId(),
                request.getLabel(),
                request.getTargetBin(),
                action,
                status,
                request.getActuatorTimeMs(),
                request.getFailureReason()
        );

        SortingResult savedResult = sortingResultRepository.save(sortingResult);

        if (status == SortingResultStatus.COMPLETED) {
            binService.increaseBinCount(
                    request.getDeviceId(),
                    request.getTargetBin()
            );
        }

        if (status == SortingResultStatus.FAILED) {
            String message = buildFailureMessage(request);

            errorEventService.createSystemErrorEvent(
                    request.getDeviceId(),
                    ErrorType.SORTING_FAILED,
                    ErrorSeverity.WARNING,
                    message
            );
        }

        return new SortingResultResponse(savedResult);
    }

    @Transactional(readOnly = true)
    public List<SortingResultResponse> getSortingResults() {
        return sortingResultRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(SortingResultResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SortingResultResponse> getSortingResultsByDeviceId(String deviceId) {
        return sortingResultRepository.findAllByDeviceIdOrderByCreatedAtDesc(deviceId)
                .stream()
                .map(SortingResultResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SortingResultResponse> getSortingResultsByStatus(String statusValue) {
        SortingResultStatus status = parseStatus(statusValue);

        return sortingResultRepository.findAllByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(SortingResultResponse::new)
                .toList();
    }

    private SortingResultStatus parseStatus(String value) {
        try {
            return SortingResultStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid sorting result status: " + value);
        }
    }

    private SortingAction parseAction(String actionValue, String targetBin) {
        if (actionValue != null && !actionValue.isBlank()) {
            try {
                return SortingAction.valueOf(actionValue.toUpperCase());
            } catch (IllegalArgumentException ignored) {
                return resolveActionByTargetBin(targetBin);
            }
        }

        return resolveActionByTargetBin(targetBin);
    }

    private SortingAction resolveActionByTargetBin(String targetBin) {
        if (targetBin == null) {
            return SortingAction.MOVE_TO_UNKNOWN_BIN;
        }

        return switch (targetBin.toUpperCase()) {
            case "PLASTIC" -> SortingAction.MOVE_TO_PLASTIC_BIN;
            case "PAPER" -> SortingAction.MOVE_TO_PAPER_BIN;
            case "CAN" -> SortingAction.MOVE_TO_CAN_BIN;
            default -> SortingAction.MOVE_TO_UNKNOWN_BIN;
        };
    }

    private String buildFailureMessage(SortingResultCreateRequest request) {
        if (request.getFailureReason() != null && !request.getFailureReason().isBlank()) {
            return request.getFailureReason();
        }

        return "Sorting actuator failed. classificationLogId=" + request.getClassificationLogId();
    }
}
