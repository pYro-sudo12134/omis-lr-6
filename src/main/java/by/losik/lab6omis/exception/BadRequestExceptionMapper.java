package by.losik.lab6omis.exception;

import javax.annotation.Priority;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Date;
import java.util.Map;

@Provider
@Priority(1)
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {
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
