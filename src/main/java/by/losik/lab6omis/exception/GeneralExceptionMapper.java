package by.losik.lab6omis.exception;

import javax.annotation.Priority;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Date;
import java.util.Map;

/**
 * Общий маппер исключений для обработки всех неперехваченных исключений (HTTP 500).
 * Преобразует любое исключение в стандартизированный JSON-ответ
 * с кодом состояния 500 (INTERNAL_SERVER_ERROR).
 * Имеет низкий приоритет обработки (@Priority(1000)) для обработки исключений,
 * которые не были перехвачены другими, более специализированными мапперами.
 *
 * @author Losik Yaroslav
 * @version 1.0
 */
@Provider
@Priority(1000)
public class GeneralExceptionMapper implements ExceptionMapper<Exception> {
    /**
     * Преобразует любое исключение в HTTP-ответ с кодом 500.
     * Возвращает JSON-объект с описанием ошибки, сообщением и временной меткой.
     * Используется как обработчик по умолчанию для неперехваченных исключений.
     *
     * @param e Exception, которая была выброшена
     * @return Response объект с кодом состояния 500 и JSON-телом ответа
     */
    @Override
    public Response toResponse(Exception e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of(
                        "error", "Ошибка сервера",
                        "message", e.getMessage(),
                        "timestamp", new Date()
                ))
                .build();
    }
}