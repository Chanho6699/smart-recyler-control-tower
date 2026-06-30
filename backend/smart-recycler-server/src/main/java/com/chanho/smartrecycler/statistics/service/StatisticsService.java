package com.chanho.smartrecycler.statistics.service;

import com.chanho.smartrecycler.bin.entity.Bin;
import com.chanho.smartrecycler.bin.entity.BinType;
import com.chanho.smartrecycler.bin.repository.BinRepository;
import com.chanho.smartrecycler.classification.repository.ClassificationLogRepository;
import com.chanho.smartrecycler.device.entity.DeviceStatus;
import com.chanho.smartrecycler.device.repository.DeviceRepository;
import com.chanho.smartrecycler.error.entity.ErrorSeverity;
import com.chanho.smartrecycler.error.repository.ErrorEventRepository;
import com.chanho.smartrecycler.statistics.dto.SummaryStatisticsResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    private final DeviceRepository deviceRepository;
    private final ClassificationLogRepository classificationLogRepository;
    private final ErrorEventRepository errorEventRepository;
    private final BinRepository binRepository;

    public StatisticsService(
            DeviceRepository deviceRepository,
            ClassificationLogRepository classificationLogRepository,
            ErrorEventRepository errorEventRepository,
            BinRepository binRepository
    ) {
        this.deviceRepository = deviceRepository;
        this.classificationLogRepository = classificationLogRepository;
        this.errorEventRepository = errorEventRepository;
        this.binRepository = binRepository;
    }

    @Transactional(readOnly = true)
    public SummaryStatisticsResponse getSummary() {
        long totalDevices = deviceRepository.count();
        long runningDevices = deviceRepository.countByStatus(DeviceStatus.RUNNING);
        long offlineDevices = deviceRepository.countByStatus(DeviceStatus.OFFLINE);
        long errorDevices = deviceRepository.countByStatus(DeviceStatus.ERROR);
        long maintenanceDevices = deviceRepository.countByStatus(DeviceStatus.MAINTENANCE);

        long totalClassificationLogs = classificationLogRepository.count();

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        long todayClassificationLogs = classificationLogRepository.countByCreatedAtAfter(todayStart);

        long totalErrorEvents = errorEventRepository.count();
        long criticalErrorEvents = errorEventRepository.countBySeverity(ErrorSeverity.CRITICAL);

        List<Bin> bins = binRepository.findAll();

        Map<BinType, Integer> binTypeCounts = new EnumMap<>(BinType.class);

        for (BinType binType : BinType.values()) {
            binTypeCounts.put(binType, 0);
        }

        long totalItemsInBins = 0;

        for (Bin bin : bins) {
            int itemCount = bin.getItemCount();
            totalItemsInBins += itemCount;

            BinType binType = bin.getBinType();
            binTypeCounts.put(
                    binType,
                    binTypeCounts.getOrDefault(binType, 0) + itemCount
            );
        }

        Map<String, Integer> binItemCounts = new HashMap<>();

        for (Map.Entry<BinType, Integer> entry : binTypeCounts.entrySet()) {
            binItemCounts.put(entry.getKey().name(), entry.getValue());
        }

        return new SummaryStatisticsResponse(
                totalDevices,
                runningDevices,
                offlineDevices,
                errorDevices,
                maintenanceDevices,
                totalClassificationLogs,
                todayClassificationLogs,
                totalErrorEvents,
                criticalErrorEvents,
                totalItemsInBins,
                binItemCounts
        );
    }
}
