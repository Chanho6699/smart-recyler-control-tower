package com.chanho.smartrecycler.sorting.repository;

import com.chanho.smartrecycler.sorting.entity.SortingResult;
import com.chanho.smartrecycler.sorting.entity.SortingResultStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SortingResultRepository extends JpaRepository<SortingResult, Long> {

    List<SortingResult> findAllByOrderByCreatedAtDesc();

    List<SortingResult> findAllByDeviceIdOrderByCreatedAtDesc(String deviceId);

    List<SortingResult> findAllByStatusOrderByCreatedAtDesc(SortingResultStatus status);

    long countByStatus(SortingResultStatus status);
}
