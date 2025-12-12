package by.losik.lab6omis.dto;

/**
 * DTO класс для ответа со средней точностью.
 * Используется для структурированных REST ответов при расчете средней точности.
 */
public class AverageAccuracyResponse {
    private final Double averageAccuracy;

    /**
     * Создает ответ со средней точностью.
     *
     * @param averageAccuracy средняя точность (может быть null, если данные отсутствуют)
     */
    public AverageAccuracyResponse(Double averageAccuracy) {
        this.averageAccuracy = averageAccuracy;
    }

    /**
     * Возвращает среднюю точность.
     *
     * @return средняя точность или null, если данные отсутствуют
     */
    public Double getAverageAccuracy() {
        return averageAccuracy;
    }
}