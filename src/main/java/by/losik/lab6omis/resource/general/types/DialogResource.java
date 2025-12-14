package by.losik.lab6omis.resource.general.types;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

@Path("api/dialog")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public class DialogResource {

    @Context
    private UriInfo uriInfo;

    @POST
    @Path("/send")
    @Produces(MediaType.TEXT_HTML)
    public Response sendMessage(@FormParam("message") String message) {

        if (message == null || message.trim().isEmpty()) {
            URI redirectUri = uriInfo.getBaseUriBuilder()
                    .path("..")
                    .replaceQueryParam("error", "Сообщение не может быть пустым")
                    .build();
            return Response.seeOther(redirectUri).build();
        }

        String response;
        if (message.contains("привет")) {
            response = "Привет! Чем могу помочь?";
        } else if (message.contains("анализ")) {
            response = "Анализ данных выполняется...";
        } else {
            response = "Я получил ваше сообщение: " + message;
        }

        URI redirectUri = uriInfo.getBaseUriBuilder()
                .path("..")
                .replaceQueryParam("response", response)
                .build();

        return Response.seeOther(redirectUri).build();
    }
}