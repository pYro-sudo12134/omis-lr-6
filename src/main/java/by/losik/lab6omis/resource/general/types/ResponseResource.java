package by.losik.lab6omis.resource.general.types;

import by.losik.lab6omis.dto.AverageLengthResponse;
import by.losik.lab6omis.dto.CountResponse;
import by.losik.lab6omis.dto.DeleteResponse;
import by.losik.lab6omis.dto.DeleteWithResponseResponse;
import by.losik.lab6omis.dto.ExistsResponse;
import by.losik.lab6omis.dto.IsNewResponse;
import by.losik.lab6omis.dto.LanguageAverageLengthResponse;
import by.losik.lab6omis.dto.SearchResponse;
import by.losik.lab6omis.dto.SolutionWithResponse;
import by.losik.lab6omis.dto.SolutionsWithResponsesStats;
import by.losik.lab6omis.dto.SyncResponse;
import by.losik.lab6omis.entities.general.enums.Language;
import by.losik.lab6omis.entities.general.types.ResponseEntity;
import by.losik.lab6omis.entities.general.types.Solution;
import by.losik.lab6omis.resource.base.BaseResource;
import by.losik.lab6omis.service.general.types.ResponseService;
import by.losik.lab6omis.service.general.types.SolutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
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

/**
 * REST ресурс для управления решениями (Solution) с поддержкой связанных ответов (ResponseEntity).
 * Предоставляет полный набор операций CRUD, поиска, фильтрации и статистики для решений,
 * а также интеграцию с ответами.
 *
 * @see Solution
 * @see ResponseEntity
 * @see SolutionService
 * @see ResponseService
 * @author Losik Yaroslav
 * @version 1.0
 */
@Path("/api/solutions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ResponseResource extends BaseResource<Solution, Long, SolutionService> {

    private static final Logger LOG = LoggerFactory.getLogger(ResponseResource.class);

    @Inject
    private ResponseService responseService;

    @Inject
    public ResponseResource(SolutionService service) {
        this.service = service;
    }

    @Override
    protected Long convertToId(String idString) {
        try {
            return Long.parseLong(idString);
        } catch (NumberFormatException e) {
            throw new BadRequestException(
                    String.format("Некорректный формат ID решения: '%s'. Ожидается числовой идентификатор.", idString)
            );
        }
    }

    @Override
    protected Solution createEntity(Solution entity) {
        return service.createSolution(entity);
    }

    @Override
    protected Solution getEntityById(Long id) {
        return service.getById(id);
    }

    @Override
    protected List<Solution> getAllEntities() {
        return service.getAllSolutions();
    }

    @Override
    protected List<Solution> getEntitiesPaginated(int page, int size) {
        return service.getAllSolutions(page, size);
    }

    @Override
    protected Solution updateEntity(Long id, Solution entity) {
        return service.updateSolution(id, entity);
    }

    @Override
    protected void deleteEntity(Long id) {
        service.deleteSolution(id);
    }

    @Override
    protected long getTotalCount() {
        return service.getTotalSolutionsCount();
    }

    /**
     * Получает решение вместе с связанным ответом.
     */
    @GET
    @Path("/{id}/with-response")
    public Response getSolutionWithResponse(@PathParam("id") String idString) {
        LOG.debug("Получение решения с ответом по ID: {}", idString);

        Long id = convertToId(idString);
        SolutionWithResponse result = service.executeWithLogging(
                String.format("Получение решения с ответом через REST: %d", id),
                () -> {
                    Solution solution = service.getById(id);
                    ResponseEntity response = findMatchingResponse(solution);

                    return new SolutionWithResponse(
                            solution,
                            response,
                            response != null
                    );
                }
        );

        return Response.ok(result).build();
    }

    /**
     * Создает решение вместе с связанным ответом.
     */
    @POST
    @Path("/with-response")
    public Response createSolutionWithResponse(@Valid Solution solution) {
        LOG.debug("Создание решения с ответом");

        SolutionWithResponse result = service.executeWithLogging(
                "Создание решения с ответом через REST",
                () -> {
                    Solution createdSolution = service.createSolution(solution);
                    ResponseEntity createdResponse = responseService.createResponse(
                            createResponseFromSolution(createdSolution)
                    );
                    return new SolutionWithResponse(createdSolution, createdResponse, true);
                }
        );

        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    /**
     * Обновляет решение и соответствующий ответ.
     */
    @PUT
    @Path("/{id}/with-response")
    public Response updateSolutionWithResponse(@PathParam("id") String idString, @Valid Solution solution) {
        LOG.debug("Обновление решения с ответом по ID: {}", idString);

        Long id = convertToId(idString);
        SolutionWithResponse result = service.executeWithLogging(
                String.format("Обновление решения с ответом через REST: %d", id),
                () -> {
                    Solution updatedSolution = service.updateSolution(id, solution);
                    ResponseEntity existingResponse = findMatchingResponse(updatedSolution);
                    ResponseEntity updatedResponse;

                    if (existingResponse != null) {
                        updatedResponse = responseService.updateResponse(
                                existingResponse.getId(),
                                createResponseFromSolution(updatedSolution)
                        );
                    } else {
                        updatedResponse = responseService.createResponse(
                                createResponseFromSolution(updatedSolution)
                        );
                    }

                    return new SolutionWithResponse(updatedSolution, updatedResponse, true);
                }
        );

        return Response.ok(result).build();
    }

    /**
     * Удаляет решение и соответствующий ответ.
     */
    @DELETE
    @Path("/{id}/with-response")
    public Response deleteSolutionWithResponse(@PathParam("id") String idString) {
        LOG.debug("Удаление решения с ответом по ID: {}", idString);

        Long id = convertToId(idString);
        DeleteWithResponseResponse result = service.executeWithLogging(
                String.format("Удаление решения с ответом через REST: %d", id),
                () -> {
                    Solution solution = service.getById(id);
                    ResponseEntity response = findMatchingResponse(solution);
                    int deletedResponses = 0;

                    if (response != null) {
                        responseService.deleteResponse(response.getId());
                        deletedResponses = 1;
                    }

                    service.deleteSolution(id);
                    return new DeleteWithResponseResponse(1, deletedResponses);
                }
        );

        return Response.ok(result).build();
    }

    /**
     * Синхронизирует ответ с решением.
     */
    @POST
    @Path("/{id}/sync-response")
    public Response syncResponseWithSolution(@PathParam("id") String idString) {
        LOG.debug("Синхронизация ответа с решением ID: {}", idString);

        Long id = convertToId(idString);
        SyncResponse result = service.executeWithLogging(
                String.format("Синхронизация ответа с решением через REST: %d", id),
                () -> {
                    Solution solution = service.getById(id);
                    ResponseEntity existingResponse = findMatchingResponse(solution);
                    boolean created = false;
                    ResponseEntity response;

                    if (existingResponse != null) {
                        response = responseService.updateResponse(
                                existingResponse.getId(),
                                createResponseFromSolution(solution)
                        );
                    } else {
                        response = responseService.createResponse(
                                createResponseFromSolution(solution)
                        );
                        created = true;
                    }

                    return new SyncResponse(solution, response, created);
                }
        );

        return Response.ok(result).build();
    }

    /**
     * Получает решения по языку.
     */
    @GET
    @Path("/language/{language}")
    public Response getByLanguage(@PathParam("language") String languageStr) {
        LOG.debug("Получение решений по языку: {}", languageStr);

        try {
            Language language = Language.valueOf(languageStr.toUpperCase());
            List<Solution> solutions = service.executeWithLogging(
                    String.format("Получение решений по языку через REST: %s", language),
                    () -> service.getByLanguage(language)
            );
            return Response.ok(new SearchResponse<>(solutions)).build();

        } catch (IllegalArgumentException e) {
            LOG.warn("Некорректный язык: {}", languageStr);
            throw new BadRequestException(
                    String.format("Некорректный язык: '%s'. Допустимые значения: %s",
                            languageStr, Arrays.toString(Language.values()))
            );
        }
    }

    /**
     * Получает решения по языку с пагинацией.
     */
    @GET
    @Path("/language/{language}/paginated")
    public Response getByLanguageWithPagination(
            @PathParam("language") String languageStr,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        LOG.debug("Получение решений на языке {} с пагинацией: page={}, size={}", languageStr, page, size);

        try {
            Language language = Language.valueOf(languageStr.toUpperCase());
            List<Solution> solutions = service.executeWithLogging(
                    String.format("Получение решений по языку с пагинацией через REST: %s, page=%d, size=%d",
                            language, page, size),
                    () -> service.getByLanguageWithPagination(language, page, size)
            );
            return Response.ok(new SearchResponse<>(solutions)).build();

        } catch (IllegalArgumentException e) {
            LOG.warn("Некорректный язык: {}", languageStr);
            throw new BadRequestException(
                    String.format("Некорректный язык: '%s'. Допустимые значения: %s",
                            languageStr, Arrays.toString(Language.values()))
            );
        }
    }

    /**
     * Ищет решения по частичному совпадению сообщения.
     */
    @GET
    @Path("/search/message")
    public Response searchByMessage(@QueryParam("q") String searchText) {
        LOG.debug("Поиск решений по сообщению: {}", searchText);

        List<Solution> solutions = service.executeWithLogging(
                String.format("Поиск решений по сообщению через REST: '%s'", searchText),
                () -> service.searchByMessage(searchText)
        );
        return Response.ok(new SearchResponse<>(solutions)).build();
    }

    /**
     * Ищет решения по ключевым словам.
     */
    @GET
    @Path("/search/keywords")
    public Response searchByKeywords(@QueryParam("keywords") String keywordsStr) {
        LOG.debug("Поиск решений по ключевым словам: {}", keywordsStr);

        if (keywordsStr == null || keywordsStr.trim().isEmpty()) {
            throw new BadRequestException("Параметр 'keywords' не может быть пустым");
        }

        List<String> keywords = Arrays.asList(keywordsStr.split(","));
        List<Solution> solutions = service.executeWithLogging(
                String.format("Поиск решений по ключевым словам через REST: %s", keywords),
                () -> service.searchByKeywords(keywords)
        );
        return Response.ok(new SearchResponse<>(solutions)).build();
    }

    /**
     * Получает решения с сообщениями в указанном диапазоне длины.
     */
    @GET
    @Path("/length/between/{minLength}/{maxLength}")
    public Response getByMessageLengthBetween(
            @PathParam("minLength") int minLength,
            @PathParam("maxLength") int maxLength) {

        LOG.debug("Поиск решений с длиной сообщения от {} до {} символов", minLength, maxLength);

        List<Solution> solutions = service.executeWithLogging(
                String.format("Поиск решений с длиной сообщения через REST: %d-%d", minLength, maxLength),
                () -> service.getByMessageLengthBetween(minLength, maxLength)
        );
        return Response.ok(new SearchResponse<>(solutions)).build();
    }

    /**
     * Получает решения с короткими сообщениями.
     */
    @GET
    @Path("/short/{maxLength}")
    public Response getShortMessages(@PathParam("maxLength") int maxLength) {
        LOG.debug("Поиск решений с сообщениями короче {} символов", maxLength);

        List<Solution> solutions = service.executeWithLogging(
                String.format("Поиск решений с короткими сообщениями через REST: < %d", maxLength),
                () -> service.getShortMessages(maxLength)
        );
        return Response.ok(new SearchResponse<>(solutions)).build();
    }

    /**
     * Получает решения с длинными сообщениями.
     */
    @GET
    @Path("/long/{minLength}")
    public Response getLongMessages(@PathParam("minLength") int minLength) {
        LOG.debug("Поиск решений с сообщениями длиннее {} символов", minLength);

        List<Solution> solutions = service.executeWithLogging(
                String.format("Поиск решений с длинными сообщениями через REST: > %d", minLength),
                () -> service.getLongMessages(minLength)
        );
        return Response.ok(new SearchResponse<>(solutions)).build();
    }

    /**
     * Получает решения, сообщения которых начинаются с указанного префикса.
     */
    @GET
    @Path("/starts-with/{prefix}")
    public Response getByMessageStartingWith(@PathParam("prefix") String prefix) {
        LOG.debug("Поиск решений по префиксу сообщения: {}", prefix);

        List<Solution> solutions = service.executeWithLogging(
                String.format("Поиск решений по префиксу сообщения через REST: '%s'", prefix),
                () -> service.getByMessageStartingWith(prefix)
        );
        return Response.ok(new SearchResponse<>(solutions)).build();
    }

    /**
     * Получает решения, сообщения которых заканчиваются на указанный суффикс.
     */
    @GET
    @Path("/ends-with/{suffix}")
    public Response getByMessageEndingWith(@PathParam("suffix") String suffix) {
        LOG.debug("Поиск решений по суффиксу сообщения: {}", suffix);

        List<Solution> solutions = service.executeWithLogging(
                String.format("Поиск решений по суффиксу сообщения через REST: '%s'", suffix),
                () -> service.getByMessageEndingWith(suffix)
        );
        return Response.ok(new SearchResponse<>(solutions)).build();
    }

    /**
     * Получает решения по нескольким языкам.
     */
    @GET
    @Path("/languages/{languages}")
    public Response getByLanguages(@PathParam("languages") String languagesStr) {
        LOG.debug("Получение решений по языкам: {}", languagesStr);

        List<Language> languages = parseLanguages(languagesStr);
        List<Solution> solutions = service.executeWithLogging(
                String.format("Получение решений по языкам через REST: %s", languages),
                () -> service.getByLanguages(languages)
        );
        return Response.ok(new SearchResponse<>(solutions)).build();
    }

    /**
     * Получает решения с сообщениями точной длины.
     */
    @GET
    @Path("/length/exact/{exactLength}")
    public Response getByMessageLength(@PathParam("exactLength") int exactLength) {
        LOG.debug("Получение решений с точной длиной сообщения: {} символов", exactLength);

        List<Solution> solutions = service.executeWithLogging(
                String.format("Получение решений с точной длиной сообщения через REST: %d", exactLength),
                () -> service.getByMessageLength(exactLength)
        );
        return Response.ok(new SearchResponse<>(solutions)).build();
    }

    /**
     * Получает решения по языку с сортировкой по длине сообщения.
     */
    @GET
    @Path("/language/{language}/sorted/length")
    public Response getByLanguageOrderByMessageLength(
            @PathParam("language") String languageStr,
            @QueryParam("ascending") @DefaultValue("true") boolean ascending) {

        LOG.debug("Получение решений на языке {} с сортировкой по длине (ascending={})", languageStr, ascending);

        try {
            Language language = Language.valueOf(languageStr.toUpperCase());
            List<Solution> solutions = service.executeWithLogging(
                    String.format("Получение решений по языку с сортировкой через REST: %s, ascending=%s",
                            language, ascending),
                    () -> service.getByLanguageOrderByMessageLength(language, ascending)
            );
            return Response.ok(new SearchResponse<>(solutions)).build();

        } catch (IllegalArgumentException e) {
            LOG.warn("Некорректный язык: {}", languageStr);
            throw new BadRequestException(
                    String.format("Некорректный язык: '%s'. Допустимые значения: %s",
                            languageStr, Arrays.toString(Language.values()))
            );
        }
    }

    /**
     * Получает статистику количества решений по языкам.
     */
    @GET
    @Path("/stats/count-by-language")
    public Response getSolutionsCountByLanguage() {
        LOG.debug("Получение статистики количества решений по языкам");

        Map<Language, Long> stats = service.executeWithLogging(
                "Получение статистики решений по языкам через REST",
                service::getSolutionsCountByLanguage
        );
        return Response.ok(stats).build();
    }

    /**
     * Рассчитывает среднюю длину сообщений всех решений.
     */
    @GET
    @Path("/stats/average-length")
    public Response getAverageMessageLength() {
        LOG.debug("Расчет средней длины сообщений всех решений");

        Double averageLength = service.executeWithLogging(
                "Расчет средней длины сообщений решений через REST",
                service::getAverageMessageLength
        );
        return Response.ok(new AverageLengthResponse(averageLength)).build();
    }

    /**
     * Рассчитывает среднюю длину сообщений по указанному языку.
     */
    @GET
    @Path("/stats/average-length/language/{language}")
    public Response getAverageMessageLengthByLanguage(@PathParam("language") String languageStr) {
        LOG.debug("Расчет средней длины сообщений для языка: {}", languageStr);

        try {
            Language language = Language.valueOf(languageStr.toUpperCase());
            Double averageLength = service.executeWithLogging(
                    String.format("Расчет средней длины сообщений для языка через REST: %s", language),
                    () -> service.getAverageMessageLengthByLanguage(language)
            );
            return Response.ok(new LanguageAverageLengthResponse(language, averageLength)).build();

        } catch (IllegalArgumentException e) {
            LOG.warn("Некорректный язык: {}", languageStr);
            throw new BadRequestException(
                    String.format("Некорректный язык: '%s'. Допустимые значения: %s",
                            languageStr, Arrays.toString(Language.values()))
            );
        }
    }

    /**
     * Получает самые короткие решения в системе.
     */
    @GET
    @Path("/shortest")
    public Response getShortestSolutions() {
        LOG.debug("Получение самых коротких решений");

        List<Solution> solutions = service.executeWithLogging(
                "Получение самых коротких решений через REST",
                service::getShortestSolutions
        );
        return Response.ok(new SearchResponse<>(solutions)).build();
    }

    /**
     * Получает самые длинные решения в системе.
     */
    @GET
    @Path("/longest")
    public Response getLongestSolutions() {
        LOG.debug("Получение самых длинных решений");

        List<Solution> solutions = service.executeWithLogging(
                "Получение самых длинных решений через REST",
                service::getLongestSolutions
        );
        return Response.ok(new SearchResponse<>(solutions)).build();
    }

    /**
     * Проверяет существование решения с указанным сообщением.
     */
    @GET
    @Path("/exists/message/{message}")
    public Response existsByMessage(@PathParam("message") String message) {
        LOG.debug("Проверка существования решения с сообщением: {}", message);

        boolean exists = service.executeWithLogging(
                String.format("Проверка существования решения с сообщением через REST: '%s'", message),
                () -> service.existsByMessage(message)
        );
        return Response.ok(new ExistsResponse(exists)).build();
    }

    /**
     * Получает количество решений на указанном языке.
     */
    @GET
    @Path("/count/language/{language}")
    public Response countByLanguage(@PathParam("language") String languageStr) {
        LOG.debug("Подсчет количества решений на языке: {}", languageStr);

        try {
            Language language = Language.valueOf(languageStr.toUpperCase());
            Long count = service.executeWithLogging(
                    String.format("Подсчет количества решений на языке через REST: %s", language),
                    () -> service.countByLanguage(language)
            );
            return Response.ok(new CountResponse(count != null ? count : 0L)).build();

        } catch (IllegalArgumentException e) {
            LOG.warn("Некорректный язык: {}", languageStr);
            throw new BadRequestException(
                    String.format("Некорректный язык: '%s'. Допустимые значения: %s",
                            languageStr, Arrays.toString(Language.values()))
            );
        }
    }

    /**
     * Проверяет, является ли решение новым.
     */
    @POST
    @Path("/check-new")
    public Response checkIfNew(@Valid Solution solution) {
        LOG.debug("Проверка, является ли решение новым");

        boolean isNew = service.executeWithLogging(
                "Проверка нового решения через REST",
                () -> service.isNewSolution(solution)
        );
        return Response.ok(new IsNewResponse(isNew)).build();
    }

    /**
     * Удаляет все решения на указанном языке.
     */
    @DELETE
    @Path("/language/{language}")
    public Response deleteByLanguage(@PathParam("language") String languageStr) {
        LOG.debug("Удаление всех решений на языке: {}", languageStr);

        try {
            Language language = Language.valueOf(languageStr.toUpperCase());
            int deletedCount = service.executeWithLogging(
                    String.format("Удаление всех решений на языке через REST: %s", language),
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
     * Получает решение по точному совпадению сообщения.
     */
    @GET
    @Path("/message/exact/{message}")
    public Response getByExactMessage(@PathParam("message") String message) {
        LOG.debug("Поиск решения по точному сообщению: {}", message);

        Solution solution = service.executeWithLogging(
                String.format("Поиск решения по точному сообщению через REST: '%s'", message),
                () -> service.getByMessage(message)
                        .orElseThrow(() -> new NotFoundException(
                                String.format("Решение с сообщением '%s' не найдено", message)
                        ))
        );
        return Response.ok(solution).build();
    }

    /**
     * Получает все решения с их ответами.
     */
    @GET
    @Path("/with-response")
    public Response getAllSolutionsWithResponses() {
        LOG.debug("Получение всех решений с ответами");

        List<SolutionWithResponse> result = service.executeWithLogging(
                "Получение всех решений с ответами через REST",
                () -> {
                    List<Solution> solutions = service.getAllSolutions();
                    return mapSolutionsToResponseList(solutions);
                }
        );
        return Response.ok(new SearchResponse<>(result)).build();
    }

    /**
     * Получает решения с ответами по языку.
     */
    @GET
    @Path("/with-response/language/{language}")
    public Response getSolutionsWithResponsesByLanguage(@PathParam("language") String languageStr) {
        LOG.debug("Получение решений с ответами по языку: {}", languageStr);

        try {
            Language language = Language.valueOf(languageStr.toUpperCase());
            List<SolutionWithResponse> result = service.executeWithLogging(
                    String.format("Получение решений с ответами по языку через REST: %s", language),
                    () -> {
                        List<Solution> solutions = service.getByLanguage(language);
                        return mapSolutionsToResponseList(solutions);
                    }
            );
            return Response.ok(new SearchResponse<>(result)).build();

        } catch (IllegalArgumentException e) {
            LOG.warn("Некорректный язык: {}", languageStr);
            throw new BadRequestException(
                    String.format("Некорректный язык: '%s'. Допустимые значения: %s",
                            languageStr, Arrays.toString(Language.values()))
            );
        }
    }

    /**
     * Ищет решения по сообщению и возвращает с ответами.
     */
    @GET
    @Path("/with-response/search/message")
    public Response searchSolutionsWithResponsesByMessage(@QueryParam("q") String searchText) {
        LOG.debug("Поиск решений с ответами по сообщению: {}", searchText);

        List<SolutionWithResponse> result = service.executeWithLogging(
                String.format("Поиск решений с ответами по сообщению через REST: '%s'", searchText),
                () -> {
                    List<Solution> solutions = service.searchByMessage(searchText);
                    return mapSolutionsToResponseList(solutions);
                }
        );
        return Response.ok(new SearchResponse<>(result)).build();
    }

    /**
     * Получает статистику по решениям и ответам.
     */
    @GET
    @Path("/stats/with-response")
    public Response getSolutionsWithResponsesStats() {
        LOG.debug("Получение статистики по решениям и ответам");

        SolutionsWithResponsesStats result = service.executeWithLogging(
                "Получение статистики по решениям и ответам через REST",
                () -> {
                    long totalSolutions = service.getTotalSolutionsCount();
                    long totalResponses = responseService.getTotalResponsesCount();
                    List<Solution> allSolutions = service.getAllSolutions();
                    List<Solution> solutionsWithoutResponses = new ArrayList<>();

                    for (Solution solution : allSolutions) {
                        if (findMatchingResponse(solution) == null) {
                            solutionsWithoutResponses.add(solution);
                        }
                    }

                    return new SolutionsWithResponsesStats(
                            totalSolutions,
                            totalResponses,
                            solutionsWithoutResponses.size(),
                            solutionsWithoutResponses
                    );
                }
        );
        return Response.ok(result).build();
    }

    private ResponseEntity findMatchingResponse(@NotNull Solution solution) {
        try {
            return responseService.getByMessage(solution.getMessage());
        } catch (NotFoundException e) {
            return null;
        }
    }

    private ResponseEntity createResponseFromSolution(@NotNull Solution solution) {
        ResponseEntity response = new ResponseEntity();
        response.setLanguage(solution.getLanguage());
        response.setMessage(solution.getMessage());
        return response;
    }

    private List<SolutionWithResponse> mapSolutionsToResponseList(@NotNull List<Solution> solutions) {
        List<SolutionWithResponse> result = new ArrayList<>();
        for (Solution solution : solutions) {
            ResponseEntity response = findMatchingResponse(solution);
            result.add(new SolutionWithResponse(solution, response, response != null));
        }
        return result;
    }

    private List<Language> parseLanguages(@NotNull String languagesStr) {
        if (languagesStr == null || languagesStr.trim().isEmpty()) {
            throw new BadRequestException("Список языков не может быть пустым");
        }

        String[] languageStrings = languagesStr.split(",");
        List<Language> languages = new ArrayList<>();

        for (String langStr : languageStrings) {
            try {
                Language language = Language.valueOf(langStr.trim().toUpperCase());
                languages.add(language);
            } catch (IllegalArgumentException e) {
                LOG.warn("Некорректный язык в списке: {}", langStr);
                throw new BadRequestException(
                        String.format("Некорректный язык: '%s'. Допустимые значения: %s",
                                langStr, Arrays.toString(Language.values()))
                );
            }
        }

        return languages;
    }
}