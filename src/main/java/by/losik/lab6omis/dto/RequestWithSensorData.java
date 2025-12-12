package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.types.Request;
import by.losik.lab6omis.entities.general.types.SensorData;

import java.util.List;

/**
 * DTO класс для запроса с данными сенсоров.
 * Используется для передачи запроса с связанными данными сенсоров.
 */
public class RequestWithSensorData {
    private Request request;
    private List<SensorData> sensorDataList;

    /**
     * Возвращает объект запроса.
     *
     * @return объект запроса
     */
    public Request getRequest() {
        return request;
    }

    /**
     * Устанавливает объект запроса.
     *
     * @param request объект запроса
     */
    public void setRequest(Request request) {
        this.request = request;
    }

    /**
     * Возвращает список данных сенсоров, связанных с запросом.
     *
     * @return список данных сенсоров
     */
    public List<SensorData> getSensorDataList() {
        return sensorDataList;
    }

    /**
     * Устанавливает список данных сенсоров, связанных с запросом.
     *
     * @param sensorDataList список данных сенсоров
     */
    public void setSensorDataList(List<SensorData> sensorDataList) {
        this.sensorDataList = sensorDataList;
    }
}