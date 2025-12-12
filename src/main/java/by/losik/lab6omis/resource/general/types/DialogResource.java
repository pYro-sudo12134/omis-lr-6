package by.losik.lab6omis.resource.general.types;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/api/dialog")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DialogResource {

    @POST
    @Path("/send")
    public Response sendMessage(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Сообщение получено: " + message);
        response.put("timestamp", System.currentTimeMillis());

        // Здесь можно добавить логику обработки сообщения
        if (message.contains("привет")) {
            response.put("response", "Привет! Чем могу помочь?");
        } else if (message.contains("анализ")) {
            response.put("response", "Анализ данных выполняется...");
        } else {
            response.put("response", "Я получил ваше сообщение: " + message);
        }

        return Response.ok(response).build();
    }
}