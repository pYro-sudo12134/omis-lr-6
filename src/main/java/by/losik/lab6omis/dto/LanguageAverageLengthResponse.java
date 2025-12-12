package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.enums.Language;

/**
 * DTO класс для ответа со средней длиной сообщений по языку.
 */
public class LanguageAverageLengthResponse {
    private final Language language;
    private final Double averageLength;
    private final String formattedLength;

    public LanguageAverageLengthResponse(Language language, Double averageLength) {
        this.language = language;
        this.averageLength = averageLength;
        this.formattedLength = averageLength != null ?
                String.format("%.2f символов", averageLength) : "N/A";
    }

    public Language getLanguage() {
        return language;
    }

    public Double getAverageLength() {
        return averageLength;
    }

    public String getFormattedLength() {
        return formattedLength;
    }
}