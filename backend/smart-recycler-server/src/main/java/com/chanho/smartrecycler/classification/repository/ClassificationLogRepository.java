package com.chanho.smartrecycler.classification.repository;

import com.chanho.smartrecycler.classification.entity.ClassificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassificationLogRepository extends JpaRepository<ClassificationLog, Long> {

    List<ClassificationLog> findAllByOrderByCreatedAtDesc();
}
