package by.losik.lab6omis.exception;

import javax.annotation.Priority;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Date;
import java.util.Map;

@Provider
@Priority(1000)
public class GeneralExceptionMapper implements ExceptionMapper<Exception> {
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