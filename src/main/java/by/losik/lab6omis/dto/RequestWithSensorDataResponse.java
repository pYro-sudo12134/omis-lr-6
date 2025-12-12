package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.types.Request;
import by.losik.lab6omis.entities.general.types.SensorData;

import java.util.List;

/**
 * DTO класс для ответа с запросом и данными сенсоров.
 * Используется для структурированных REST ответов при получении запроса с связанными данными сенсоров.
 */
public class RequestWithSensorDataResponse {
    private final Request request;
    private final List<SensorData> sensorDataList;

    /**
     * Создает ответ с запросом и связанными данными сенсоров.
     *
     * @param request объект запроса
     * @param sensorDataList список данных сенсоров, связанных с запросом
     */
    public RequestWithSensorDataResponse(Request request, List<SensorData> sensorDataList) {
        this.request = request;
        this.sensorDataList = sensorDataList;
    }

    /**
     * Возвращает объект запроса.
     *
     * @return объект запроса
     */
    public Request getRequest() {
        return request;
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