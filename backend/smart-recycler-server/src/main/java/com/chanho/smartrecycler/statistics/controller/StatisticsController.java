package com.chanho.smartrecycler.statistics.controller;

import com.chanho.smartrecycler.statistics.dto.SummaryStatisticsResponse;
import com.chanho.smartrecycler.statistics.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/summary")
    public ResponseEntity<SummaryStatisticsResponse> getSummary() {
        SummaryStatisticsResponse response = statisticsService.getSummary();
        return ResponseEntity.ok(response);
    }
}
