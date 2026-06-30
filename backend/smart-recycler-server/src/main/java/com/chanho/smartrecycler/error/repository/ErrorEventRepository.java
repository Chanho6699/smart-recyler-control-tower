package com.chanho.smartrecycler.error.repository;

import com.chanho.smartrecycler.error.entity.ErrorEvent;
import com.chanho.smartrecycler.error.entity.ErrorEventStatus;
import com.chanho.smartrecycler.error.entity.ErrorSeverity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ErrorEventRepository extends JpaRepository<ErrorEvent, Long> {

    List<ErrorEvent> findAllByOrderByCreatedAtDesc();

    List<ErrorEvent> findAllByDeviceIdOrderByCreatedAtDesc(String deviceId);

    long countBySeverity(ErrorSeverity severity);

    long countByEventStatus(ErrorEventStatus eventStatus);
}
