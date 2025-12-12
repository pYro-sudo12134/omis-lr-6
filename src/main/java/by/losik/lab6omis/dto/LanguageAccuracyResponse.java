package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.enums.Language;

/**
 * Ответ с информацией о точности по языку.
 */
public class LanguageAccuracyResponse {
    private final Language language;
    private final Double averageAccuracy;
    private final String formattedAccuracy;

    public LanguageAccuracyResponse(Language language, Double averageAccuracy) {
        this.language = language;
        this.averageAccuracy = averageAccuracy;
        this.formattedAccuracy = averageAccuracy != null ?
                String.format("%.2f%%", averageAccuracy) : "N/A";
    }

    public Language getLanguage() {
        return language;
    }

    public Double getAverageAccuracy() {
        return averageAccuracy;
    }

    public String getFormattedAccuracy() {
        return formattedAccuracy;
    }
}