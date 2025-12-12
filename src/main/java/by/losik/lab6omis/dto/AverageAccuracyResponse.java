package by.losik.lab6omis.dto;

public class AverageAccuracyResponse {
    private final Double averageAccuracy;

    public AverageAccuracyResponse(Double averageAccuracy) {
        this.averageAccuracy = averageAccuracy;
    }

    public Double getAverageAccuracy() { return averageAccuracy; }
}