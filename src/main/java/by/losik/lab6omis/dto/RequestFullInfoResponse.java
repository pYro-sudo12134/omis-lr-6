package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.types.Request;
import by.losik.lab6omis.entities.general.types.SensorData;
import by.losik.lab6omis.entities.general.types.Sound;

import java.util.List;

public class RequestFullInfoResponse {
    private final Request request;
    private final List<Sound> sounds;
    private final List<SensorData> sensorDataList;

    public RequestFullInfoResponse(Request request, List<Sound> sounds, List<SensorData> sensorDataList) {
        this.request = request;
        this.sounds = sounds;
        this.sensorDataList = sensorDataList;
    }

    public Request getRequest() { return request; }
    public List<Sound> getSounds() { return sounds; }
    public List<SensorData> getSensorDataList() { return sensorDataList; }
}
