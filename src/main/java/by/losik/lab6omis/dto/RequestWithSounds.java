package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.types.Request;
import by.losik.lab6omis.entities.general.types.Sound;

import java.util.List;

/**
 * DTO класс для запроса со звуками.
 * Используется для передачи запроса с связанными звуковыми данными.
 */
public class RequestWithSounds {
    private Request request;
    private List<Sound> sounds;

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
     * Возвращает список звуков, связанных с запросом.
     *
     * @return список звуков
     */
    public List<Sound> getSounds() {
        return sounds;
    }

    /**
     * Устанавливает список звуков, связанных с запросом.
     *
     * @param sounds список звуков
     */
    public void setSounds(List<Sound> sounds) {
        this.sounds = sounds;
    }
}