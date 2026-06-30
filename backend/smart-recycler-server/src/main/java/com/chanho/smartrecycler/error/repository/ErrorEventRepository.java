package com.chanho.smartrecycler.error.repository;

import com.chanho.smartrecycler.error.entity.ErrorEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ErrorEventRepository extends JpaRepository<ErrorEvent, Long> {

    List<ErrorEvent> findAllByOrderByCreatedAtDesc();

    List<ErrorEvent> findAllByDeviceIdOrderByCreatedAtDesc(String deviceId);
}
