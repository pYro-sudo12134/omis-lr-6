package by.losik.lab6omis.resource.general.types;

import by.losik.lab6omis.entities.general.enums.Language;
import by.losik.lab6omis.entities.general.types.Solution;
import by.losik.lab6omis.resource.base.BaseResource;
import by.losik.lab6omis.service.general.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST ресурс для работы с решениями (Solution).
 * Использует базовый ресурс для основных CRUD операций,
 * а также предоставляет расширенные эндпоинты для работы с командами и бизнес-логикой.
 */
@Path("/solutions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SolutionResource extends BaseResource<Solution, Long, SolutionService> {

    private static final Logger LOG = LoggerFactory.getLogger(SolutionResource.class);

    @Inject
    private AnalysisCommand analysisCommand;

    @Inject
    private SolutionCommand solutionCommand;

    @Inject
    private ResponseCommand responseCommand;

    @Inject
    public SolutionResource(SolutionService solutionService) {
        this.service = solutionService;
    }

    /**
     * Конвертация строки в Long ID.
     */
    @Override
    protected Long convertToId(String idString) {
        try {
            return Long.parseLong(idString);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Неверный формат ID. Ожидается число.");
        }
    }

    /**
     * Создание сущности (абстрактный метод из BaseResource).
     */
    @Override
    protected Solution createEntity(Solution entity) {
        return service.createSolution(entity);
    }

    /**
     * Получение сущности по ID (абстрактный метод из BaseResource).
     */
    @Override
    protected Solution getEntityById(Long id) {
        return service.getById(id);
    }

    /**
     * Получение всех сущностей (абстрактный метод из BaseResource).
     */
    @Override
    protected List<Solution> getAllEntities() {
        return service.getAllSolutions();
    }

    /**
     * Получение сущностей с пагинацией (абстрактный метод из BaseResource).
     */
    @Override
    protected List<Solution> getEntitiesPaginated(int page, int size) {
        return service.getAllSolutions(page, size);
    }

    /**
     * Обновление сущности (абстрактный метод из BaseResource).
     */
    @Override
    protected Solution updateEntity(Long id, Solution entity) {
        return service.updateSolution(id, entity);
    }

    /**
     * Удаление сущности (абстрактный метод из BaseResource).
     */
    @Override
    protected void deleteEntity(Long id) {
        service.deleteSolution(id);
    }

    /**
     * Получение общего количества (абстрактный метод из BaseResource).
     */
    @Override
    protected long getTotalCount() {
        return service.getTotalSolutionsCount();
    }

    /**
     * Получение решений по языку.
     */
    @GET
    @Path("/language/{language}")
    public Response getByLanguage(@PathParam("language") Language language) {
        LOG.debug("Получение решений по языку: {}", language);
        List<Solution> solutions = service.getByLanguage(language);
        return Response.ok(solutions).build();
    }

    /**
     * Поиск решений по части сообщения.
     */
    @GET
    @Path("/search")
    public Response searchByMessage(@QueryParam("q") String searchText) {
        LOG.debug("Поиск решений по сообщению: {}", searchText);
        List<Solution> solutions = service.searchByMessage(searchText);
        return Response.ok(solutions).build();
    }

    /**
     * Получение статистики по языкам.
     */
    @GET
    @Path("/stats/language")
    public Response getSolutionsCountByLanguage() {
        LOG.debug("Получение статистики решений по языкам");
        Map<Language, Long> stats = service.getSolutionsCountByLanguage();
        return Response.ok(stats).build();
    }

    /**
     * Получение средней длины сообщений.
     */
    @GET
    @Path("/stats/avg-length")
    public Response getAverageMessageLength() {
        LOG.debug("Получение средней длины сообщений");
        Double avgLength = service.getAverageMessageLength();
        return Response.ok(Map.of("averageLength", avgLength)).build();
    }

    /**
     * Получение самых коротких решений.
     */
    @GET
    @Path("/shortest")
    public Response getShortestSolutions() {
        LOG.debug("Получение самых коротких решений");
        List<Solution> solutions = service.getShortestSolutions();
        return Response.ok(solutions).build();
    }

    /**
     * Получение самых длинных решений.
     */
    @GET
    @Path("/longest")
    public Response getLongestSolutions() {
        LOG.debug("Получение самых длинных решений");
        List<Solution> solutions = service.getLongestSolutions();
        return Response.ok(solutions).build();
    }

    /**
     * Выполнение команды анализа.
     */
    @POST
    @Path("/commands/analyze")
    public Response executeAnalysisCommand() {
        LOG.info("Выполнение команды анализа через REST");
        analysisCommand.call();
        return Response.ok(Map.of("message", "Команда анализа выполнена успешно")).build();
    }

    /**
     * Отмена команды анализа.
     */
    @DELETE
    @Path("/commands/analyze")
    public Response cancelAnalysisCommand() {
        LOG.info("Отмена команды анализа через REST");
        analysisCommand.cancel();
        return Response.ok(Map.of("message", "Команда анализа отменена")).build();
    }

    /**
     * Выполнение команды решения.
     */
    @POST
    @Path("/commands/solution")
    public Response executeSolutionCommand() {
        LOG.info("Выполнение команды решения через REST");
        solutionCommand.call();
        return Response.ok(Map.of("message", "Команда решения выполнена успешно")).build();
    }

    /**
     * Отмена команды решения.
     */
    @DELETE
    @Path("/commands/solution")
    public Response cancelSolutionCommand() {
        LOG.info("Отмена команды решения через REST");
        solutionCommand.cancel();
        return Response.ok(Map.of("message", "Команда решения отменена")).build();
    }

    /**
     * Выполнение команды ответа.
     */
    @POST
    @Path("/commands/response")
    public Response executeResponseCommand() {
        LOG.info("Выполнение команды ответа через REST");
        responseCommand.call();
        return Response.ok(Map.of("message", "Команда ответа выполнена успешно")).build();
    }

    /**
     * Отмена команды ответа.
     */
    @DELETE
    @Path("/commands/response")
    public Response cancelResponseCommand() {
        LOG.info("Отмена команды ответа через REST");
        responseCommand.cancel();
        return Response.ok(Map.of("message", "Команда ответа отменена")).build();
    }

    /**
     * Выполнение всех команд последовательно.
     */
    @POST
    @Path("/commands/all")
    public Response executeAllCommands() {
        LOG.info("Выполнение всех команд через REST");
        analysisCommand.call();
        solutionCommand.call();
        responseCommand.call();
        return Response.ok(Map.of("message", "Все команды выполнены успешно")).build();
    }

    /**
     * Отмена всех команд.
     */
    @DELETE
    @Path("/commands/all")
    public Response cancelAllCommands() {
        LOG.info("Отмена всех команд через REST");
        analysisCommand.cancel();
        solutionCommand.cancel();
        responseCommand.cancel();
        return Response.ok(Map.of("message", "Все команды отменены")).build();
    }

    /**
     * Проверка существования решения по сообщению.
     */
    @GET
    @Path("/exists")
    public Response existsByMessage(@QueryParam("message") String message) {
        LOG.debug("Проверка существования решения по сообщению: {}", message);
        boolean exists = service.existsByMessage(message);
        return Response.ok(Map.of("exists", exists)).build();
    }

    /**
     * Получение решения по точному сообщению.
     */
    @GET
    @Path("/by-message")
    public Response getByMessage(@QueryParam("message") String message) {
        LOG.debug("Поиск решения по точному сообщению: {}", message);
        Optional<Solution> solution = service.getByMessage(message);
        return solution
                .map(s -> Response.ok(s).build())
                .orElse(Response.status(Response.Status.NOT_FOUND)
                        .entity(Map.of("message", "Решение не найдено"))
                        .build());
    }

    /**
     * Удаление всех решений на указанном языке.
     */
    @DELETE
    @Path("/language/{language}/all")
    public Response deleteByLanguage(@PathParam("language") Language language) {
        LOG.debug("Удаление всех решений на языке: {}", language);
        int deletedCount = service.deleteByLanguage(language);
        return Response.ok(Map.of("deletedCount", deletedCount)).build();
    }

    /**
     * Получение решений с сообщениями заданной длины.
     */
    @GET
    @Path("/length/{length}")
    public Response getByMessageLength(@PathParam("length") int length) {
        LOG.debug("Поиск решений с длиной сообщения: {}", length);
        List<Solution> solutions = service.getByMessageLength(length);
        return Response.ok(solutions).build();
    }

    /**
     * Комбинированный эндпоинт для демонстрации работы всех компонентов.
     */
    @POST
    @Path("/process")
    public Response processSolution(Solution solution) {
        LOG.info("Комбинированная обработка решения через REST");

        analysisCommand.call();

        Solution createdSolution = service.createSolution(solution);

        solutionCommand.call();

        responseCommand.call();

        return Response.status(Response.Status.CREATED)
                .entity(Map.of(
                        "message", "Решение успешно обработано",
                        "solution", createdSolution
                ))
                .build();
    }
}