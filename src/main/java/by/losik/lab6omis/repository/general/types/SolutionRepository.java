package by.losik.lab6omis.repository.general.types;

import by.losik.lab6omis.entities.general.enums.Language;
import by.losik.lab6omis.entities.general.types.Solution;
import by.losik.lab6omis.repository.base.BaseRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Репозиторий для управления решениями (Solution).
 * Предоставляет полный набор операций CRUD, поиска, фильтрации и статистики.
 *
 * @see Solution
 * @author Losik Yaroslav
 * @version 1.0
 */
@ApplicationScoped
public class SolutionRepository extends BaseRepository<Solution, Long> {

    @Inject
    public SolutionRepository() {}

    /**
     * Найти решения по языку
     * @param language Язык решения
     * @return Список решений на указанном языке
     */
    public List<Solution> findByLanguage(Language language) {
        return executeQuery(
                "SELECT s FROM Solution s WHERE s.language = :language",
                Map.of("language", language)
        );
    }

    /**
     * Найти решения по сообщению (точное совпадение)
     * @param message Сообщение решения
     * @return Optional с найденным решением или пустой
     */
    public Optional<Solution> findByMessage(String message) {
        return executeQuerySingle(
                "SELECT s FROM Solution s WHERE s.message = :message",
                Map.of("message", message)
        );
    }

    /**
     * Найти решения, содержащие указанный текст в сообщении
     * @param text Текст для поиска
     * @return Список решений, содержащих указанный текст в сообщении
     */
    public List<Solution> findByMessageContaining(String text) {
        return executeQuery(
                "SELECT s FROM Solution s WHERE LOWER(s.message) LIKE LOWER(:text)",
                Map.of("text", "%" + text + "%")
        );
    }

    /**
     * Найти решения с сообщениями в заданном диапазоне длины
     * @param minLength Минимальная длина сообщения
     * @param maxLength Максимальная длина сообщения
     * @return Список решений с сообщениями указанной длины
     */
    public List<Solution> findByMessageLengthBetween(int minLength, int maxLength) {
        return executeQuery(
                "SELECT s FROM Solution s WHERE LENGTH(s.message) BETWEEN :minLength AND :maxLength",
                Map.of("minLength", minLength, "maxLength", maxLength)
        );
    }

    /**
     * Найти решения с короткими сообщениями (меньше указанной длины)
     * @param maxLength Максимальная длина сообщения
     * @return Список решений с короткими сообщениями
     */
    public List<Solution> findByShortMessage(int maxLength) {
        return executeQuery(
                "SELECT s FROM Solution s WHERE LENGTH(s.message) < :maxLength",
                Map.of("maxLength", maxLength)
        );
    }

    /**
     * Найти решения с длинными сообщениями (больше указанной длины)
     * @param minLength Минимальная длина сообщения
     * @return Список решений с длинными сообщениями
     */
    public List<Solution> findByLongMessage(int minLength) {
        return executeQuery(
                "SELECT s FROM Solution s WHERE LENGTH(s.message) > :minLength",
                Map.of("minLength", minLength)
        );
    }

    /**
     * Найти решения по языку с сортировкой по длине сообщения
     * @param language Язык решения
     * @param ascending true - по возрастанию, false - по убыванию
     * @return Отсортированный список решений
     */
    public List<Solution> findByLanguageOrderByMessageLength(Language language, boolean ascending) {
        String orderClause = ascending ? "ASC" : "DESC";
        return executeQuery(
                "SELECT s FROM Solution s WHERE s.language = :language ORDER BY LENGTH(s.message) " + orderClause,
                Map.of("language", language)
        );
    }

    /**
     * Найти решения, сообщения которых начинаются с указанного текста
     * @param prefix Префикс сообщения
     * @return Список решений, чьи сообщения начинаются с указанного префикса
     */
    public List<Solution> findByMessageStartingWith(String prefix) {
        return executeQuery(
                "SELECT s FROM Solution s WHERE s.message LIKE :prefix",
                Map.of("prefix", prefix + "%")
        );
    }

    /**
     * Найти решения, сообщения которых заканчиваются на указанный текст
     * @param suffix Суффикс сообщения
     * @return Список решений, чьи сообщения заканчиваются на указанный суффикс
     */
    public List<Solution> findByMessageEndingWith(String suffix) {
        return executeQuery(
                "SELECT s FROM Solution s WHERE s.message LIKE :suffix",
                Map.of("suffix", "%" + suffix)
        );
    }

    /**
     * Найти решения по нескольким языкам
     * @param languages Список языков
     * @return Список решений на указанных языках
     */
    public List<Solution> findByLanguages(List<Language> languages) {
        return executeQuery(
                "SELECT s FROM Solution s WHERE s.language IN :languages",
                Map.of("languages", languages)
        );
    }

    /**
     * Получить статистику по количеству решений на каждом языке
     * @return Список массивов объектов [язык, количество]
     */
    public Map<Language, Long> getSolutionsCountByLanguage() {
        List<Object[]> results = executeCustomQuery(
                "SELECT s.language, COUNT(s) FROM Solution s GROUP BY s.language",
                Object[].class
        );
        return results.stream()
                .collect(Collectors.toMap(
                        arr -> (Language) arr[0],
                        arr -> (Long) arr[1]
                ));
    }

    /**
     * Получить среднюю длину сообщений решений
     * @return Средняя длина сообщений
     */
    public Double getAverageMessageLength() {
        return executeCustomQuerySingle(
                "SELECT AVG(LENGTH(s.message)) FROM Solution s",
                Double.class
        );
    }

    /**
     * Получить среднюю длину сообщений решений по языку
     * @param language Язык решения
     * @return Средняя длина сообщений для указанного языка
     */
    public Double getAverageMessageLengthByLanguage(Language language) {
        return executeCustomQuerySingle(
                "SELECT AVG(LENGTH(s.message)) FROM Solution s WHERE s.language = :language",
                Double.class,
                Map.of("language", language)
        );
    }

    /**
     * Получить самые короткие решения
     * @return Список решений с минимальной длиной сообщения
     */
    public List<Solution> findShortestSolutions() {
        return executeQuery(
                "SELECT s FROM Solution s WHERE LENGTH(s.message) = " +
                        "(SELECT MIN(LENGTH(s2.message)) FROM Solution s2)"
        );
    }

    /**
     * Получить самые длинные решения
     * @return Список решений с максимальной длиной сообщения
     */
    public List<Solution> findLongestSolutions() {
        return executeQuery(
                "SELECT s FROM Solution s WHERE LENGTH(s.message) = " +
                        "(SELECT MAX(LENGTH(s2.message)) FROM Solution s2)"
        );
    }

    /**
     * Проверить, существует ли решение с указанным сообщением
     * @param message Сообщение решения
     * @return true если существует, false в противном случае
     */
    public boolean existsByMessage(String message) {
        List<Solution> results = executeQuery(
                "SELECT s FROM Solution s WHERE s.message = :message",
                Map.of("message", message)
        );
        return !results.isEmpty();
    }

    /**
     * Найти решения по языку с пагинацией
     * @param language Язык решения
     * @param page Номер страницы
     * @param size Размер страницы
     * @return Список решений для указанной страницы
     */
    public List<Solution> findByLanguageWithPagination(Language language, int page, int size) {
        return executeQuery(em ->
                em.createQuery("SELECT s FROM Solution s WHERE s.language = :language", Solution.class)
                        .setParameter("language", language)
                        .setFirstResult(page * size)
                        .setMaxResults(size)
                        .getResultList()
        );
    }

    /**
     * Найти количество решений по языку
     * @param language Язык решения
     * @return Количество решений на указанном языке
     */
    public Long countByLanguage(Language language) {
        return executeCustomQuerySingle(
                "SELECT COUNT(s) FROM Solution s WHERE s.language = :language",
                Long.class,
                Map.of("language", language)
        );
    }

    /**
     * Удалить все решения по языку
     * @param language Язык решения
     * @return Количество удаленных записей
     */
    public int deleteByLanguage(Language language) {
        return executeQuery(em ->
                em.createQuery("DELETE FROM Solution s WHERE s.language = :language")
                .setParameter("language", language)
                .executeUpdate());
    }

    /**
     * Найти решения с сообщениями определенной длины
     * @param exactLength Точная длина сообщения
     * @return Список решений с сообщениями указанной длины
     */
    public List<Solution> findByMessageLength(int exactLength) {
        return executeQuery(
                "SELECT s FROM Solution s WHERE LENGTH(s.message) = :exactLength",
                Map.of("exactLength", exactLength)
        );
    }

    /**
     * Поиск решений с использованием полнотекстового поиска (по ключевым словам)
     * @param keywords Ключевые слова для поиска
     * @return Список решений, содержащих хотя бы одно из ключевых слов
     */
    public List<Solution> searchByKeywords(List<String> keywords) {
        StringBuilder jpql = new StringBuilder("SELECT s FROM Solution s WHERE ");
        for (int i = 0; i < keywords.size(); i++) {
            if (i > 0) {
                jpql.append(" OR ");
            }
            jpql.append("LOWER(s.message) LIKE LOWER(:keyword").append(i).append(")");
        }

        Map<String, Object> params = new java.util.HashMap<>();
        for (int i = 0; i < keywords.size(); i++) {
            params.put("keyword" + i, "%" + keywords.get(i) + "%");
        }

        return executeQuery(jpql.toString(), params);
    }
}