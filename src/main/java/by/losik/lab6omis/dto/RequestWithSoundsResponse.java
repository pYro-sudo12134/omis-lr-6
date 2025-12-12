package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.types.Request;
import by.losik.lab6omis.entities.general.types.Sound;
import java.util.List;

/**
 * DTO класс для ответа с запросом и звуками.
 * Используется для структурированных REST ответов при получении запроса с связанными звуковыми данными.
 */
public class RequestWithSoundsResponse {
    private final Request request;
    private final List<Sound> sounds;

    /**
     * Создает ответ с запросом и связанными звуковыми данными.
     *
     * @param request объект запроса
     * @param sounds список звуков, связанных с запросом
     */
    public RequestWithSoundsResponse(Request request, List<Sound> sounds) {
        this.request = request;
        this.sounds = sounds;
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
     * Возвращает список звуков, связанных с запросом.
     *
     * @return список звуков
     */
    public List<Sound> getSounds() {
        return sounds;
    }
}