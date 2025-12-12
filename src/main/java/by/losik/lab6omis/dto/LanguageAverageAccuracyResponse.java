package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.enums.Language;

/**
 * DTO класс для ответа со средней точностью по языку.
 * Используется для структурированных REST ответов при расчете средней точности для конкретного языка.
 */
public class LanguageAverageAccuracyResponse {
    private final Language language;
    private final Double averageAccuracy;

    /**
     * Создает ответ со средней точностью для указанного языка.
     *
     * @param language язык, для которого рассчитана точность
     * @param averageAccuracy средняя точность (может быть null, если данные отсутствуют)
     */
    public LanguageAverageAccuracyResponse(Language language, Double averageAccuracy) {
        this.language = language;
        this.averageAccuracy = averageAccuracy;
    }

    /**
     * Возвращает язык, для которого рассчитана точность.
     *
     * @return язык
     */
    public Language getLanguage() {
        return language;
    }

    /**
     * Возвращает среднюю точность для языка.
     *
     * @return средняя точность или null, если данные отсутствуют
     */
    public Double getAverageAccuracy() {
        return averageAccuracy;
    }
}