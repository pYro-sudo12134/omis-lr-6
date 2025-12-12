package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.enums.Language;

public class LanguageAverageAccuracyResponse {
    private final Language language;
    private final Double averageAccuracy;

    public LanguageAverageAccuracyResponse(Language language, Double averageAccuracy) {
        this.language = language;
        this.averageAccuracy = averageAccuracy;
    }

    public Language getLanguage() { return language; }
    public Double getAverageAccuracy() { return averageAccuracy; }
}