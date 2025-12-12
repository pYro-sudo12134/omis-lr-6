package by.losik.lab6omis.repository.general.types;

import by.losik.lab6omis.entities.general.enums.Language;
import by.losik.lab6omis.entities.general.types.Request;
import by.losik.lab6omis.repository.base.BaseRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Репозиторий для управления запросами (Request).
 * Предоставляет полный набор операций CRUD, поиска, фильтрации и статистики.
 *
 * @see Request
 * @author Losik Yaroslav
 * @version 1.0
 */
@ApplicationScoped
public class RequestRepository extends BaseRepository<Request, Long> {

    @Inject
    public RequestRepository() {}

    /**
     * Найти запросы по языку
     * @param language Язык запроса
     * @return Список запросов на указанном языке
     */
    public List<Request> findByLanguage(Language language) {
        return executeQuery(
                "SELECT r FROM Request r WHERE r.language = :language",
                Map.of("language", language)
        );
    }

    /**
     * Найти запросы с точностью распознавания выше указанного значения
     * @param minAccuracy Минимальная точность распознавания
     * @return Список запросов с указанной точностью или выше
     */
    public List<Request> findByRecognitionAccuracyGreaterThan(Double minAccuracy) {
        return executeQuery(
                "SELECT r FROM Request r WHERE r.recognitionAccuracy >= :minAccuracy",
                Map.of("minAccuracy", minAccuracy)
        );
    }

    /**
     * Найти запросы с точностью распознавания в диапазоне
     * @param minAccuracy Минимальная точность
     * @param maxAccuracy Максимальная точность
     * @return Список запросов в указанном диапазоне точности
     */
    public List<Request> findByRecognitionAccuracyBetween(Double minAccuracy, Double maxAccuracy) {
        return executeQuery(
                "SELECT r FROM Request r WHERE r.recognitionAccuracy BETWEEN :minAccuracy AND :maxAccuracy",
                Map.of("minAccuracy", minAccuracy, "maxAccuracy", maxAccuracy)
        );
    }

    /**
     * Найти запросы по цели (поиск с использованием LIKE)
     * @param goalPattern Паттерн поиска цели (можно использовать %)
     * @return Список запросов, чья цель соответствует паттерну
     */
    public List<Request> findByGoalLike(String goalPattern) {
        return executeQuery(
                "SELECT r FROM Request r WHERE r.goal LIKE :goalPattern",
                Map.of("goalPattern", goalPattern)
        );
    }

    /**
     * Найти запросы по языку с сортировкой по точности распознавания (по убыванию)
     * @param language Язык запроса
     * @return Отсортированный список запросов
     */
    public List<Request> findByLanguageOrderByAccuracyDesc(Language language) {
        return executeQuery(
                "SELECT r FROM Request r WHERE r.language = :language ORDER BY r.recognitionAccuracy DESC",
                Map.of("language", language)
        );
    }

    /**
     * Получить среднюю точность распознавания по всем запросам
     * @return Средняя точность распознавания
     */
    public Double getAverageRecognitionAccuracy() {
        return executeCustomQuerySingle(
                "SELECT AVG(r.recognitionAccuracy) FROM Request r",
                Double.class
        );
    }

    /**
     * Получить среднюю точность распознавания по языку
     * @param language Язык запроса
     * @return Средняя точность распознавания для указанного языка
     */
    public Double getAverageRecognitionAccuracyByLanguage(Language language) {
        return executeCustomQuerySingle(
                "SELECT AVG(r.recognitionAccuracy) FROM Request r WHERE r.language = :language",
                Double.class,
                Map.of("language", language)
        );
    }

    /**
     * Получить статистику по количеству запросов на каждом языке
     * @return Список массивов объектов [язык, количество]
     */
    public Map<Language, Long> getRequestsCountByLanguage() {
        List<Object[]> results = executeCustomQuery(
                "SELECT r.language, COUNT(r) FROM Request r GROUP BY r.language",
                Object[].class
        );
        return results.stream()
                .collect(Collectors.toMap(
                        arr -> (Language) arr[0],
                        arr -> (Long) arr[1]
                ));
    }

    /**
     * Получить запросы с максимальной точностью распознавания
     * @return Список запросов с максимальной точностью
     */
    public List<Request> findTopByRecognitionAccuracy() {
        return executeQuery(
                "SELECT r FROM Request r WHERE r.recognitionAccuracy = " +
                        "(SELECT MAX(r2.recognitionAccuracy) FROM Request r2)"
        );
    }

    /**
     * Получить запросы с минимальной точностью распознавания
     * @return Список запросов с минимальной точностью
     */
    public List<Request> findBottomByRecognitionAccuracy() {
        return executeQuery(
                "SELECT r FROM Request r WHERE r.recognitionAccuracy = " +
                        "(SELECT MIN(r2.recognitionAccuracy) FROM Request r2)"
        );
    }

    /**
     * Проверить, существует ли запрос с указанной целью
     * @param goal Цель запроса
     * @return true если существует, false в противном случае
     */
    public boolean existsByGoal(String goal) {
        List<Request> results = executeQuery(
                "SELECT r FROM Request r WHERE r.goal = :goal",
                Map.of("goal", goal)
        );
        return !results.isEmpty();
    }

    /**
     * Удалить все запросы по языку
     * @param language Язык запроса
     * @return Количество удаленных записей
     */
    public int deleteByLanguage(Language language) {
        return executeQuery(em ->
                em.createQuery("DELETE FROM Request r WHERE r.language = :language")
                .setParameter("language", language)
                .executeUpdate());
    }

    /**
     * Найти запрос по цели (точное совпадение)
     * @param goal Цель запроса
     * @return Optional с найденным запросом или пустой
     */
    public Optional<Request> findByGoal(String goal) {
        return executeQuerySingle(
                "SELECT r FROM Request r WHERE r.goal = :goal",
                Map.of("goal", goal)
        );
    }

    /**
     * Найти запросы с целью, содержащей указанный текст
     * @param text Текст для поиска
     * @return Список запросов, содержащих указанный текст в цели
     */
    public List<Request> findByGoalContaining(String text) {
        return executeQuery(
                "SELECT r FROM Request r WHERE LOWER(r.goal) LIKE LOWER(:text)",
                Map.of("text", "%" + text + "%")
        );
    }
}