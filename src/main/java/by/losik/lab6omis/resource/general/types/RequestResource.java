package by.losik.lab6omis.resource.general.types;

import by.losik.lab6omis.dto.AverageAccuracyResponse;
import by.losik.lab6omis.dto.ComprehensiveStatsResponse;
import by.losik.lab6omis.dto.DeleteResponse;
import by.losik.lab6omis.dto.ExistsResponse;
import by.losik.lab6omis.dto.IsNewResponse;
import by.losik.lab6omis.dto.LanguageAverageAccuracyResponse;
import by.losik.lab6omis.dto.RequestFullInfoResponse;
import by.losik.lab6omis.dto.RequestWithSensorData;
import by.losik.lab6omis.dto.RequestWithSensorDataResponse;
import by.losik.lab6omis.dto.RequestWithSounds;
import by.losik.lab6omis.dto.RequestWithSoundsResponse;
import by.losik.lab6omis.dto.SearchResponse;
import by.losik.lab6omis.entities.general.enums.Language;
import by.losik.lab6omis.entities.general.types.Request;
import by.losik.lab6omis.entities.general.types.SensorData;
import by.losik.lab6omis.entities.general.types.Sound;
import by.losik.lab6omis.resource.base.BaseResource;
import by.losik.lab6omis.service.general.types.RequestService;
import by.losik.lab6omis.service.general.types.SensorDataService;
import by.losik.lab6omis.service.general.types.SoundService;
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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST ресурс для управления запросами (Request) с интеграцией звуков (Sound) и данных сенсоров (SensorData).
 * Предоставляет полный набор операций CRUD, поиска, фильтрации и статистики для запросов,
 * а также интеграцию с другими сущностями системы.
 *
 * @see Request
 * @see Sound
 * @see SensorData
 * @see RequestService
 * @see SoundService
 * @see SensorDataService
 * @author Losik Yaroslav
 * @version 1.0
 */
@Path("/api/requests")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RequestResource extends BaseResource<Request, Long, RequestService> {

    private static final Logger LOG = LoggerFactory.getLogger(RequestResource.class);

    @Inject
    private SoundService soundService;

    @Inject
    private SensorDataService sensorDataService;

    @Inject
    public RequestResource(RequestService service) {
        this.service = service;
    }

    @Override
    protected Long convertToId(String idString) {
        try {
            return Long.parseLong(idString);
        } catch (NumberFormatException e) {
            throw new BadRequestException(
                    String.format("Некорректный формат ID запроса: '%s'. Ожидается числовой идентификатор.", idString)
            );
        }
    }

    @Override
    protected Request createEntity(Request entity) {
        return service.createRequest(entity);
    }

    @Override
    protected Request getEntityById(Long id) {
        return service.getById(id);
    }

    @Override
    protected List<Request> getAllEntities() {
        return service.getAllRequests();
    }

    @Override
    protected List<Request> getEntitiesPaginated(int page, int size) {
        return service.getAllRequests(page, size);
    }

    @Override
    protected Request updateEntity(Long id, Request entity) {
        return service.updateRequest(id, entity);
    }

    @Override
    protected void deleteEntity(Long id) {
        service.deleteRequest(id);
    }

    @Override
    protected long getTotalCount() {
        return service.getTotalRequestsCount();
    }

    // =============== Методы для работы с запросами ===============

    /**
     * Получает запрос по языку.
     */
    @GET
    @Path("/language/{language}")
    public Response getByLanguage(@PathParam("language") String languageStr) {
        LOG.debug("Получение запросов по языку: {}", languageStr);

        try {
            Language language = Language.valueOf(languageStr.toUpperCase());
            List<Request> requests = service.executeWithLogging(
                    String.format("Получение запросов по языку через REST: %s", language),
                    () -> service.getByLanguage(language)
            );
            return Response.ok(new SearchResponse<>(requests)).build();

        } catch (IllegalArgumentException e) {
            LOG.warn("Некорректный язык: {}", languageStr);
            throw new BadRequestException(
                    String.format("Некорректный язык: '%s'. Допустимые значения: %s",
                            languageStr, Arrays.toString(Language.values()))
            );
        }
    }

    /**
     * Получает запросы с точностью распознавания выше указанного значения.
     */
    @GET
    @Path("/accuracy/gt/{minAccuracy}")
    public Response getByRecognitionAccuracyGreaterThan(@PathParam("minAccuracy") Double minAccuracy) {
        LOG.debug("Поиск запросов с точностью выше: {}", minAccuracy);

        List<Request> requests = service.executeWithLogging(
                String.format("Поиск запросов с точностью выше %.2f через REST", minAccuracy),
                () -> service.getByRecognitionAccuracyGreaterThan(minAccuracy)
        );
        return Response.ok(new SearchResponse<>(requests)).build();
    }

    /**
     * Получает запросы с точностью распознавания в диапазоне.
     */
    @GET
    @Path("/accuracy/between/{minAccuracy}/{maxAccuracy}")
    public Response getByRecognitionAccuracyBetween(
            @PathParam("minAccuracy") Double minAccuracy,
            @PathParam("maxAccuracy") Double maxAccuracy) {

        LOG.debug("Поиск запросов с точностью от {} до {}", minAccuracy, maxAccuracy);

        List<Request> requests = service.executeWithLogging(
                String.format("Поиск запросов с точностью от %.2f до %.2f через REST", minAccuracy, maxAccuracy),
                () -> service.getByRecognitionAccuracyBetween(minAccuracy, maxAccuracy)
        );
        return Response.ok(new SearchResponse<>(requests)).build();
    }

    /**
     * Получает запросы по паттерну цели.
     */
    @GET
    @Path("/goal/pattern/{pattern}")
    public Response getByGoalPattern(@PathParam("pattern") String pattern) {
        LOG.debug("Поиск запросов по паттерну цели: {}", pattern);

        List<Request> requests = service.executeWithLogging(
                String.format("Поиск запросов по паттерну цели через REST: '%s'", pattern),
                () -> service.getByGoalPattern(pattern)
        );
        return Response.ok(new SearchResponse<>(requests)).build();
    }

    /**
     * Получает запросы по языку с сортировкой по точности распознавания (по убыванию).
     */
    @GET
    @Path("/language/{language}/sorted/accuracy-desc")
    public Response getByLanguageOrderByAccuracyDesc(@PathParam("language") String languageStr) {
        LOG.debug("Получение запросов на языке {} с сортировкой по точности (убыв.)", languageStr);

        try {
            Language language = Language.valueOf(languageStr.toUpperCase());
            List<Request> requests = service.executeWithLogging(
                    String.format("Получение запросов по языку с сортировкой по точности через REST: %s", language),
                    () -> service.getByLanguageOrderByAccuracyDesc(language)
            );
            return Response.ok(new SearchResponse<>(requests)).build();

        } catch (IllegalArgumentException e) {
            LOG.warn("Некорректный язык: {}", languageStr);
            throw new BadRequestException(
                    String.format("Некорректный язык: '%s'. Допустимые значения: %s",
                            languageStr, Arrays.toString(Language.values()))
            );
        }
    }

    /**
     * Рассчитывает среднюю точность распознавания всех запросов.
     */
    @GET
    @Path("/stats/average-accuracy")
    public Response getAverageRecognitionAccuracy() {
        LOG.debug("Расчет средней точности распознавания всех запросов");

        Double averageAccuracy = service.executeWithLogging(
                "Расчет средней точности распознавания через REST",
                service::getAverageRecognitionAccuracy
        );
        return Response.ok(new AverageAccuracyResponse(averageAccuracy)).build();
    }

    /**
     * Рассчитывает среднюю точность распознавания по указанному языку.
     */
    @GET
    @Path("/stats/average-accuracy/language/{language}")
    public Response getAverageRecognitionAccuracyByLanguage(@PathParam("language") String languageStr) {
        LOG.debug("Расчет средней точности распознавания для языка: {}", languageStr);

        try {
            Language language = Language.valueOf(languageStr.toUpperCase());
            Double averageAccuracy = service.executeWithLogging(
                    String.format("Расчет средней точности распознавания для языка через REST: %s", language),
                    () -> service.getAverageRecognitionAccuracyByLanguage(language)
            );
            return Response.ok(new LanguageAverageAccuracyResponse(language, averageAccuracy)).build();

        } catch (IllegalArgumentException e) {
            LOG.warn("Некорректный язык: {}", languageStr);
            throw new BadRequestException(
                    String.format("Некорректный язык: '%s'. Допустимые значения: %s",
                            languageStr, Arrays.toString(Language.values()))
            );
        }
    }

    /**
     * Получает статистику количества запросов по языкам.
     */
    @GET
    @Path("/stats/count-by-language")
    public Response getRequestsCountByLanguage() {
        LOG.debug("Получение статистики количества запросов по языкам");

        Map<Language, Long> stats = service.executeWithLogging(
                "Получение статистики запросов по языкам через REST",
                service::getRequestsCountByLanguage
        );
        return Response.ok(stats).build();
    }

    /**
     * Получает запросы с максимальной точностью распознавания.
     */
    @GET
    @Path("/top-accuracy")
    public Response getTopAccuracyRequests() {
        LOG.debug("Получение запросов с максимальной точностью");

        List<Request> requests = service.executeWithLogging(
                "Получение запросов с максимальной точностью через REST",
                service::getTopAccuracyRequests
        );
        return Response.ok(new SearchResponse<>(requests)).build();
    }

    /**
     * Получает запросы с минимальной точностью распознавания.
     */
    @GET
    @Path("/bottom-accuracy")
    public Response getBottomAccuracyRequests() {
        LOG.debug("Получение запросов с минимальной точностью");

        List<Request> requests = service.executeWithLogging(
                "Получение запросов с минимальной точностью через REST",
                service::getBottomAccuracyRequests
        );
        return Response.ok(new SearchResponse<>(requests)).build();
    }

    /**
     * Проверяет существование запроса с указанной целью.
     */
    @GET
    @Path("/exists/goal/{goal}")
    public Response existsByGoal(@PathParam("goal") String goal) {
        LOG.debug("Проверка существования запроса с целью: {}", goal);

        boolean exists = service.executeWithLogging(
                String.format("Проверка существования запроса с целью через REST: '%s'", goal),
                () -> service.existsByGoal(goal)
        );
        return Response.ok(new ExistsResponse(exists)).build();
    }

    /**
     * Получает запрос по точному совпадению цели.
     */
    @GET
    @Path("/goal/exact/{goal}")
    public Response getByExactGoal(@PathParam("goal") String goal) {
        LOG.debug("Поиск запроса по точной цели: {}", goal);

        Request request = service.executeWithLogging(
                String.format("Поиск запроса по точной цели через REST: '%s'", goal),
                () -> service.getByGoal(goal)
        );
        return Response.ok(request).build();
    }

    /**
     * Ищет запросы по частичному совпадению цели.
     */
    @GET
    @Path("/search/goal")
    public Response searchByGoal(@QueryParam("q") String searchText) {
        LOG.debug("Поиск запросов по цели: {}", searchText);

        List<Request> requests = service.executeWithLogging(
                String.format("Поиск запросов по цели через REST: '%s'", searchText),
                () -> service.searchByGoal(searchText)
        );
        return Response.ok(new SearchResponse<>(requests)).build();
    }

    /**
     * Удаляет все запросы на указанном языке.
     */
    @DELETE
    @Path("/language/{language}")
    public Response deleteByLanguage(@PathParam("language") String languageStr) {
        LOG.debug("Удаление всех запросов на языке: {}", languageStr);

        try {
            Language language = Language.valueOf(languageStr.toUpperCase());
            int deletedCount = service.executeWithLogging(
                    String.format("Удаление всех запросов на языке через REST: %s", language),
                    () -> service.deleteByLanguage(language)
            );
            return Response.ok(new DeleteResponse(deletedCount)).build();

        } catch (IllegalArgumentException e) {
            LOG.warn("Некорректный язык: {}", languageStr);
            throw new BadRequestException(
                    String.format("Некорректный язык: '%s'. Допустимые значения: %s",
                            languageStr, Arrays.toString(Language.values()))
            );
        }
    }

    /**
     * Проверяет, является ли запрос новым.
     */
    @POST
    @Path("/check-new")
    public Response checkIfNew(@Valid Request request) {
        LOG.debug("Проверка, является ли запрос новым");

        boolean isNew = service.executeWithLogging(
                "Проверка нового запроса через REST",
                () -> service.isNewRequest(request)
        );
        return Response.ok(new IsNewResponse(isNew)).build();
    }

    // =============== Методы для интеграции со звуками ===============

    /**
     * Создает запрос вместе со связанными звуками.
     */
    @POST
    @Path("/with-sounds")
    public Response createRequestWithSounds(@Valid RequestWithSounds requestWithSounds) {
        LOG.debug("Создание запроса с связанными звуками");

        RequestWithSoundsResponse result = service.executeWithLogging(
                "Создание запроса с звуками через REST",
                () -> {
                    // Создаем запрос
                    Request request = service.createRequest(requestWithSounds.getRequest());

                    // Создаем звуки
                    List<Sound> createdSounds = new ArrayList<>();
                    for (Sound sound : requestWithSounds.getSounds()) {
                        Sound createdSound = soundService.createSound(sound);
                        createdSounds.add(createdSound);
                    }

                    return new RequestWithSoundsResponse(request, createdSounds);
                }
        );

        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    /**
     * Получает запрос вместе со связанными звуками.
     */
    @GET
    @Path("/{id}/with-sounds")
    public Response getRequestWithSounds(@PathParam("id") String idString) {
        LOG.debug("Получение запроса с звуками по ID: {}", idString);

        Long id = convertToId(idString);
        RequestWithSoundsResponse result = service.executeWithLogging(
                String.format("Получение запроса с звуками через REST: %d", id),
                () -> {
                    Request request = service.getById(id);
                    // В этом примере предполагаем, что звуки связаны через цели/шаблоны
                    // Можно адаптировать логику связи под вашу бизнес-логику
                    List<Sound> sounds = findRelatedSounds(request);
                    return new RequestWithSoundsResponse(request, sounds);
                }
        );

        return Response.ok(result).build();
    }

    /**
     * Ищет звуки, связанные с запросами по точности.
     */
    @GET
    @Path("/with-sounds/by-accuracy")
    public Response getRequestsWithSoundsByAccuracy(
            @QueryParam("minAccuracy") @DefaultValue("0.0") Double minAccuracy,
            @QueryParam("maxAccuracy") @DefaultValue("100.0") Double maxAccuracy) {

        LOG.debug("Поиск запросов с звуками по точности от {} до {}", minAccuracy, maxAccuracy);

        List<RequestWithSoundsResponse> result = service.executeWithLogging(
                String.format("Поиск запросов с звуками по точности через REST: %.2f-%.2f", minAccuracy, maxAccuracy),
                () -> {
                    List<Request> requests = service.getByRecognitionAccuracyBetween(minAccuracy, maxAccuracy);
                    return requests.stream()
                            .map(request -> {
                                List<Sound> sounds = findRelatedSounds(request);
                                return new RequestWithSoundsResponse(request, sounds);
                            })
                            .collect(Collectors.toList());
                }
        );

        return Response.ok(new SearchResponse<>(result)).build();
    }

    // =============== Методы для интеграции с данными сенсоров ===============

    /**
     * Создает запрос вместе с данными сенсоров.
     */
    @POST
    @Path("/with-sensor-data")
    public Response createRequestWithSensorData(@Valid RequestWithSensorData requestWithSensorData) {
        LOG.debug("Создание запроса с данными сенсоров");

        RequestWithSensorDataResponse result = service.executeWithLogging(
                "Создание запроса с данными сенсоров через REST",
                () -> {
                    Request request = service.createRequest(requestWithSensorData.getRequest());
                    List<SensorData> createdSensorData = new ArrayList<>();

                    for (SensorData sensorData : requestWithSensorData.getSensorDataList()) {
                        SensorData created = sensorDataService.createSensorData(sensorData);
                        createdSensorData.add(created);
                    }

                    return new RequestWithSensorDataResponse(request, createdSensorData);
                }
        );

        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    /**
     * Получает запрос вместе с данными сенсоров.
     */
    @GET
    @Path("/{id}/with-sensor-data")
    public Response getRequestWithSensorData(@PathParam("id") String idString) {
        LOG.debug("Получение запроса с данными сенсоров по ID: {}", idString);

        Long id = convertToId(idString);
        RequestWithSensorDataResponse result = service.executeWithLogging(
                String.format("Получение запроса с данными сенсоров через REST: %d", id),
                () -> {
                    Request request = service.getById(id);
                    List<SensorData> sensorData = findRelatedSensorData(request);
                    return new RequestWithSensorDataResponse(request, sensorData);
                }
        );

        return Response.ok(result).build();
    }

    /**
     * Ищет данные сенсоров по запросам с определенной точностью.
     */
    @GET
    @Path("/with-sensor-data/by-accuracy-range")
    public Response getRequestsWithSensorDataByAccuracyRange(
            @QueryParam("minAccuracy") @DefaultValue("0.0") Double minAccuracy,
            @QueryParam("maxAccuracy") @DefaultValue("100.0") Double maxAccuracy) {

        LOG.debug("Поиск запросов с данными сенсоров по точности от {} до {}", minAccuracy, maxAccuracy);

        List<RequestWithSensorDataResponse> result = service.executeWithLogging(
                String.format("Поиск запросов с данными сенсоров по точности через REST: %.2f-%.2f",
                        minAccuracy, maxAccuracy),
                () -> {
                    List<Request> requests = service.getByRecognitionAccuracyBetween(minAccuracy, maxAccuracy);
                    return requests.stream()
                            .map(request -> {
                                List<SensorData> sensorData = findRelatedSensorData(request);
                                return new RequestWithSensorDataResponse(request, sensorData);
                            })
                            .collect(Collectors.toList());
                }
        );

        return Response.ok(new SearchResponse<>(result)).build();
    }

    // =============== Комплексные методы с интеграцией всех трех сущностей ===============

    /**
     * Получает запрос со всей связанной информацией (звуки + данные сенсоров).
     */
    @GET
    @Path("/{id}/full-info")
    public Response getRequestFullInfo(@PathParam("id") String idString) {
        LOG.debug("Получение полной информации по запросу ID: {}", idString);

        Long id = convertToId(idString);
        RequestFullInfoResponse result = service.executeWithLogging(
                String.format("Получение полной информации по запросу через REST: %d", id),
                () -> {
                    Request request = service.getById(id);
                    List<Sound> sounds = findRelatedSounds(request);
                    List<SensorData> sensorData = findRelatedSensorData(request);

                    return new RequestFullInfoResponse(request, sounds, sensorData);
                }
        );

        return Response.ok(result).build();
    }

    /**
     * Создает комплексную запись: запрос + звуки + данные сенсоров.
     */
    @POST
    @Path("/full")
    public Response createFullRequest(@Valid RequestFullInfoResponse fullRequest) {
        LOG.debug("Создание комплексной записи: запрос + звуки + данные сенсоров");

        RequestFullInfoResponse result = service.executeWithLogging(
                "Создание комплексной записи через REST",
                () -> {
                    // Создаем запрос
                    Request createdRequest = service.createRequest(fullRequest.getRequest());

                    // Создаем звуки
                    List<Sound> createdSounds = new ArrayList<>();
                    for (Sound sound : fullRequest.getSounds()) {
                        Sound createdSound = soundService.createSound(sound);
                        createdSounds.add(createdSound);
                    }

                    // Создаем данные сенсоров
                    List<SensorData> createdSensorData = new ArrayList<>();
                    for (SensorData sensorData : fullRequest.getSensorDataList()) {
                        SensorData created = sensorDataService.createSensorData(sensorData);
                        createdSensorData.add(created);
                    }

                    return new RequestFullInfoResponse(createdRequest, createdSounds, createdSensorData);
                }
        );

        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    /**
     * Получает статистику по всем сущностям.
     */
    @GET
    @Path("/stats/comprehensive")
    public Response getComprehensiveStats() {
        LOG.debug("Получение комплексной статистики");

        ComprehensiveStatsResponse result = service.executeWithLogging(
                "Получение комплексной статистики через REST",
                () -> {
                    long totalRequests = service.getTotalRequestsCount();
                    long totalSounds = soundService.getTotalSoundsCount();
                    long totalSensorData = sensorDataService.getTotalSensorDataCount();

                    Double avgAccuracy = service.getAverageRecognitionAccuracy();
                    Double avgFrequency = soundService.getAverageFrequency();

                    Map<Language, Long> requestsByLanguage = service.getRequestsCountByLanguage();
                    Map<String, Double> avgFrequencyByNoise = soundService.getAverageFrequencyByNoiseType();
                    Map<Long, Long> dataCountBySensor = sensorDataService.getDataCountBySensor();

                    return new ComprehensiveStatsResponse(
                            totalRequests, totalSounds, totalSensorData,
                            avgAccuracy, avgFrequency,
                            requestsByLanguage, avgFrequencyByNoise, dataCountBySensor
                    );
                }
        );

        return Response.ok(result).build();
    }

    /**
     * Ищет запросы по комплексным критериям.
     */
    @GET
    @Path("/search/complex")
    public Response searchComplex(
            @QueryParam("goalPattern") String goalPattern,
            @QueryParam("minAccuracy") @DefaultValue("0.0") Double minAccuracy,
            @QueryParam("maxAccuracy") @DefaultValue("100.0") Double maxAccuracy,
            @QueryParam("noisePattern") String noisePattern,
            @QueryParam("sensorId") Long sensorId) {

        LOG.debug("Комплексный поиск запросов");

        List<RequestFullInfoResponse> result = service.executeWithLogging(
                "Комплексный поиск запросов через REST",
                () -> {
                    List<Request> filteredRequests = service.getAllRequests().stream()
                            .filter(request -> {
                                if (goalPattern != null && !goalPattern.isEmpty()) {
                                    if (!request.getGoal().contains(goalPattern)) {
                                        return false;
                                    }
                                }
                                return request.getRecognitionAccuracy() >= minAccuracy &&
                                        request.getRecognitionAccuracy() <= maxAccuracy;
                            })
                            .collect(Collectors.toList());

                    return filteredRequests.stream()
                            .map(request -> {
                                List<Sound> sounds = findRelatedSounds(request);
                                List<SensorData> sensorData = findRelatedSensorData(request);

                                // Дополнительная фильтрация по звукам и сенсорам
                                if (noisePattern != null && !noisePattern.isEmpty()) {
                                    sounds = sounds.stream()
                                            .filter(sound -> sound.getNoise().contains(noisePattern))
                                            .collect(Collectors.toList());
                                }

                                if (sensorId != null) {
                                    sensorData = sensorData.stream()
                                            .filter(data -> data.getSensor().getId().equals(sensorId))
                                            .collect(Collectors.toList());
                                }

                                return new RequestFullInfoResponse(request, sounds, sensorData);
                            })
                            .filter(response ->
                                    (noisePattern == null || !response.getSounds().isEmpty()) &&
                                            (sensorId == null || !response.getSensorDataList().isEmpty())
                            )
                            .collect(Collectors.toList());
                }
        );

        return Response.ok(new SearchResponse<>(result)).build();
    }

    /**
     * Находит звуки, связанные с запросом.
     * В этом примере предполагается связь по цели запроса и типу шума.
     * Можно адаптировать под вашу бизнес-логику.
     */
    private List<Sound> findRelatedSounds(Request request) {
        List<Sound> relatedSounds = new ArrayList<>();

        try {
            List<Sound> sounds = soundService.searchByNoise(request.getGoal());
            relatedSounds.addAll(sounds);
        } catch (Exception e) {
            LOG.warn("Ошибка при поиске связанных звуков для запроса {}: {}", request.getId(), e.getMessage());
        }

        return relatedSounds;
    }

    /**
     * Находит данные сенсоров, связанные с запросом.
     * В этом примере предполагается связь через цель запроса и назначение данных.
     */
    private List<SensorData> findRelatedSensorData(Request request) {
        List<SensorData> relatedData = new ArrayList<>();

        try {
            List<SensorData> sensorData = sensorDataService.searchByPurpose(request.getGoal());
            relatedData.addAll(sensorData);
        } catch (Exception e) {
            LOG.warn("Ошибка при поиске связанных данных сенсоров для запроса {}: {}", request.getId(), e.getMessage());
        }

        return relatedData;
    }

}