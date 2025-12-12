package by.losik.lab6omis.resource.general.types;

import by.losik.lab6omis.dto.DeleteResponse;
import by.losik.lab6omis.dto.ExistsResponse;
import by.losik.lab6omis.dto.IsNewResponse;
import by.losik.lab6omis.dto.SearchResponse;
import by.losik.lab6omis.entities.general.types.Sensor;
import by.losik.lab6omis.resource.base.BaseResource;
import by.losik.lab6omis.service.general.types.SensorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * REST ресурс для управления сенсорами (Sensor).
 * Предоставляет полный набор операций CRUD, поиска, фильтрации и статистики.
 * Наследует базовые операции CRUD от {@link BaseResource}.
 *
 * @see Sensor
 * @see SensorService
 * @author Losik Yaroslav
 * @version 1.0
 */
@Path("/api/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource extends BaseResource<Sensor, Long, SensorService> {

    private static final Logger LOG = LoggerFactory.getLogger(SensorResource.class);

    @Inject
    public SensorResource(SensorService service) {
        this.service = service;
    }

    @Override
    protected Long convertToId(String idString) {
        try {
            return Long.parseLong(idString);
        } catch (NumberFormatException e) {
            throw new BadRequestException(
                    String.format("Некорректный формат ID сенсора: '%s'. Ожидается числовой идентификатор.", idString)
            );
        }
    }

    @Override
    protected Sensor createEntity(Sensor entity) {
        return service.createSensor(entity);
    }

    @Override
    protected Sensor getEntityById(Long id) {
        return service.getById(id);
    }

    @Override
    protected List<Sensor> getAllEntities() {
        return service.getAllSensors();
    }

    @Override
    protected List<Sensor> getEntitiesPaginated(int page, int size) {
        return service.getAllSensors(page, size);
    }

    @Override
    protected Sensor updateEntity(Long id, Sensor entity) {
        return service.updateSensor(id, entity);
    }

    @Override
    protected void deleteEntity(Long id) {
        service.deleteSensor(id);
    }

    @Override
    protected long getTotalCount() {
        return service.countAllSensors();
    }

    /**
     * Получает сенсор по точному имени.
     *
     * @param name имя сенсора для поиска
     * @return найденный сенсор
     */
    @GET
    @Path("/name/{name}")
    public Response getByName(@PathParam("name") String name) {
        LOG.debug("Поиск сенсора по имени: {}", name);

        Sensor sensor = service.executeWithLogging(
                String.format("Поиск сенсора по имени через REST: '%s'", name),
                () -> service.getByName(name)
        );

        return Response.ok(sensor).build();
    }

    /**
     * Ищет сенсоры по частичному совпадению имени.
     *
     * @param searchText текст для поиска в имени
     * @return список найденных сенсоров
     */
    @GET
    @Path("/search/name")
    public Response searchByName(@QueryParam("q") String searchText) {
        LOG.debug("Поиск сенсоров по имени: {}", searchText);

        List<Sensor> sensors = service.executeWithLogging(
                String.format("Поиск сенсоров по имени через REST: '%s'", searchText),
                () -> service.searchByName(searchText)
        );

        return Response.ok(new SearchResponse<>(sensors)).build();
    }

    /**
     * Получает сенсоры по точному типу.
     *
     * @param type тип сенсора для поиска
     * @return список сенсоров указанного типа
     */
    @GET
    @Path("/type/{type}")
    public Response getByType(@PathParam("type") String type) {
        LOG.debug("Поиск сенсоров по типу: {}", type);

        List<Sensor> sensors = service.executeWithLogging(
                String.format("Поиск сенсоров по типу через REST: '%s'", type),
                () -> service.getByType(type)
        );

        return Response.ok(new SearchResponse<>(sensors)).build();
    }

    /**
     * Ищет сенсоры по частичному совпадению типа.
     *
     * @param searchText текст для поиска в типе
     * @return список найденных сенсоров
     */
    @GET
    @Path("/search/type")
    public Response searchByType(@QueryParam("q") String searchText) {
        LOG.debug("Поиск сенсоров по типу: {}", searchText);

        List<Sensor> sensors = service.executeWithLogging(
                String.format("Поиск сенсоров по типу через REST: '%s'", searchText),
                () -> service.searchByType(searchText)
        );

        return Response.ok(new SearchResponse<>(sensors)).build();
    }

    /**
     * Получает сенсоры по локации.
     *
     * @param location локация для поиска
     * @return список сенсоров в указанной локации
     */
    @GET
    @Path("/location/{location}")
    public Response getByLocation(@PathParam("location") String location) {
        LOG.debug("Поиск сенсоров по локации: {}", location);

        List<Sensor> sensors = service.executeWithLogging(
                String.format("Поиск сенсоров по локации через REST: '%s'", location),
                () -> service.getByLocation(location)
        );

        return Response.ok(new SearchResponse<>(sensors)).build();
    }

    /**
     * Получает сенсоры по статусу активности.
     *
     * @param isActive статус активности (true - активные, false - неактивные)
     * @return список сенсоров с указанным статусом активности
     */
    @GET
    @Path("/active/{isActive}")
    public Response getByActiveStatus(@PathParam("isActive") Boolean isActive) {
        LOG.debug("Поиск сенсоров по статусу активности: {}", isActive);

        List<Sensor> sensors = service.executeWithLogging(
                String.format("Поиск сенсоров по статусу активности через REST: %s", isActive),
                () -> service.getByActiveStatus(isActive)
        );

        return Response.ok(new SearchResponse<>(sensors)).build();
    }

    /**
     * Получает активные сенсоры.
     *
     * @return список активных сенсоров
     */
    @GET
    @Path("/active")
    public Response getActiveSensors() {
        LOG.debug("Получение активных сенсоров");

        List<Sensor> sensors = service.executeWithLogging(
                "Получение активных сенсоров через REST",
                () -> service.getActiveSensors()
        );

        return Response.ok(new SearchResponse<>(sensors)).build();
    }

    /**
     * Получает неактивные сенсоры.
     *
     * @return список неактивных сенсоров
     */
    @GET
    @Path("/inactive")
    public Response getInactiveSensors() {
        LOG.debug("Получение неактивных сенсоров");

        List<Sensor> sensors = service.executeWithLogging(
                "Получение неактивных сенсоров через REST",
                () -> service.getInactiveSensors()
        );

        return Response.ok(new SearchResponse<>(sensors)).build();
    }

    /**
     * Ищет сенсоры по частичному совпадению локации.
     *
     * @param searchText текст для поиска в локации
     * @return список найденных сенсоров
     */
    @GET
    @Path("/search/location")
    public Response searchByLocation(@QueryParam("q") String searchText) {
        LOG.debug("Поиск сенсоров по локации: {}", searchText);

        List<Sensor> sensors = service.executeWithLogging(
                String.format("Поиск сенсоров по локации через REST: '%s'", searchText),
                () -> service.searchByLocation(searchText)
        );

        return Response.ok(new SearchResponse<>(sensors)).build();
    }

    /**
     * Получает сенсоры по имени и типу.
     *
     * @param name имя сенсора
     * @param type тип сенсора
     * @return список сенсоров, соответствующих обоим критериям
     */
    @GET
    @Path("/name/{name}/type/{type}")
    public Response getByNameAndType(
            @PathParam("name") String name,
            @PathParam("type") String type) {

        LOG.debug("Поиск сенсоров по имени '{}' и типу '{}'", name, type);

        List<Sensor> sensors = service.executeWithLogging(
                String.format("Поиск сенсоров по имени '%s' и типу '%s' через REST", name, type),
                () -> service.getByNameAndType(name, type)
        );

        return Response.ok(new SearchResponse<>(sensors)).build();
    }

    /**
     * Получает сенсоры по типу с сортировкой по имени (по возрастанию).
     *
     * @param type тип сенсора
     * @return отсортированный список сенсоров
     */
    @GET
    @Path("/type/{type}/sorted/name-asc")
    public Response getByTypeOrderByNameAsc(@PathParam("type") String type) {
        LOG.debug("Поиск сенсоров по типу '{}' с сортировкой по имени (возр.)", type);

        List<Sensor> sensors = service.executeWithLogging(
                String.format("Поиск сенсоров по типу '%s' с сортировкой по имени (возр.) через REST", type),
                () -> service.getByTypeOrderByNameAsc(type)
        );

        return Response.ok(new SearchResponse<>(sensors)).build();
    }

    /**
     * Получает сенсоры по типу с сортировкой по имени (по убыванию).
     *
     * @param type тип сенсора
     * @return отсортированный список сенсоров
     */
    @GET
    @Path("/type/{type}/sorted/name-desc")
    public Response getByTypeOrderByNameDesc(@PathParam("type") String type) {
        LOG.debug("Поиск сенсоров по типу '{}' с сортировкой по имени (убыв.)", type);

        List<Sensor> sensors = service.executeWithLogging(
                String.format("Поиск сенсоров по типу '%s' с сортировкой по имени (убыв.) через REST", type),
                () -> service.getByTypeOrderByNameDesc(type)
        );

        return Response.ok(new SearchResponse<>(sensors)).build();
    }

    /**
     * Получает сенсоры, имена которых начинаются с указанного префикса.
     *
     * @param prefix префикс имени
     * @return список сенсоров, чьи имена начинаются с указанного префикса
     */
    @GET
    @Path("/name/starts-with/{prefix}")
    public Response getByNameStartingWith(@PathParam("prefix") String prefix) {
        LOG.debug("Поиск сенсоров по префиксу имени: '{}'", prefix);

        List<Sensor> sensors = service.executeWithLogging(
                String.format("Поиск сенсоров по префиксу имени через REST: '%s'", prefix),
                () -> service.getByNameStartingWith(prefix)
        );

        return Response.ok(new SearchResponse<>(sensors)).build();
    }

    /**
     * Получает сенсоры, имена которых заканчиваются на указанный суффикс.
     *
     * @param suffix суффикс имени
     * @return список сенсоров, чьи имена заканчиваются на указанный суффикс
     */
    @GET
    @Path("/name/ends-with/{suffix}")
    public Response getByNameEndingWith(@PathParam("suffix") String suffix) {
        LOG.debug("Поиск сенсоров по суффиксу имени: '{}'", suffix);

        List<Sensor> sensors = service.executeWithLogging(
                String.format("Поиск сенсоров по суффиксу имени через REST: '%s'", suffix),
                () -> service.getByNameEndingWith(suffix)
        );

        return Response.ok(new SearchResponse<>(sensors)).build();
    }

    /**
     * Получает статистику по количеству сенсоров каждого типа.
     *
     * @return карта [тип сенсора, количество]
     */
    @GET
    @Path("/stats/count-by-type")
    public Response getSensorCountByType() {
        LOG.debug("Получение статистики сенсоров по типам");

        Map<String, Long> stats = service.executeWithLogging(
                "Получение статистики сенсоров по типам через REST",
                () -> service.getSensorCountByType()
        );

        return Response.ok(stats).build();
    }

    /**
     * Получает статистику по количеству сенсоров в каждой локации.
     *
     * @return карта [локация, количество сенсоров]
     */
    @GET
    @Path("/stats/count-by-location")
    public Response getSensorCountByLocation() {
        LOG.debug("Получение статистики сенсоров по локациям");

        Map<String, Long> stats = service.executeWithLogging(
                "Получение статистики сенсоров по локациям через REST",
                () -> service.getSensorCountByLocation()
        );

        return Response.ok(stats).build();
    }

    /**
     * Получает статистику по количеству активных/неактивных сенсоров.
     *
     * @return карта [статус активности, количество]
     */
    @GET
    @Path("/stats/count-by-active-status")
    public Response getSensorCountByActiveStatus() {
        LOG.debug("Получение статистики сенсоров по статусу активности");

        Map<Boolean, Long> stats = service.executeWithLogging(
                "Получение статистики сенсоров по статусу активности через REST",
                () -> service.getSensorCountByActiveStatus()
        );

        return Response.ok(stats).build();
    }

    /**
     * Проверяет, существует ли сенсор с указанным именем.
     *
     * @param name имя сенсора для проверки
     * @return объект с результатом проверки
     */
    @GET
    @Path("/exists/name/{name}")
    public Response existsByName(@PathParam("name") String name) {
        LOG.debug("Проверка существования сенсора с именем: {}", name);

        boolean exists = service.executeWithLogging(
                String.format("Проверка существования сенсора с именем через REST: '%s'", name),
                () -> service.existsByName(name)
        );

        return Response.ok(new ExistsResponse(exists)).build();
    }

    /**
     * Удаляет все сенсоры указанного типа.
     *
     * @param type тип сенсора для удаления
     * @return объект с количеством удаленных записей
     */
    @DELETE
    @Path("/type/{type}")
    public Response deleteByType(@PathParam("type") String type) {
        LOG.debug("Удаление сенсоров типа: {}", type);

        int deletedCount = service.executeWithLogging(
                String.format("Удаление сенсоров типа через REST: '%s'", type),
                () -> service.deleteByType(type)
        );

        return Response.ok(new DeleteResponse(deletedCount)).build();
    }

    /**
     * Удаляет все сенсоры в указанной локации.
     *
     * @param location локация для удаления
     * @return объект с количеством удаленных записей
     */
    @DELETE
    @Path("/location/{location}")
    public Response deleteByLocation(@PathParam("location") String location) {
        LOG.debug("Удаление сенсоров в локации: {}", location);

        int deletedCount = service.executeWithLogging(
                String.format("Удаление сенсоров в локации через REST: '%s'", location),
                () -> service.deleteByLocation(location)
        );

        return Response.ok(new DeleteResponse(deletedCount)).build();
    }

    /**
     * Получает сенсоры по типу с пагинацией.
     *
     * @param type тип сенсора
     * @param page номер страницы
     * @param size размер страницы
     * @return список сенсоров для указанной страницы
     */
    @GET
    @Path("/type/{type}/page/{page}/size/{size}")
    public Response getByTypeWithPagination(
            @PathParam("type") String type,
            @PathParam("page") @DefaultValue("0") int page,
            @PathParam("size") @DefaultValue("20") int size) {

        LOG.debug("Получение сенсоров типа '{}' с пагинацией: page={}, size={}", type, page, size);

        List<Sensor> sensors = service.executeWithLogging(
                String.format("Получение сенсоров типа '%s' с пагинацией через REST: page=%d, size=%d", type, page, size),
                () -> service.getByTypeWithPagination(type, page, size)
        );

        return Response.ok(new SearchResponse<>(sensors)).build();
    }

    /**
     * Получает количество сенсоров указанного типа.
     *
     * @param type тип сенсора
     * @return количество сенсоров указанного типа
     */
    @GET
    @Path("/count/type/{type}")
    public Response countByType(@PathParam("type") String type) {
        LOG.debug("Получение количества сенсоров типа: {}", type);

        Long count = service.executeWithLogging(
                String.format("Получение количества сенсоров типа через REST: '%s'", type),
                () -> service.countByType(type)
        );

        return Response.ok(java.util.Map.of("type", type, "count", count)).build();
    }

    /**
     * Получает сенсоры по нескольким типам.
     *
     * @param types список типов сенсоров (через запятую)
     * @return список сенсоров указанных типов
     */
    @GET
    @Path("/multiple-types")
    public Response getByTypes(@QueryParam("types") String types) {
        LOG.debug("Поиск сенсоров по типам: {}", types);

        try {
            List<String> typeList = java.util.Arrays.stream(types.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(java.util.stream.Collectors.toList());

            if (typeList.isEmpty()) {
                return Response.ok(new SearchResponse<>(List.of())).build();
            }

            List<Sensor> sensors = service.executeWithLogging(
                    String.format("Поиск сенсоров по типам через REST: %s", typeList),
                    () -> service.getByTypes(typeList)
            );

            return Response.ok(new SearchResponse<>(sensors)).build();

        } catch (Exception e) {
            LOG.warn("Ошибка при поиске сенсоров по типам: {}", types);
            throw new BadRequestException(
                    String.format("Ошибка при поиске сенсоров по типам: '%s'. Ожидается список типов через запятую.", types)
            );
        }
    }

    /**
     * Получает сенсоры по нескольким локациям.
     *
     * @param locations список локаций (через запятую)
     * @return список сенсоров в указанных локациях
     */
    @GET
    @Path("/multiple-locations")
    public Response getByLocations(@QueryParam("locations") String locations) {
        LOG.debug("Поиск сенсоров по локациям: {}", locations);

        try {
            List<String> locationList = java.util.Arrays.stream(locations.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(java.util.stream.Collectors.toList());

            if (locationList.isEmpty()) {
                return Response.ok(new SearchResponse<>(List.of())).build();
            }

            List<Sensor> sensors = service.executeWithLogging(
                    String.format("Поиск сенсоров по локациям через REST: %s", locationList),
                    () -> service.getByLocations(locationList)
            );

            return Response.ok(new SearchResponse<>(sensors)).build();

        } catch (Exception e) {
            LOG.warn("Ошибка при поиске сенсоров по локациям: {}", locations);
            throw new BadRequestException(
                    String.format("Ошибка при поиске сенсоров по локациям: '%s'. Ожидается список локаций через запятую.", locations)
            );
        }
    }

    /**
     * Получает сенсоры без указанной локации.
     *
     * @return список сенсоров без локации
     */
    @GET
    @Path("/without-location")
    public Response getSensorsWithoutLocation() {
        LOG.debug("Поиск сенсоров без локации");

        List<Sensor> sensors = service.executeWithLogging(
                "Поиск сенсоров без локации через REST",
                () -> service.getSensorsWithoutLocation()
        );

        return Response.ok(new SearchResponse<>(sensors)).build();
    }

    /**
     * Обновляет статус активности сенсора.
     *
     * @param sensorId ID сенсора
     * @param isActive новый статус активности
     * @return количество обновленных записей
     */
    @PUT
    @Path("/{sensorId}/activity/{isActive}")
    public Response updateSensorActivity(
            @PathParam("sensorId") Long sensorId,
            @PathParam("isActive") Boolean isActive) {

        LOG.debug("Обновление активности сенсора ID={} на {}", sensorId, isActive);

        int updatedCount = service.executeWithLogging(
                String.format("Обновление активности сенсора через REST: ID=%d, активность=%s", sensorId, isActive),
                () -> service.updateSensorActivity(sensorId, isActive)
        );

        return Response.ok(java.util.Map.of("sensorId", sensorId, "updatedCount", updatedCount)).build();
    }

    /**
     * Активирует все сенсоры указанного типа.
     *
     * @param type тип сенсора
     * @return количество обновленных записей
     */
    @PUT
    @Path("/type/{type}/activate")
    public Response activateSensorsByType(@PathParam("type") String type) {
        LOG.debug("Активация всех сенсоров типа: {}", type);

        int updatedCount = service.executeWithLogging(
                String.format("Активация сенсоров типа через REST: '%s'", type),
                () -> service.activateSensorsByType(type)
        );

        return Response.ok(new DeleteResponse(updatedCount)).build();
    }

    /**
     * Деактивирует все сенсоры указанного типа.
     *
     * @param type тип сенсора
     * @return количество обновленных записей
     */
    @PUT
    @Path("/type/{type}/deactivate")
    public Response deactivateSensorsByType(@PathParam("type") String type) {
        LOG.debug("Деактивация всех сенсоров типа: {}", type);

        int updatedCount = service.executeWithLogging(
                String.format("Деактивация сенсоров типа через REST: '%s'", type),
                () -> service.deactivateSensorsByType(type)
        );

        return Response.ok(new DeleteResponse(updatedCount)).build();
    }

    /**
     * Ищет сенсоры по шаблону имени.
     *
     * @param namePattern паттерн имени (можно использовать % и _)
     * @return список сенсоров, имена которых соответствуют паттерну
     */
    @GET
    @Path("/name/pattern/{namePattern}")
    public Response getByNamePattern(@PathParam("namePattern") String namePattern) {
        LOG.debug("Поиск сенсоров по шаблону имени: '{}'", namePattern);

        List<Sensor> sensors = service.executeWithLogging(
                String.format("Поиск сенсоров по шаблону имени через REST: '%s'", namePattern),
                () -> service.getByNamePattern(namePattern)
        );

        return Response.ok(new SearchResponse<>(sensors)).build();
    }

    /**
     * Получает сенсоры с количеством связанных данных.
     *
     * @return список массивов [сенсор, количество данных]
     */
    @GET
    @Path("/stats/with-data-count")
    public Response getSensorsWithDataCount() {
        LOG.debug("Получение сенсоров с количеством связанных данных");

        List<Object[]> sensorsWithCount = service.executeWithLogging(
                "Получение сенсоров с количеством связанных данных через REST",
                () -> service.getSensorsWithDataCount()
        );

        return Response.ok(sensorsWithCount).build();
    }

    /**
     * Получает сенсоры без связанных данных.
     *
     * @return список сенсоров без данных
     */
    @GET
    @Path("/without-data")
    public Response getSensorsWithoutData() {
        LOG.debug("Поиск сенсоров без связанных данных");

        List<Sensor> sensors = service.executeWithLogging(
                "Поиск сенсоров без связанных данных через REST",
                () -> service.getSensorsWithoutData()
        );

        return Response.ok(new SearchResponse<>(sensors)).build();
    }

    /**
     * Получает сенсоры с наибольшим количеством данных.
     *
     * @param limit максимальное количество возвращаемых записей
     * @return список сенсоров с наибольшим количеством данных
     */
    @GET
    @Path("/top-by-data/{limit}")
    public Response getTopSensorsByDataCount(@PathParam("limit") Integer limit) {
        LOG.debug("Получение топ-{} сенсоров по количеству данных", limit);

        List<Sensor> sensors = service.executeWithLogging(
                String.format("Получение топ-%d сенсоров по количеству данных через REST", limit),
                () -> service.getTopSensorsByDataCount(limit)
        );

        return Response.ok(new SearchResponse<>(sensors)).build();
    }

    /**
     * Проверяет, является ли сенсор новым (не сохраненным в БД).
     *
     * @param sensor объект сенсора для проверки
     * @return объект с результатом проверки
     */
    @POST
    @Path("/check-new")
    public Response checkIfNew(@Valid Sensor sensor) {
        LOG.debug("Проверка, является ли сенсор новым");

        boolean isNew = service.executeWithLogging(
                "Проверка нового сенсора через REST",
                () -> service.isNewSensor(sensor)
        );

        return Response.ok(new IsNewResponse(isNew)).build();
    }
}