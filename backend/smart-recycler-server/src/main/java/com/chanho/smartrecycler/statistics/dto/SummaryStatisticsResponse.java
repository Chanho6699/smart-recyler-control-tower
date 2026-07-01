package com.chanho.smartrecycler.statistics.dto;

import java.util.Map;

public class SummaryStatisticsResponse {

    private long totalDevices;
    private long runningDevices;
    private long offlineDevices;
    private long errorDevices;
    private long maintenanceDevices;
    private long stoppedDevices;

    private long totalClassificationLogs;
    private long todayClassificationLogs;

    private long totalSortingResults;
    private long completedSortingResults;
    private long failedSortingResults;
    private double sortingSuccessRate;

    private long totalErrorEvents;
    private long criticalErrorEvents;
    private long openErrorEvents;
    private long resolvedErrorEvents;

    private long totalItemsInBins;

    private Map<String, Integer> binItemCounts;

    public SummaryStatisticsResponse(
            long totalDevices,
            long runningDevices,
            long offlineDevices,
            long errorDevices,
            long maintenanceDevices,
            long stoppedDevices,
            long totalClassificationLogs,
            long todayClassificationLogs,
            long totalSortingResults,
            long completedSortingResults,
            long failedSortingResults,
            double sortingSuccessRate,
            long totalErrorEvents,
            long criticalErrorEvents,
            long openErrorEvents,
            long resolvedErrorEvents,
            long totalItemsInBins,
            Map<String, Integer> binItemCounts
    ) {
        this.totalDevices = totalDevices;
        this.runningDevices = runningDevices;
        this.offlineDevices = offlineDevices;
        this.errorDevices = errorDevices;
        this.maintenanceDevices = maintenanceDevices;
        this.stoppedDevices = stoppedDevices;
        this.totalClassificationLogs = totalClassificationLogs;
        this.todayClassificationLogs = todayClassificationLogs;
        this.totalSortingResults = totalSortingResults;
        this.completedSortingResults = completedSortingResults;
        this.failedSortingResults = failedSortingResults;
        this.sortingSuccessRate = sortingSuccessRate;
        this.totalErrorEvents = totalErrorEvents;
        this.criticalErrorEvents = criticalErrorEvents;
        this.openErrorEvents = openErrorEvents;
        this.resolvedErrorEvents = resolvedErrorEvents;
        this.totalItemsInBins = totalItemsInBins;
        this.binItemCounts = binItemCounts;
    }

    public long getTotalDevices() {
        return totalDevices;
    }

    public long getRunningDevices() {
        return runningDevices;
    }

    public long getOfflineDevices() {
        return offlineDevices;
    }

    public long getErrorDevices() {
        return errorDevices;
    }

    public long getMaintenanceDevices() {
        return maintenanceDevices;
    }

    public long getStoppedDevices() {
        return stoppedDevices;
    }

    public long getTotalClassificationLogs() {
        return totalClassificationLogs;
    }

    public long getTodayClassificationLogs() {
        return todayClassificationLogs;
    }

    public long getTotalSortingResults() {
        return totalSortingResults;
    }

    public long getCompletedSortingResults() {
        return completedSortingResults;
    }

    public long getFailedSortingResults() {
        return failedSortingResults;
    }

    public double getSortingSuccessRate() {
        return sortingSuccessRate;
    }

    public long getTotalErrorEvents() {
        return totalErrorEvents;
    }

    public long getCriticalErrorEvents() {
        return criticalErrorEvents;
    }

    public long getOpenErrorEvents() {
        return openErrorEvents;
    }

    public long getResolvedErrorEvents() {
        return resolvedErrorEvents;
    }

    public long getTotalItemsInBins() {
        return totalItemsInBins;
    }

    public Map<String, Integer> getBinItemCounts() {
        return binItemCounts;
    }
}
