package by.losik.lab6omis.resource;

import by.losik.lab6omis.service.cache.CacheMonitorService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("/cache")
@Produces(MediaType.APPLICATION_JSON)
public class CacheResource {

    @Inject
    private CacheMonitorService cacheMonitorService;

    @GET
    @Path("/stats")
    public Response getCacheStatistics() {
        Map<String, Object> stats = cacheMonitorService.getCacheStatistics();
        return Response.ok(stats).build();
    }

    @POST
    @Path("/clear")
    public Response clearCache() {
        cacheMonitorService.clearAllCaches();
        return Response.ok("Cache cleared").build();
    }
}