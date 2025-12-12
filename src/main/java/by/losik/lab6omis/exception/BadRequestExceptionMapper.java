package by.losik.lab6omis.exception;

import javax.annotation.Priority;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Date;
import java.util.Map;

/**
 * Маппер исключений для обработки BadRequestException (HTTP 400).
 * Преобразует исключение BadRequestException в стандартизированный JSON-ответ
 * с кодом состояния 400 (BAD_REQUEST).
 * Имеет высокий приоритет обработки (@Priority(1)) для специализированной обработки.
 *
 * @author Losik Yaroslav
 * @version 1.0
 */
@Provider
@Priority(1)
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {
    /**
     * Преобразует BadRequestException в HTTP-ответ с кодом 400.
     * Возвращает JSON-объект с описанием ошибки, сообщением и временной меткой.
     *
     * @param e BadRequestException, которая была выброшена
     * @return Response объект с кодом состояния 400 и JSON-телом ответа
     */
    @Override
    public Response toResponse(BadRequestException e) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of(
                        "error", "Ошибка запроса",
                        "message", e.getMessage(),
                        "timestamp", new Date()
                ))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}