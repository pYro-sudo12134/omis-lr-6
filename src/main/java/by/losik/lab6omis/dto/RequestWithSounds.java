package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.types.Request;
import by.losik.lab6omis.entities.general.types.Sound;

import java.util.List;

public class RequestWithSounds {
    private Request request;
    private List<Sound> sounds;

    public Request getRequest() { return request; }
    public void setRequest(Request request) { this.request = request; }
    public List<Sound> getSounds() { return sounds; }
    public void setSounds(List<Sound> sounds) { this.sounds = sounds; }
}
