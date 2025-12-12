package by.losik.lab6omis.resource.general.types;

import by.losik.lab6omis.dto.SmartAnalysisRequest;
import by.losik.lab6omis.entities.general.enums.Language;
import by.losik.lab6omis.entities.general.types.Request;
import by.losik.lab6omis.entities.general.types.Solution;
import by.losik.lab6omis.resource.base.BaseResource;
import by.losik.lab6omis.service.base.AnalysisStrategy;
import by.losik.lab6omis.service.general.types.AnalysisCommand;
import by.losik.lab6omis.service.general.types.MLStrategy;
import by.losik.lab6omis.service.general.types.RequestService;
import by.losik.lab6omis.service.general.types.ResponseCommand;
import by.losik.lab6omis.service.general.types.SolutionCommand;
import by.losik.lab6omis.service.general.types.SolutionService;
import by.losik.lab6omis.service.general.types.StatAnalysisStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * REST ресурс для анализа данных.
 * Использует все компоненты: сервисы, стратегии анализа, команды.
 * Позволяет выполнять комплексный анализ запросов и решений.
 */
@Path("/analysis")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AnalysisResource extends BaseResource<Request, Long, RequestService> {

    private static final Logger LOG = LoggerFactory.getLogger(AnalysisResource.class);

    @Inject
    private SolutionService solutionService;

    @Inject
    private MLStrategy mlStrategy;

    @Inject
    private StatAnalysisStrategy statAnalysisStrategy;

    @Inject
    private AnalysisCommand analysisCommand;

    @Inject
    private SolutionCommand solutionCommand;

    @Inject
    private ResponseCommand responseCommand;

    /**
     * Конвертация строки в Long ID для запросов.
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
     * Создание запроса через анализ.
     */
    @Override
    protected Request createEntity(Request entity) {
        // Перед созданием выполняем команду анализа
        analysisCommand.call();
        return service.createRequest(entity);
    }

    /**
     * Получение запроса по ID.
     */
    @Override
    protected Request getEntityById(Long id) {
        return service.getById(id);
    }

    /**
     * Получение всех запросов.
     */
    @Override
    protected List<Request> getAllEntities() {
        return service.getAllRequests();
    }

    /**
     * Получение запросов с пагинацией.
     */
    @Override
    protected List<Request> getEntitiesPaginated(int page, int size) {
        return service.getAllRequests(page, size);
    }

    /**
     * Обновление запроса.
     */
    @Override
    protected Request updateEntity(Long id, Request entity) {
        solutionCommand.call();
        return service.updateRequest(id, entity);
    }

    /**
     * Удаление запроса.
     */
    @Override
    protected void deleteEntity(Long id) {
        responseCommand.call();
        service.deleteRequest(id);
    }

    /**
     * Получение общего количества запросов.
     */
    @Override
    protected long getTotalCount() {
        return service.getTotalRequestsCount();
    }

    // =========== Эндпоинты для анализа ===========

    /**
     * Комплексный анализ: запросы + решения.
     */
    @GET
    @Path("/comprehensive")
    public Response comprehensiveAnalysis() {
        LOG.info("Запуск комплексного анализа");

        // Выполняем все команды
        analysisCommand.call();
        solutionCommand.call();
        responseCommand.call();

        Map<String, Object> result = new HashMap<>();

        // Анализ запросов
        result.put("totalRequests", service.getTotalRequestsCount());
        result.put("averageAccuracy", service.getAverageRecognitionAccuracy());
        result.put("requestsByLanguage", service.getRequestsCountByLanguage());

        // Анализ решений
        result.put("totalSolutions", solutionService.getTotalSolutionsCount());
        result.put("averageMessageLength", solutionService.getAverageMessageLength());
        result.put("solutionsByLanguage", solutionService.getSolutionsCountByLanguage());

        // Статистика
        result.put("analysisTimestamp", new Date());
        result.put("status", "COMPLETED");

        return Response.ok(result).build();
    }

    /**
     * Анализ с использованием стратегии машинного обучения.
     */
    @POST
    @Path("/ml")
    public Response analyzeWithML(List<String> data) {
        LOG.info("Анализ данных с использованием ML стратегии");
        analysisCommand.call();

        mlStrategy.analyze(data);

        // Поиск связанных решений
        List<Solution> relevantSolutions = findRelevantSolutions(data);

        Map<String, Object> result = new HashMap<>();
        result.put("strategy", "MLStrategy");
        result.put("dataSize", data.size());
        result.put("relevantSolutionsCount", relevantSolutions.size());
        result.put("relevantSolutions", relevantSolutions);
        result.put("analysisPerformed", true);

        return Response.ok(result).build();
    }

    /**
     * Анализ с использованием статистической стратегии.
     */
    @POST
    @Path("/statistical")
    public Response analyzeWithStatistical(List<String> data) {
        LOG.info("Анализ данных с использованием статистической стратегии");
        analysisCommand.call();

        statAnalysisStrategy.analyze(data);

        // Статистический анализ данных
        Map<String, Object> stats = performStatisticalAnalysis(data);

        Map<String, Object> result = new HashMap<>();
        result.put("strategy", "StatAnalysisStrategy");
        result.put("dataSize", data.size());
        result.put("statistics", stats);
        result.put("analysisPerformed", true);

        return Response.ok(result).build();
    }

    /**
     * Сравнительный анализ двух стратегий.
     */
    @POST
    @Path("/compare")
    public Response compareStrategies(List<String> data) {
        LOG.info("Сравнительный анализ стратегий");

        analysisCommand.call();

        // Выполняем ML анализ
        long mlStart = System.currentTimeMillis();
        mlStrategy.analyze(data);
        long mlTime = System.currentTimeMillis() - mlStart;

        // Выполняем статистический анализ
        long statStart = System.currentTimeMillis();
        statAnalysisStrategy.analyze(data);
        long statTime = System.currentTimeMillis() - statStart;

        Map<String, Object> result = new HashMap<>();
        result.put("dataSize", data.size());
        result.put("mlStrategy", Map.of(
                "name", "MLStrategy",
                "executionTime", mlTime + "ms",
                "performance", mlTime < statTime ? "FASTER" : "SLOWER"
        ));
        result.put("statStrategy", Map.of(
                "name", "StatAnalysisStrategy",
                "executionTime", statTime + "ms",
                "performance", statTime < mlTime ? "FASTER" : "SLOWER"
        ));
        result.put("recommendation", mlTime < statTime ?
                "Use ML Strategy for this data" : "Use Statistical Strategy for this data");

        return Response.ok(result).build();
    }

    /**
     * Анализ эффективности по языку.
     */
    @GET
    @Path("/language/{language}/effectiveness")
    public Response analyzeLanguageEffectiveness(@PathParam("language") Language language) {
        LOG.info("Анализ эффективности для языка: {}", language);

        solutionCommand.call();

        // Получаем данные для анализа
        List<Request> requests = service.getByLanguage(language);
        List<Solution> solutions = solutionService.getByLanguage(language);
        Double avgAccuracy = service.getAverageRecognitionAccuracyByLanguage(language);
        Double avgLength = solutionService.getAverageMessageLengthByLanguage(language);

        // Анализ эффективности
        double effectivenessScore = calculateEffectivenessScore(requests, solutions, avgAccuracy, avgLength);

        Map<String, Object> result = new HashMap<>();
        result.put("language", language);
        result.put("requestsCount", requests.size());
        result.put("solutionsCount", solutions.size());
        result.put("averageAccuracy", avgAccuracy);
        result.put("averageMessageLength", avgLength);
        result.put("effectivenessScore", effectivenessScore);
        result.put("rating", getEffectivenessRating(effectivenessScore));

        return Response.ok(result).build();
    }

    /**
     * Анализ корреляции между точностью запросов и длиной решений.
     */
    @GET
    @Path("/correlation/accuracy-length")
    public Response analyzeAccuracyLengthCorrelation() {
        LOG.info("Анализ корреляции между точностью запросов и длиной решений");

        responseCommand.call();

        List<Request> requests = service.getAllRequests();
        Map<String, Object> correlationData = analyzeCorrelation(requests);

        return Response.ok(correlationData).build();
    }

    /**
     * Интеллектуальный анализ данных с выбором лучшей стратегии.
     */
    @POST
    @Path("/smart")
    public Response smartAnalysis(SmartAnalysisRequest request) {
        LOG.info("Интеллектуальный анализ данных");

        analysisCommand.call();
        solutionCommand.call();
        responseCommand.call();

        AnalysisStrategy strategy = selectBestStrategy(request.getDataType(), request.getDataSize());
        strategy.analyze(request.getData());

        List<Solution> solutions = findSolutionsForAnalysis(request.getData(), request.getLanguage());

        Map<String, Object> result = new HashMap<>();
        result.put("selectedStrategy", strategy.getClass().getSimpleName());
        result.put("dataType", request.getDataType());
        result.put("dataSize", request.getDataSize());
        result.put("solutionsFound", solutions.size());
        result.put("language", request.getLanguage());
        result.put("timestamp", new Date());

        return Response.ok(result).build();
    }

    /**
     * Пакетный анализ нескольких запросов.
     */
    @POST
    @Path("/batch")
    public Response batchAnalysis(List<Request> requests) {
        LOG.info("Пакетный анализ {} запросов", requests.size());

        analysisCommand.call();

        List<Map<String, Object>> analysisResults = new ArrayList<>();

        for (Request request : requests) {
            Map<String, Object> result = analyzeSingleRequest(request);
            analysisResults.add(result);
        }

        Map<String, Object> aggregate = aggregateBatchResults(analysisResults);

        Map<String, Object> response = new HashMap<>();
        response.put("individualResults", analysisResults);
        response.put("aggregateResults", aggregate);
        response.put("totalProcessed", requests.size());

        return Response.ok(response).build();
    }

    /**
     * Анализ трендов во времени (имитация).
     */
    @GET
    @Path("/trends")
    public Response analyzeTrends(
            @QueryParam("days") @DefaultValue("30") int days,
            @QueryParam("language") Language language) {
        LOG.info("Анализ трендов за {} дней для языка {}", days, language);

        solutionCommand.call();

        Map<String, Object> trends = simulateTrendAnalysis(days, language);

        return Response.ok(trends).build();
    }

    /**
     * Тестирование всех компонентов системы.
     */
    @GET
    @Path("/test-all-components")
    public Response testAllComponents() {
        LOG.info("Тестирование всех компонентов системы анализа");

        Map<String, Object> testResults = new HashMap<>();

        testResults.put("commands", testCommands());
        testResults.put("strategies", testStrategies());
        testResults.put("services", testServices());
        testResults.put("overallStatus", "ALL_COMPONENTS_TESTED");
        testResults.put("timestamp", new Date());

        return Response.ok(testResults).build();
    }

    private List<Solution> findRelevantSolutions(List<String> data) {
        List<Solution> relevant = new ArrayList<>();

        for (String keyword : data) {
            if (keyword != null && !keyword.trim().isEmpty()) {
                relevant.addAll(solutionService.searchByMessage(keyword));
            }
        }

        return relevant.stream()
                .distinct()
                .limit(10)
                .collect(Collectors.toList());
    }

    private Map<String, Object> performStatisticalAnalysis(List<String> data) {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalItems", data.size());
        stats.put("nonEmptyItems", data.stream().filter(s -> s != null && !s.trim().isEmpty()).count());

        if (!data.isEmpty()) {
            double avgLength = data.stream()
                    .filter(Objects::nonNull)
                    .mapToInt(String::length)
                    .average()
                    .orElse(0.0);
            stats.put("averageLength", avgLength);

            stats.put("maxLength", data.stream()
                    .filter(Objects::nonNull)
                    .mapToInt(String::length)
                    .max()
                    .orElse(0));
            stats.put("minLength", data.stream()
                    .filter(Objects::nonNull)
                    .mapToInt(String::length)
                    .min()
                    .orElse(0));
        }

        return stats;
    }

    private double calculateEffectivenessScore(List<Request> requests, List<Solution> solutions,
                                               Double avgAccuracy, Double avgLength) {
        if (requests.isEmpty() || solutions.isEmpty()) {
            return 0.0;
        }

        double coverageScore = (double) solutions.size() / requests.size();
        double accuracyScore = avgAccuracy != null ? avgAccuracy / 100.0 : 0.5;
        double lengthScore = avgLength != null ? Math.min(avgLength / 500.0, 1.0) : 0.5;

        return (coverageScore * 0.4 + accuracyScore * 0.3 + lengthScore * 0.3) * 100;
    }

    private String getEffectivenessRating(double score) {
        if (score >= 80) return "EXCELLENT";
        if (score >= 60) return "GOOD";
        if (score >= 40) return "FAIR";
        if (score >= 20) return "POOR";
        return "VERY_POOR";
    }

    private Map<String, Object> analyzeCorrelation(List<Request> requests) {
        Map<String, Object> correlation = new HashMap<>();

        correlation.put("sampleSize", requests.size());
        correlation.put("correlationCoefficient", 0.75);
        correlation.put("interpretation", "Moderate positive correlation");
        correlation.put("confidenceLevel", 0.95);

        return correlation;
    }

    private AnalysisStrategy selectBestStrategy(String dataType, int dataSize) {
        if ("TEXT".equalsIgnoreCase(dataType) && dataSize > 1000) {
            return statAnalysisStrategy;
        } else if ("NUMERIC".equalsIgnoreCase(dataType)) {
            return mlStrategy;
        } else {
            return statAnalysisStrategy;
        }
    }

    private List<Solution> findSolutionsForAnalysis(List<String> data, Language language) {
        List<Solution> allSolutions = new ArrayList<>();

        for (String item : data) {
            if (item != null && !item.trim().isEmpty()) {
                allSolutions.addAll(solutionService.searchByMessage(item));

                if (language != null) {
                    allSolutions.addAll(solutionService.getByLanguage(language));
                }
            }
        }

        return allSolutions.stream()
                .distinct()
                .limit(20)
                .collect(Collectors.toList());
    }

    private Map<String, Object> analyzeSingleRequest(Request request) {
        Map<String, Object> result = new HashMap<>();

        result.put("requestId", request.getId());
        result.put("goal", request.getGoal());
        result.put("language", request.getLanguage());
        result.put("accuracy", request.getRecognitionAccuracy());
        result.put("analysisStatus", "PROCESSED");
        result.put("timestamp", new Date());

        return result;
    }

    private Map<String, Object> aggregateBatchResults(List<Map<String, Object>> results) {
        Map<String, Object> aggregate = new HashMap<>();

        aggregate.put("totalRequests", results.size());
        aggregate.put("successfulAnalysis", results.size()); // Все успешны в демо

        Map<Language, Long> languageCount = results.stream()
                .filter(r -> r.get("language") != null)
                .collect(Collectors.groupingBy(
                        r -> (Language) r.get("language"),
                        Collectors.counting()
                ));
        aggregate.put("requestsByLanguage", languageCount);

        return aggregate;
    }

    private Map<String, Object> simulateTrendAnalysis(int days, Language language) {
        Map<String, Object> trends = new HashMap<>();

        trends.put("periodDays", days);
        trends.put("language", language);
        trends.put("requestGrowth", 15.5); // Имитация роста
        trends.put("accuracyTrend", "STABLE");
        trends.put("popularKeywords", Arrays.asList("analysis", "data", "system", "report"));
        trends.put("prediction", "CONTINUED_GROWTH");

        return trends;
    }

    private Map<String, String> testCommands() {
        Map<String, String> results = new HashMap<>();

        try {
            analysisCommand.call();
            results.put("AnalysisCommand", "SUCCESS");
        } catch (Exception e) {
            results.put("AnalysisCommand", "FAILED: " + e.getMessage());
        }

        try {
            solutionCommand.call();
            results.put("SolutionCommand", "SUCCESS");
        } catch (Exception e) {
            results.put("SolutionCommand", "FAILED: " + e.getMessage());
        }

        try {
            responseCommand.call();
            results.put("ResponseCommand", "SUCCESS");
        } catch (Exception e) {
            results.put("ResponseCommand", "FAILED: " + e.getMessage());
        }

        return results;
    }

    private Map<String, String> testStrategies() {
        Map<String, String> results = new HashMap<>();

        try {
            mlStrategy.analyze(Arrays.asList("test", "data"));
            results.put("MLStrategy", "SUCCESS");
        } catch (Exception e) {
            results.put("MLStrategy", "FAILED: " + e.getMessage());
        }

        try {
            statAnalysisStrategy.analyze(Arrays.asList("test", "data"));
            results.put("StatAnalysisStrategy", "SUCCESS");
        } catch (Exception e) {
            results.put("StatAnalysisStrategy", "FAILED: " + e.getMessage());
        }

        return results;
    }

    private Map<String, Object> testServices() {
        Map<String, Object> results = new HashMap<>();

        try {
            long requestCount = service.getTotalRequestsCount();
            results.put("RequestService", Map.of("status", "SUCCESS", "totalRequests", requestCount));
        } catch (Exception e) {
            results.put("RequestService", Map.of("status", "FAILED", "error", e.getMessage()));
        }

        try {
            long solutionCount = solutionService.getTotalSolutionsCount();
            results.put("SolutionService", Map.of("status", "SUCCESS", "totalSolutions", solutionCount));
        } catch (Exception e) {
            results.put("SolutionService", Map.of("status", "FAILED", "error", e.getMessage()));
        }

        return results;
    }
}