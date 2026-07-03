package com.chanho.smartrecycler.sorting.service;

import com.chanho.smartrecycler.bin.service.BinService;
import com.chanho.smartrecycler.error.entity.ErrorSeverity;
import com.chanho.smartrecycler.error.entity.ErrorType;
import com.chanho.smartrecycler.error.service.ErrorEventService;
import com.chanho.smartrecycler.sorting.dto.SortingResultCreateRequest;
import com.chanho.smartrecycler.sorting.entity.SortingAction;
import com.chanho.smartrecycler.sorting.entity.SortingResult;
import com.chanho.smartrecycler.sorting.entity.SortingResultStatus;
import com.chanho.smartrecycler.sorting.repository.SortingResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SortingResultServiceTest {

    @Mock
    private SortingResultRepository sortingResultRepository;

    @Mock
    private BinService binService;

    @Mock
    private ErrorEventService errorEventService;

    private SortingResultService sortingResultService;

    @BeforeEach
    void setUp() {
        sortingResultService = new SortingResultService(
                sortingResultRepository,
                binService,
                errorEventService
        );

        when(sortingResultRepository.save(any(SortingResult.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createSortingResult_completed_increasesBinCount() {
        // given
        SortingResultCreateRequest request = createSortingResultRequest(
                1L,
                "EDGE-TEST-001",
                "plastic_bottle",
                "PLASTIC",
                SortingAction.MOVE_TO_PLASTIC_BIN,
                SortingResultStatus.COMPLETED,
                320,
                null
        );

        // when
        sortingResultService.createSortingResult(request);

        // then
        verify(binService).increaseBinCount("EDGE-TEST-001", "PLASTIC");
        verify(errorEventService, never())
                .createSystemErrorEvent(anyString(), any(ErrorType.class), any(ErrorSeverity.class), anyString());
    }

    @Test
    void createSortingResult_failed_createsSortingFailedErrorEvent() {
        // given
        SortingResultCreateRequest request = createSortingResultRequest(
                2L,
                "EDGE-TEST-002",
                "can",
                "CAN",
                SortingAction.MOVE_TO_CAN_BIN,
                SortingResultStatus.FAILED,
                900,
                "Virtual actuator jam detected."
        );

        // when
        sortingResultService.createSortingResult(request);

        // then
        verify(errorEventService).createSystemErrorEvent(
                "EDGE-TEST-002",
                ErrorType.SORTING_FAILED,
                ErrorSeverity.WARNING,
                "Virtual actuator jam detected."
        );
        verify(binService, never()).increaseBinCount(anyString(), anyString());
    }

    private static SortingResultCreateRequest createSortingResultRequest(
            Long classificationLogId,
            String deviceId,
            String label,
            String targetBin,
            SortingAction action,
            SortingResultStatus status,
            Integer actuatorTimeMs,
            String failureReason
    ) {
        SortingResultCreateRequest request = new SortingResultCreateRequest();
        setField(request, "classificationLogId", classificationLogId);
        setField(request, "deviceId", deviceId);
        setField(request, "label", label);
        setField(request, "targetBin", targetBin);
        setField(request, "action", action.name());
        setField(request, "status", status.name());
        setField(request, "actuatorTimeMs", actuatorTimeMs);
        setField(request, "failureReason", failureReason);
        return request;
    }

    private static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
