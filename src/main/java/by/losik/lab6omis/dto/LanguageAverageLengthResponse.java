package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.enums.Language;

/**
 * DTO класс для ответа со средней длиной сообщений по языку.
 * Используется для структурированных REST ответов при расчете средней длины сообщений для конкретного языка.
 */
public class LanguageAverageLengthResponse {
    private final Language language;
    private final Double averageLength;
    private final String formattedLength;

    /**
     * Создает ответ со средней длиной сообщений для указанного языка.
     *
     * @param language язык, для которого рассчитана длина
     * @param averageLength средняя длина сообщений (может быть null, если данные отсутствуют)
     */
    public LanguageAverageLengthResponse(Language language, Double averageLength) {
        this.language = language;
        this.averageLength = averageLength;
        this.formattedLength = averageLength != null ?
                String.format("%.2f символов", averageLength) : "N/A";
    }

    /**
     * Возвращает язык, для которого рассчитана длина.
     *
     * @return язык
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Возвращает среднюю длину сообщений для языка.
     *
     * @return средняя длина или null, если данные отсутствуют
     */
    public Double getAverageLength() {
        return averageLength;
    }

    /**
     * Возвращает отформатированное представление средней длины.
     *
     * @return отформатированная строка со средней длиной или "N/A", если данные отсутствуют
     */
    public String getFormattedLength() {
        return formattedLength;
    }
}