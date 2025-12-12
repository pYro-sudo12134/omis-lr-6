package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.enums.Language;

import java.util.Map;

public class ComprehensiveStatsResponse {
    private final long totalRequests;
    private final long totalSounds;
    private final long totalSensorData;
    private final Double averageAccuracy;
    private final Double averageFrequency;
    private final Map<Language, Long> requestsByLanguage;
    private final Map<String, Double> avgFrequencyByNoise;
    private final Map<Long, Long> dataCountBySensor;

    public ComprehensiveStatsResponse(
            long totalRequests, long totalSounds, long totalSensorData,
            Double averageAccuracy, Double averageFrequency,
            Map<Language, Long> requestsByLanguage,
            Map<String, Double> avgFrequencyByNoise,
            Map<Long, Long> dataCountBySensor) {

        this.totalRequests = totalRequests;
        this.totalSounds = totalSounds;
        this.totalSensorData = totalSensorData;
        this.averageAccuracy = averageAccuracy;
        this.averageFrequency = averageFrequency;
        this.requestsByLanguage = requestsByLanguage;
        this.avgFrequencyByNoise = avgFrequencyByNoise;
        this.dataCountBySensor = dataCountBySensor;
    }

    public long getTotalRequests() { return totalRequests; }
    public long getTotalSounds() { return totalSounds; }
    public long getTotalSensorData() { return totalSensorData; }
    public Double getAverageAccuracy() { return averageAccuracy; }
    public Double getAverageFrequency() { return averageFrequency; }
    public Map<Language, Long> getRequestsByLanguage() { return requestsByLanguage; }
    public Map<String, Double> getAvgFrequencyByNoise() { return avgFrequencyByNoise; }
    public Map<Long, Long> getDataCountBySensor() { return dataCountBySensor; }
}