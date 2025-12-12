package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.types.Request;
import by.losik.lab6omis.entities.general.types.SensorData;

import java.util.List;

public class RequestWithSensorData {
    private Request request;
    private List<SensorData> sensorDataList;

    public Request getRequest() { return request; }
    public void setRequest(Request request) { this.request = request; }
    public List<SensorData> getSensorDataList() { return sensorDataList; }
    public void setSensorDataList(List<SensorData> sensorDataList) { this.sensorDataList = sensorDataList; }
}