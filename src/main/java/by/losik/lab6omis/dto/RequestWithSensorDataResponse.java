package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.types.Request;
import by.losik.lab6omis.entities.general.types.SensorData;

import java.util.List;

public class RequestWithSensorDataResponse {
    private final Request request;
    private final List<SensorData> sensorDataList;

    public RequestWithSensorDataResponse(Request request, List<SensorData> sensorDataList) {
        this.request = request;
        this.sensorDataList = sensorDataList;
    }

    public Request getRequest() { return request; }
    public List<SensorData> getSensorDataList() { return sensorDataList; }
}