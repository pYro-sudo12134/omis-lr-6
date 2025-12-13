package by.losik.lab6omis.exception;

import javax.annotation.Priority;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Date;
import java.util.Map;

/**
 * Маппер исключений для обработки NotFoundException (HTTP 404).
 * Преобразует исключение NotFoundException в стандартизированный JSON-ответ
 * с кодом состояния 404 (NOT_FOUND).
 * Имеет средний приоритет обработки.
 *
 * @author Losik Yaroslav
 * @version 1.0
 */
@Provider
@Priority(100)
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    @Override
    public Response toResponse(NotFoundException e) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of(
                        "error", "Ресурс не найден",
                        "message", e.getMessage(),
                        "timestamp", new Date()
                ))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}