package by.losik.lab6omis.resource.base;

import by.losik.lab6omis.dto.CountResponse;
import by.losik.lab6omis.service.base.BaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Базовый REST контроллер с общими операциями CRUD.
 * Использует String для PathParam с конвертацией в нужный тип.
 *
 * @param <T> тип сущности
 * @param <ID> тип идентификатора (Long, String, etc.)
 * @param <S> тип сервиса
 * @author Losik Yaroslav
 * @version 1.0
 */
public abstract class BaseResource<T, ID, S extends BaseService<T, ID>> {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());
    protected S service;

    /**
     * Создание новой сущности.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@Valid T entity) {
        LOG.debug("Создание новой сущности через REST");
        T created = service.executeWithLogging(
                "Создание сущности через REST",
                () -> createEntity(entity)
        );
        return Response.status(Response.Status.CREATED)
                .entity(created)
                .build();
    }

    /**
     * Получение сущности по ID.
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") String idString) {
        LOG.debug("Получение сущности по ID: {}", idString);
        T entity = service.executeWithLogging(
                String.format("Получение сущности по ID через REST: %s", idString),
                () -> {
                    ID id = convertToId(idString);
                    return getEntityById(id);
                }
        );
        return Response.ok(entity).build();
    }

    /**
     * Получение всех сущностей.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAll() {
        LOG.debug("Получение всех сущностей через REST");
        List<T> entities = service.executeWithLogging(
                "Получение всех сущностей через REST",
                this::getAllEntities
        );
        return Response.ok(entities).build();
    }

    /**
     * Получение сущностей с пагинацией.
     */
    @GET
    @Path("/page/{page}/size/{size}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllPaginated(
            @PathParam("page") @DefaultValue("0") int page,
            @PathParam("size") @DefaultValue("20") int size) {
        LOG.debug("Получение сущностей с пагинацией: page={}, size={}", page, size);
        List<T> entities = service.executeWithLogging(
                String.format("Получение сущностей с пагинацией через REST: page=%d, size=%d", page, size),
                () -> getEntitiesPaginated(page, size)
        );
        return Response.ok(entities).build();
    }

    /**
     * Обновление сущности.
     */
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") String idString, @Valid T entity) {
        LOG.debug("Обновление сущности с ID: {}", idString);
        T updated = service.executeWithLogging(
                String.format("Обновление сущности через REST, ID: %s", idString),
                () -> {
                    ID id = convertToId(idString);
                    return updateEntity(id, entity);
                }
        );
        return Response.ok(updated).build();
    }

    /**
     * Удаление сущности.
     */
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") String idString) {
        LOG.debug("Удаление сущности с ID: {}", idString);
        service.executeVoidWithLogging(
                String.format("Удаление сущности через REST, ID: %s", idString),
                () -> {
                    ID id = convertToId(idString);
                    deleteEntity(id);
                }
        );
        return Response.noContent().build();
    }

    /**
     * Получение общего количества.
     */
    @GET
    @Path("/count")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCount() {
        LOG.debug("Получение общего количества сущностей через REST");
        long count = service.executeWithLogging(
                "Получение количества сущностей через REST",
                this::getTotalCount
        );
        return Response.ok(new CountResponse(count)).build();
    }

    /**
     * Конвертация строки в ID нужного типа.
     * Должен быть переопределен в наследниках.
     */
    protected abstract ID convertToId(String idString);
    protected abstract T createEntity(T entity);
    protected abstract T getEntityById(ID id);
    protected abstract List<T> getAllEntities();
    protected abstract List<T> getEntitiesPaginated(int page, int size);
    protected abstract T updateEntity(ID id, T entity);
    protected abstract void deleteEntity(ID id);
    protected abstract long getTotalCount();
}