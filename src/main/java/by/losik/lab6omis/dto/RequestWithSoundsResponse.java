package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.types.Request;
import by.losik.lab6omis.entities.general.types.Sound;

import java.util.List;

public class RequestWithSoundsResponse {
    private final Request request;
    private final List<Sound> sounds;

    public RequestWithSoundsResponse(Request request, List<Sound> sounds) {
        this.request = request;
        this.sounds = sounds;
    }

    public Request getRequest() { return request; }
    public List<Sound> getSounds() { return sounds; }
}