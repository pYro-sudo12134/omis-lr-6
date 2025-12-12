package by.losik.lab6omis.resource.general.types;

import by.losik.lab6omis.dto.DeleteResponse;
import by.losik.lab6omis.dto.ExistsResponse;
import by.losik.lab6omis.dto.IsNewResponse;
import by.losik.lab6omis.dto.SearchResponse;
import by.losik.lab6omis.entities.general.types.Sensor;
import by.losik.lab6omis.entities.general.types.SensorData;
import by.losik.lab6omis.resource.base.BaseResource;
import by.losik.lab6omis.service.general.types.SensorDataService;
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
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * REST ресурс для управления данными сенсоров (SensorData).
 * Предоставляет полный набор операций CRUD, поиска, фильтрации и статистики.
 * Наследует базовые операции CRUD от {@link BaseResource}.
 *
 * @see SensorData
 * @see SensorDataService
 * @author Losik Yaroslav
 * @version 1.0
 */
@Path("/api/sensor-data")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorDataResource extends BaseResource<SensorData, Long, SensorDataService> {

    private static final Logger LOG = LoggerFactory.getLogger(SensorDataResource.class);

    @Inject
    private SensorService sensorService;

    @Inject
    public SensorDataResource(SensorDataService service) {
        this.service = service;
    }

    @Override
    protected Long convertToId(String idString) {
        try {
            return Long.parseLong(idString);
        } catch (NumberFormatException e) {
            throw new BadRequestException(
                    String.format("Некорректный формат ID данных сенсора: '%s'. Ожидается числовой идентификатор.", idString)
            );
        }
    }

    @Override
    protected SensorData createEntity(SensorData entity) {
        return service.createSensorData(entity);
    }

    @Override
    protected SensorData getEntityById(Long id) {
        return service.getById(id);
    }

    @Override
    protected List<SensorData> getAllEntities() {
        return service.getAllSensorData();
    }

    @Override
    protected List<SensorData> getEntitiesPaginated(int page, int size) {
        return service.getAllSensorData(page, size);
    }

    @Override
    protected SensorData updateEntity(Long id, SensorData entity) {
        return service.updateSensorData(id, entity);
    }

    @Override
    protected void deleteEntity(Long id) {
        service.deleteSensorData(id);
    }

    @Override
    protected long getTotalCount() {
        return service.getTotalSensorDataCount();
    }

    /**
     * Получает данные сенсора по ID сенсора.
     *
     * @param sensorId ID сенсора
     * @return список данных указанного сенсора
     */
    @GET
    @Path("/sensor/{sensorId}")
    public Response getBySensorId(@PathParam("sensorId") Long sensorId) {
        LOG.debug("Получение данных сенсора по ID сенсора: {}", sensorId);

        List<SensorData> sensorData = service.executeWithLogging(
                String.format("Получение данных сенсора по ID сенсора через REST: %d", sensorId),
                () -> service.getBySensorId(sensorId)
        );

        return Response.ok(new SearchResponse<>(sensorData)).build();
    }

    /**
     * Получает данные сенсора по сенсору (объекту).
     *
     * @param sensorId ID сенсора
     * @return список данных указанного сенсора
     */
    @GET
    @Path("/sensor-object/{sensorId}")
    public Response getBySensor(@PathParam("sensorId") Long sensorId) {
        LOG.debug("Получение данных сенсора по объекту сенсора: {}", sensorId);

        Sensor sensor = sensorService.executeWithLogging(
                String.format("Получение сенсора по ID: %d", sensorId),
                () -> sensorService.getById(sensorId)
        );

        if (sensor == null) {
            throw new NotFoundException(String.format("Сенсор с ID %d не найден", sensorId));
        }

        List<SensorData> sensorData = service.executeWithLogging(
                String.format("Получение данных сенсора по объекту сенсора через REST: %d", sensorId),
                () -> service.getBySensor(sensor)
        );

        return Response.ok(new SearchResponse<>(sensorData)).build();
    }

    /**
     * Получает данные по назначению (точное совпадение).
     *
     * @param purpose назначение данных
     * @return найденные данные
     */
    @GET
    @Path("/purpose/exact/{purpose}")
    public Response getByPurpose(@PathParam("purpose") String purpose) {
        LOG.debug("Поиск данных по назначению: {}", purpose);

        SensorData sensorData = service.executeWithLogging(
                String.format("Поиск данных по назначению через REST: '%s'", purpose),
                () -> service.getByPurpose(purpose)
        );

        return Response.ok(sensorData).build();
    }

    /**
     * Ищет данные сенсоров по частичному совпадению назначения.
     *
     * @param searchText текст для поиска в назначении
     * @return список найденных данных
     */
    @GET
    @Path("/search/purpose")
    public Response searchByPurpose(@QueryParam("q") String searchText) {
        LOG.debug("Поиск данных по назначению: {}", searchText);

        List<SensorData> sensorData = service.executeWithLogging(
                String.format("Поиск данных по назначению через REST: '%s'", searchText),
                () -> service.searchByPurpose(searchText)
        );

        return Response.ok(new SearchResponse<>(sensorData)).build();
    }

    /**
     * Получает данные по временной метке.
     *
     * @param timestamp временная метка (в формате ISO: yyyy-MM-ddTHH:mm:ss)
     * @return список данных с указанной временной меткой
     */
    @GET
    @Path("/timestamp/{timestamp}")
    public Response getByTimestamp(@PathParam("timestamp") String timestamp) {
        LOG.debug("Поиск данных по временной метке: {}", timestamp);

        try {
            LocalDateTime dateTime = LocalDateTime.parse(timestamp);

            List<SensorData> sensorData = service.executeWithLogging(
                    String.format("Поиск данных по временной метке через REST: %s", dateTime),
                    () -> service.getByTimestamp(dateTime)
            );

            return Response.ok(new SearchResponse<>(sensorData)).build();

        } catch (Exception e) {
            LOG.warn("Некорректный формат временной метки: {}", timestamp);
            throw new BadRequestException(
                    String.format("Некорректный формат временной метки. Ожидается ISO формат (yyyy-MM-ddTHH:mm:ss). Получено: %s", timestamp)
            );
        }
    }

    /**
     * Получает данные за определенный период времени.
     *
     * @param startDate начальная дата (в формате ISO: yyyy-MM-ddTHH:mm:ss)
     * @param endDate конечная дата (в формате ISO: yyyy-MM-ddTHH:mm:ss)
     * @return список данных за указанный период
     */
    @GET
    @Path("/time-period")
    public Response getByTimestampBetween(
            @QueryParam("start") String startDate,
            @QueryParam("end") String endDate) {

        LOG.debug("Поиск данных за период: с {} по {}", startDate, endDate);

        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);

            List<SensorData> sensorData = service.executeWithLogging(
                    String.format("Поиск данных за период через REST: с %s по %s", start, end),
                    () -> service.getByTimestampBetween(start, end)
            );

            return Response.ok(new SearchResponse<>(sensorData)).build();

        } catch (Exception e) {
            LOG.warn("Некорректный формат даты: start={}, end={}", startDate, endDate);
            throw new BadRequestException(
                    String.format("Некорректный формат даты. Ожидается ISO формат (yyyy-MM-ddTHH:mm:ss). start: %s, end: %s",
                            startDate, endDate)
            );
        }
    }

    /**
     * Получает данные сенсоров за последние N дней.
     *
     * @param days количество дней
     * @return список данных за последние N дней
     */
    @GET
    @Path("/recent/{days}")
    public Response getRecentData(@PathParam("days") Integer days) {
        LOG.debug("Получение данных за последние {} дней", days);

        List<SensorData> sensorData = service.executeWithLogging(
                String.format("Получение данных за последние %d дней через REST", days),
                () -> service.getRecentData(days)
        );

        return Response.ok(new SearchResponse<>(sensorData)).build();
    }

    /**
     * Получает данные сенсора за определенный период.
     *
     * @param sensorId ID сенсора
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @return список данных сенсора за указанный период
     */
    @GET
    @Path("/sensor/{sensorId}/time-period")
    public Response getBySensorAndTimestampBetween(
            @PathParam("sensorId") Long sensorId,
            @QueryParam("start") String startDate,
            @QueryParam("end") String endDate) {

        LOG.debug("Поиск данных сенсора ID={} за период: с {} по {}", sensorId, startDate, endDate);

        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);

            Sensor sensor = sensorService.executeWithLogging(
                    String.format("Получение сенсора по ID: %d", sensorId),
                    () -> sensorService.getById(sensorId)
            );

            if (sensor == null) {
                throw new NotFoundException(String.format("Сенсор с ID %d не найден", sensorId));
            }

            List<SensorData> sensorData = service.executeWithLogging(
                    String.format("Поиск данных сенсора за период через REST: сенсор ID=%d, период: с %s по %s",
                            sensorId, start, end),
                    () -> service.getBySensorAndTimestampBetween(sensor, start, end)
            );

            return Response.ok(new SearchResponse<>(sensorData)).build();

        } catch (Exception e) {
            LOG.warn("Ошибка при поиске данных сенсора за период: sensorId={}, start={}, end={}",
                    sensorId, startDate, endDate);
            throw new BadRequestException(
                    String.format("Ошибка при поиске данных сенсора за период. sensorId: %d, start: %s, end: %s",
                            sensorId, startDate, endDate)
            );
        }
    }

    /**
     * Получает данные по назначению с сортировкой по времени (по возрастанию).
     *
     * @param purpose назначение данных
     * @return отсортированный список данных
     */
    @GET
    @Path("/purpose/{purpose}/sorted/asc")
    public Response getByPurposeOrderByTimestampAsc(@PathParam("purpose") String purpose) {
        LOG.debug("Поиск данных по назначению '{}' с сортировкой по времени (возр.)", purpose);

        List<SensorData> sensorData = service.executeWithLogging(
                String.format("Поиск данных по назначению с сортировкой (возр.) через REST: '%s'", purpose),
                () -> service.getByPurposeOrderByTimestampAsc(purpose)
        );

        return Response.ok(new SearchResponse<>(sensorData)).build();
    }

    /**
     * Получает данные по назначению с сортировкой по времени (по убыванию).
     *
     * @param purpose назначение данных
     * @return отсортированный список данных
     */
    @GET
    @Path("/purpose/{purpose}/sorted/desc")
    public Response getByPurposeOrderByTimestampDesc(@PathParam("purpose") String purpose) {
        LOG.debug("Поиск данных по назначению '{}' с сортировкой по времени (убыв.)", purpose);

        List<SensorData> sensorData = service.executeWithLogging(
                String.format("Поиск данных по назначению с сортировкой (убыв.) через REST: '%s'", purpose),
                () -> service.getByPurposeOrderByTimestampDesc(purpose)
        );

        return Response.ok(new SearchResponse<>(sensorData)).build();
    }

    /**
     * Получает данные по сенсору с сортировкой по времени.
     *
     * @param sensorId ID сенсора
     * @param order порядок сортировки (asc или desc)
     * @return отсортированный список данных
     */
    @GET
    @Path("/sensor/{sensorId}/sorted")
    public Response getBySensorOrderByTimestamp(
            @PathParam("sensorId") Long sensorId,
            @QueryParam("order") @DefaultValue("asc") String order) {

        boolean ascending = "asc".equalsIgnoreCase(order);
        LOG.debug("Поиск данных сенсора ID={} с сортировкой по времени ({})", sensorId, order);

        Sensor sensor = sensorService.executeWithLogging(
                String.format("Получение сенсора по ID: %d", sensorId),
                () -> sensorService.getById(sensorId)
        );

        if (sensor == null) {
            throw new NotFoundException(String.format("Сенсор с ID %d не найден", sensorId));
        }

        List<SensorData> sensorData = service.executeWithLogging(
                String.format("Поиск данных сенсора с сортировкой через REST: сенсор ID=%d, порядок=%s",
                        sensorId, order),
                () -> service.getBySensorOrderByTimestamp(sensor, ascending)
        );

        return Response.ok(new SearchResponse<>(sensorData)).build();
    }

    /**
     * Получает данные, назначение которых начинается с указанного текста.
     *
     * @param prefix префикс назначения
     * @return список данных, чьи назначения начинаются с указанного префикса
     */
    @GET
    @Path("/purpose/starts-with/{prefix}")
    public Response getByPurposeStartingWith(@PathParam("prefix") String prefix) {
        LOG.debug("Поиск данных по префиксу назначения: '{}'", prefix);

        List<SensorData> sensorData = service.executeWithLogging(
                String.format("Поиск данных по префиксу назначения через REST: '%s'", prefix),
                () -> service.getByPurposeStartingWith(prefix)
        );

        return Response.ok(new SearchResponse<>(sensorData)).build();
    }

    /**
     * Получает данные, назначение которых заканчивается на указанный текст.
     *
     * @param suffix суффикс назначения
     * @return список данных, чьи назначения заканчиваются на указанный суффикс
     */
    @GET
    @Path("/purpose/ends-with/{suffix}")
    public Response getByPurposeEndingWith(@PathParam("suffix") String suffix) {
        LOG.debug("Поиск данных по суффиксу назначения: '{}'", suffix);

        List<SensorData> sensorData = service.executeWithLogging(
                String.format("Поиск данных по суффиксу назначения через REST: '%s'", suffix),
                () -> service.getByPurposeEndingWith(suffix)
        );

        return Response.ok(new SearchResponse<>(sensorData)).build();
    }

    /**
     * Получает статистику по количеству данных для каждого сенсора.
     *
     * @return карта, где ключ - ID сенсора, значение - количество данных
     */
    @GET
    @Path("/stats/count-by-sensor")
    public Response getDataCountBySensor() {
        LOG.debug("Получение статистики данных по сенсорам");

        Map<Long, Long> stats = service.executeWithLogging(
                "Получение статистики данных по сенсорам через REST",
                () -> service.getDataCountBySensor()
        );

        return Response.ok(stats).build();
    }

    /**
     * Получает статистику по количеству данных за последние дни.
     *
     * @param days количество дней
     * @return карта, где ключ - дата, значение - количество данных
     */
    @GET
    @Path("/stats/count-by-day/{days}")
    public Response getDataCountByDay(@PathParam("days") Integer days) {
        LOG.debug("Получение статистики данных по дням за последние {} дней", days);

        Map<String, Long> stats = service.executeWithLogging(
                String.format("Получение статистики данных по дням через REST за последние %d дней", days),
                () -> service.getDataCountByDay(days)
        );

        return Response.ok(stats).build();
    }

    /**
     * Получает последние данные для каждого сенсора.
     *
     * @return список последних данных по каждому сенсору
     */
    @GET
    @Path("/latest-per-sensor")
    public Response getLatestDataForEachSensor() {
        LOG.debug("Получение последних данных для каждого сенсора");

        List<SensorData> sensorData = service.executeWithLogging(
                "Получение последних данных для каждого сенсора через REST",
                () -> service.getLatestDataForEachSensor()
        );

        return Response.ok(new SearchResponse<>(sensorData)).build();
    }

    /**
     * Получает самые старые данные сенсоров.
     *
     * @return список данных с минимальной временной меткой
     */
    @GET
    @Path("/oldest")
    public Response getOldestData() {
        LOG.debug("Получение самых старых данных сенсоров");

        List<SensorData> sensorData = service.executeWithLogging(
                "Получение самых старых данных сенсоров через REST",
                () -> service.getOldestData()
        );

        return Response.ok(new SearchResponse<>(sensorData)).build();
    }

    /**
     * Получает самые новые данные сенсоров.
     *
     * @return список данных с максимальной временной меткой
     */
    @GET
    @Path("/newest")
    public Response getNewestData() {
        LOG.debug("Получение самых новых данных сенсоров");

        List<SensorData> sensorData = service.executeWithLogging(
                "Получение самых новых данных сенсоров через REST",
                () -> service.getNewestData()
        );

        return Response.ok(new SearchResponse<>(sensorData)).build();
    }

    /**
     * Проверяет существование данных для указанного сенсора.
     *
     * @param sensorId ID сенсора для проверки
     * @return объект с результатом проверки
     */
    @GET
    @Path("/exists/sensor/{sensorId}")
    public Response existsBySensor(@PathParam("sensorId") Long sensorId) {
        LOG.debug("Проверка существования данных для сенсора ID: {}", sensorId);

        Sensor sensor = sensorService.executeWithLogging(
                String.format("Получение сенсора по ID: %d", sensorId),
                () -> sensorService.getById(sensorId)
        );

        if (sensor == null) {
            throw new NotFoundException(String.format("Сенсор с ID %d не найден", sensorId));
        }

        boolean exists = service.executeWithLogging(
                String.format("Проверка существования данных для сенсора через REST: %d", sensorId),
                () -> service.existsBySensor(sensor)
        );

        return Response.ok(new ExistsResponse(exists)).build();
    }

    /**
     * Удаляет все данные для указанного сенсора.
     *
     * @param sensorId ID сенсора, данные которого нужно удалить
     * @return объект с количеством удаленных записей
     */
    @DELETE
    @Path("/sensor/{sensorId}")
    public Response deleteBySensor(@PathParam("sensorId") Long sensorId) {
        LOG.debug("Удаление всех данных для сенсора ID: {}", sensorId);

        Sensor sensor = sensorService.executeWithLogging(
                String.format("Получение сенсора по ID: %d", sensorId),
                () -> sensorService.getById(sensorId)
        );

        if (sensor == null) {
            throw new NotFoundException(String.format("Сенсор с ID %d не найден", sensorId));
        }

        int deletedCount = service.executeWithLogging(
                String.format("Удаление всех данных для сенсора через REST: %d", sensorId),
                () -> service.deleteBySensor(sensor)
        );

        return Response.ok(new DeleteResponse(deletedCount)).build();
    }

    /**
     * Удаляет старые данные (до указанной даты).
     *
     * @param cutoffDate дата, до которой удалять данные (в формате ISO)
     * @return объект с количеством удаленных записей
     */
    @DELETE
    @Path("/old-data/{cutoffDate}")
    public Response deleteOldData(@PathParam("cutoffDate") String cutoffDate) {
        LOG.debug("Удаление старых данных до даты: {}", cutoffDate);

        try {
            LocalDateTime cutoff = LocalDateTime.parse(cutoffDate);

            int deletedCount = service.executeWithLogging(
                    String.format("Удаление старых данных через REST до даты: %s", cutoff),
                    () -> service.deleteOldData(cutoff)
            );

            return Response.ok(new DeleteResponse(deletedCount)).build();

        } catch (Exception e) {
            LOG.warn("Некорректный формат даты: {}", cutoffDate);
            throw new BadRequestException(
                    String.format("Некорректный формат даты. Ожидается ISO формат (yyyy-MM-ddTHH:mm:ss). Получено: %s", cutoffDate)
            );
        }
    }

    /**
     * Получает данные по сенсору с пагинацией.
     *
     * @param sensorId ID сенсора
     * @param page номер страницы
     * @param size размер страницы
     * @return список данных для указанной страницы
     */
    @GET
    @Path("/sensor/{sensorId}/page/{page}/size/{size}")
    public Response getBySensorWithPagination(
            @PathParam("sensorId") Long sensorId,
            @PathParam("page") @DefaultValue("0") int page,
            @PathParam("size") @DefaultValue("20") int size) {

        LOG.debug("Получение данных сенсора ID={} с пагинацией: page={}, size={}", sensorId, page, size);

        Sensor sensor = sensorService.executeWithLogging(
                String.format("Получение сенсора по ID: %d", sensorId),
                () -> sensorService.getById(sensorId)
        );

        if (sensor == null) {
            throw new NotFoundException(String.format("Сенсор с ID %d не найден", sensorId));
        }

        List<SensorData> sensorData = service.executeWithLogging(
                String.format("Получение данных сенсора с пагинацией через REST: сенсор ID=%d, page=%d, size=%d",
                        sensorId, page, size),
                () -> service.getBySensorWithPagination(sensor, page, size)
        );

        return Response.ok(new SearchResponse<>(sensorData)).build();
    }

    /**
     * Получает количество данных для указанного сенсора.
     *
     * @param sensorId ID сенсора для подсчета
     * @return количество данных для указанного сенсора
     */
    @GET
    @Path("/count/sensor/{sensorId}")
    public Response countBySensor(@PathParam("sensorId") Long sensorId) {
        LOG.debug("Подсчет количества данных для сенсора ID: {}", sensorId);

        Sensor sensor = sensorService.executeWithLogging(
                String.format("Получение сенсора по ID: %d", sensorId),
                () -> sensorService.getById(sensorId)
        );

        if (sensor == null) {
            throw new NotFoundException(String.format("Сенсор с ID %d не найден", sensorId));
        }

        Long count = service.executeWithLogging(
                String.format("Подсчет количества данных для сенсора через REST: %d", sensorId),
                () -> service.countBySensor(sensor)
        );

        return Response.ok(java.util.Map.of("sensorId", sensorId, "count", count)).build();
    }

    /**
     * Получает данные по нескольким сенсорам.
     *
     * @param sensorIds список ID сенсоров (через запятую)
     * @return список данных для указанных сенсоров
     */
    @GET
    @Path("/multiple-sensors")
    public Response getBySensors(@QueryParam("sensorIds") String sensorIds) {
        LOG.debug("Поиск данных по сенсорам: {}", sensorIds);

        try {
            List<Long> ids = java.util.Arrays.stream(sensorIds.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .collect(java.util.stream.Collectors.toList());

            if (ids.isEmpty()) {
                return Response.ok(new SearchResponse<>(List.of())).build();
            }

            List<Sensor> sensors = new java.util.ArrayList<>();
            for (Long sensorId : ids) {
                Sensor sensor = sensorService.executeWithLogging(
                        String.format("Получение сенсора по ID: %d", sensorId),
                        () -> sensorService.getById(sensorId)
                );

                if (sensor != null) {
                    sensors.add(sensor);
                }
            }

            if (sensors.isEmpty()) {
                throw new NotFoundException("Не найдены сенсоры с указанными ID");
            }

            List<SensorData> sensorData = service.executeWithLogging(
                    String.format("Поиск данных по %d сенсорам через REST", sensors.size()),
                    () -> service.getBySensors(sensors)
            );

            return Response.ok(new SearchResponse<>(sensorData)).build();

        } catch (NumberFormatException e) {
            LOG.warn("Некорректный формат ID сенсора в списке: {}", sensorIds);
            throw new BadRequestException(
                    String.format("Некорректный формат ID сенсора в списке: '%s'. Ожидается список числовых ID через запятую.", sensorIds)
            );
        }
    }

    /**
     * Получает данные по типу назначения и временному диапазону.
     *
     * @param purposePattern паттерн назначения
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @return список данных, соответствующих критериям
     */
    @GET
    @Path("/complex-search")
    public Response getByPurposePatternAndTimeRange(
            @QueryParam("pattern") String purposePattern,
            @QueryParam("start") String startDate,
            @QueryParam("end") String endDate) {

        LOG.debug("Комплексный поиск: паттерн='{}', период: {} - {}", purposePattern, startDate, endDate);

        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);

            List<SensorData> sensorData = service.executeWithLogging(
                    String.format("Комплексный поиск данных через REST: паттерн='%s', период: %s - %s",
                            purposePattern, start, end),
                    () -> service.getByPurposePatternAndTimeRange(purposePattern, start, end)
            );

            return Response.ok(new SearchResponse<>(sensorData)).build();

        } catch (Exception e) {
            LOG.warn("Ошибка при комплексном поиске: паттерн={}, start={}, end={}", purposePattern, startDate, endDate);
            throw new BadRequestException(
                    String.format("Ошибка при комплексном поиске. Проверьте параметры. Паттерн: %s, start: %s, end: %s",
                            purposePattern, startDate, endDate)
            );
        }
    }

    /**
     * Получает распределение данных по часам суток.
     *
     * @return карта, где ключ - час (0-23), значение - количество данных
     */
    @GET
    @Path("/stats/distribution-by-hour")
    public Response getDataDistributionByHour() {
        LOG.debug("Получение распределения данных по часам суток");

        Map<Integer, Long> distribution = service.executeWithLogging(
                "Получение распределения данных по часам суток через REST",
                () -> service.getDataDistributionByHour()
        );

        return Response.ok(distribution).build();
    }

    /**
     * Проверяет, являются ли данные новыми (не сохраненными в БД).
     *
     * @param sensorData объект данных сенсора для проверки
     * @return объект с результатом проверки
     */
    @POST
    @Path("/check-new")
    public Response checkIfNew(@Valid SensorData sensorData) {
        LOG.debug("Проверка, являются ли данные новыми");

        boolean isNew = service.executeWithLogging(
                "Проверка новых данных сенсора через REST",
                () -> service.isNewSensorData(sensorData)
        );

        return Response.ok(new IsNewResponse(isNew)).build();
    }
}