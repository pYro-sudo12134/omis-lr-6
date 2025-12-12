package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.enums.Language;
import java.util.Map;

/**
 * DTO класс для комплексной статистики.
 * Используется для структурированных REST ответов при получении агрегированной статистики.
 */
public class ComprehensiveStatsResponse {
    private final long totalRequests;
    private final long totalSounds;
    private final long totalSensorData;
    private final Double averageAccuracy;
    private final Double averageFrequency;
    private final Map<Language, Long> requestsByLanguage;
    private final Map<String, Double> avgFrequencyByNoise;
    private final Map<Long, Long> dataCountBySensor;

    /**
     * Создает объект комплексной статистики.
     *
     * @param totalRequests общее количество запросов
     * @param totalSounds общее количество звуков
     * @param totalSensorData общее количество данных сенсоров
     * @param averageAccuracy средняя точность (может быть null)
     * @param averageFrequency средняя частота (может быть null)
     * @param requestsByLanguage распределение запросов по языкам
     * @param avgFrequencyByNoise средняя частота по типам шума
     * @param dataCountBySensor количество данных по сенсорам
     */
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

    /**
     * Возвращает общее количество запросов.
     *
     * @return общее количество запросов
     */
    public long getTotalRequests() {
        return totalRequests;
    }

    /**
     * Возвращает общее количество звуков.
     *
     * @return общее количество звуков
     */
    public long getTotalSounds() {
        return totalSounds;
    }

    /**
     * Возвращает общее количество данных сенсоров.
     *
     * @return общее количество данных сенсоров
     */
    public long getTotalSensorData() {
        return totalSensorData;
    }

    /**
     * Возвращает среднюю точность.
     *
     * @return средняя точность или null, если данные отсутствуют
     */
    public Double getAverageAccuracy() {
        return averageAccuracy;
    }

    /**
     * Возвращает среднюю частоту.
     *
     * @return средняя частота или null, если данные отсутствуют
     */
    public Double getAverageFrequency() {
        return averageFrequency;
    }

    /**
     * Возвращает распределение запросов по языкам.
     *
     * @return карта "язык → количество запросов"
     */
    public Map<Language, Long> getRequestsByLanguage() {
        return requestsByLanguage;
    }

    /**
     * Возвращает среднюю частоту по типам шума.
     *
     * @return карта "тип шума → средняя частота"
     */
    public Map<String, Double> getAvgFrequencyByNoise() {
        return avgFrequencyByNoise;
    }

    /**
     * Возвращает количество данных по сенсорам.
     *
     * @return карта "ID сенсора → количество данных"
     */
    public Map<Long, Long> getDataCountBySensor() {
        return dataCountBySensor;
    }
}