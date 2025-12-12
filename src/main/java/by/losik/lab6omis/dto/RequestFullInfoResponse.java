package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.types.Request;
import by.losik.lab6omis.entities.general.types.SensorData;
import by.losik.lab6omis.entities.general.types.Sound;

import java.util.List;

/**
 * DTO класс для полной информации о запросе.
 * Используется для структурированных REST ответов при получении запроса со всеми связанными данными.
 */
public class RequestFullInfoResponse {
    private final Request request;
    private final List<Sound> sounds;
    private final List<SensorData> sensorDataList;

    /**
     * Создает ответ с полной информацией о запросе.
     *
     * @param request основной объект запроса
     * @param sounds список звуков, связанных с запросом
     * @param sensorDataList список данных сенсоров, связанных с запросом
     */
    public RequestFullInfoResponse(Request request, List<Sound> sounds, List<SensorData> sensorDataList) {
        this.request = request;
        this.sounds = sounds;
        this.sensorDataList = sensorDataList;
    }

    /**
     * Возвращает основной объект запроса.
     *
     * @return объект запроса
     */
    public Request getRequest() {
        return request;
    }

    /**
     * Возвращает список звуков, связанных с запросом.
     *
     * @return список звуков
     */
    public List<Sound> getSounds() {
        return sounds;
    }

    /**
     * Возвращает список данных сенсоров, связанных с запросом.
     *
     * @return список данных сенсоров
     */
    public List<SensorData> getSensorDataList() {
        return sensorDataList;
    }
}