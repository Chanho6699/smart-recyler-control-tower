package com.chanho.smartrecycler.classification.service;

import com.chanho.smartrecycler.classification.dto.ClassificationLogCreateRequest;
import com.chanho.smartrecycler.classification.entity.ClassificationLog;
import com.chanho.smartrecycler.classification.repository.ClassificationLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * ClassificationLogService는 BinService/ErrorEventService 의존성을 전혀 갖지 않는다.
 * 즉 classification log 생성은 구조적으로 bin count나 error event에 영향을 줄 수 없다.
 * (COMPLETED/FAILED sorting result에 따라 bin count가 실제로 언제 늘어나는지는
 *  SortingResultServiceTest에서 별도로 검증한다.)
 */
@ExtendWith(MockitoExtension.class)
class ClassificationLogServiceTest {

    @Mock
    private ClassificationLogRepository classificationLogRepository;

    private ClassificationLogService classificationLogService;

    @BeforeEach
    void setUp() {
        classificationLogService = new ClassificationLogService(classificationLogRepository);
    }

    @Test
    void createClassificationLog_savesRecognitionResultOnly() {
        // given
        ClassificationLogCreateRequest request = createClassificationLogRequest(
                "EDGE-TEST-001",
                "plastic_bottle",
                0.92,
                "PLASTIC",
                45L,
                "FAKE_AI"
        );

        when(classificationLogRepository.save(any(ClassificationLog.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        classificationLogService.createLog(request);

        // then: AI 인식 결과만 저장됨 (bin/error 관련 협력 객체가 아예 없음)
        ArgumentCaptor<ClassificationLog> captor = ArgumentCaptor.forClass(ClassificationLog.class);
        verify(classificationLogRepository).save(captor.capture());

        ClassificationLog savedLog = captor.getValue();
        assertThat(savedLog.getDeviceId()).isEqualTo("EDGE-TEST-001");
        assertThat(savedLog.getLabel()).isEqualTo("plastic_bottle");
        assertThat(savedLog.getTargetBin()).isEqualTo("PLASTIC");
    }

    private static ClassificationLogCreateRequest createClassificationLogRequest(
            String deviceId,
            String label,
            double confidence,
            String targetBin,
            long inferenceTimeMs,
            String runtimeType
    ) {
        ClassificationLogCreateRequest request = new ClassificationLogCreateRequest();
        setField(request, "deviceId", deviceId);
        setField(request, "label", label);
        setField(request, "confidence", confidence);
        setField(request, "targetBin", targetBin);
        setField(request, "inferenceTimeMs", inferenceTimeMs);
        setField(request, "runtimeType", runtimeType);
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
