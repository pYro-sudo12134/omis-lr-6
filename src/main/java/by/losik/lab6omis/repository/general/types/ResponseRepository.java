package by.losik.lab6omis.repository.general.types;

import by.losik.lab6omis.entities.general.enums.Language;
import by.losik.lab6omis.entities.general.types.ResponseEntity;
import by.losik.lab6omis.repository.base.BaseRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Репозиторий для управления ответами (ResponseEntity).
 * Предоставляет полный набор операций CRUD, поиска, фильтрации и статистики.
 *
 * @see ResponseEntity
 * @author Losik Yaroslav
 * @version 1.0
 */
@ApplicationScoped
public class ResponseRepository extends BaseRepository<ResponseEntity, Long> {

    @Inject
    public ResponseRepository() {}

    /**
     * Найти ответы по языку
     * @param language Язык ответа
     * @return Список ответов на указанном языке
     */
    public List<ResponseEntity> findByLanguage(Language language) {
        return executeQuery(
                "SELECT r FROM ResponseEntity r WHERE r.language = :language",
                Map.of("language", language)
        );
    }

    /**
     * Найти ответы по сообщению (точное совпадение)
     * @param message Сообщение ответа
     * @return Optional с найденным ответом или пустой
     */
    public Optional<ResponseEntity> findByMessage(String message) {
        return executeQuerySingle(
                "SELECT r FROM ResponseEntity r WHERE r.message = :message",
                Map.of("message", message)
        );
    }

    /**
     * Найти ответы, содержащие указанный текст в сообщении
     * @param text Текст для поиска
     * @return Список ответов, содержащих указанный текст в сообщении
     */
    public List<ResponseEntity> findByMessageContaining(String text) {
        return executeQuery(
                "SELECT r FROM ResponseEntity r WHERE LOWER(r.message) LIKE LOWER(:text)",
                Map.of("text", "%" + text + "%")
        );
    }

    /**
     * Найти ответы по длине сообщения
     * @param minLength Минимальная длина сообщения
     * @param maxLength Максимальная длина сообщения
     * @return Список ответов с сообщениями указанной длины
     */
    public List<ResponseEntity> findByMessageLengthBetween(int minLength, int maxLength) {
        return executeQuery(
                "SELECT r FROM ResponseEntity r WHERE LENGTH(r.message) BETWEEN :minLength AND :maxLength",
                Map.of("minLength", minLength, "maxLength", maxLength)
        );
    }

    /**
     * Найти ответы с короткими сообщениями (меньше указанной длины)
     * @param maxLength Максимальная длина сообщения
     * @return Список ответов с короткими сообщениями
     */
    public List<ResponseEntity> findByShortMessage(int maxLength) {
        return executeQuery(
                "SELECT r FROM ResponseEntity r WHERE LENGTH(r.message) < :maxLength",
                Map.of("maxLength", maxLength)
        );
    }

    /**
     * Найти ответы с длинными сообщениями (больше указанной длины)
     * @param minLength Минимальная длина сообщения
     * @return Список ответов с длинными сообщениями
     */
    public List<ResponseEntity> findByLongMessage(int minLength) {
        return executeQuery(
                "SELECT r FROM ResponseEntity r WHERE LENGTH(r.message) > :minLength",
                Map.of("minLength", minLength)
        );
    }

    /**
     * Найти ответы по языку с сортировкой по длине сообщения (по возрастанию)
     * @param language Язык ответа
     * @return Отсортированный список ответов
     */
    public List<ResponseEntity> findByLanguageOrderByMessageLengthAsc(Language language) {
        return executeQuery(
                "SELECT r FROM ResponseEntity r WHERE r.language = :language ORDER BY LENGTH(r.message) ASC",
                Map.of("language", language)
        );
    }

    /**
     * Найти ответы по языку с сортировкой по длине сообщения (по убыванию)
     * @param language Язык ответа
     * @return Отсортированный список ответов
     */
    public List<ResponseEntity> findByLanguageOrderByMessageLengthDesc(Language language) {
        return executeQuery(
                "SELECT r FROM ResponseEntity r WHERE r.language = :language ORDER BY LENGTH(r.message) DESC",
                Map.of("language", language)
        );
    }

    /**
     * Найти ответы, сообщения которых начинаются с указанного текста
     * @param prefix Префикс сообщения
     * @return Список ответов, чьи сообщения начинаются с указанного префикса
     */
    public List<ResponseEntity> findByMessageStartingWith(String prefix) {
        return executeQuery(
                "SELECT r FROM ResponseEntity r WHERE r.message LIKE :prefix",
                Map.of("prefix", prefix + "%")
        );
    }

    /**
     * Найти ответы, сообщения которых заканчиваются на указанный текст
     * @param suffix Суффикс сообщения
     * @return Список ответов, чьи сообщения заканчиваются на указанный суффикс
     */
    public List<ResponseEntity> findByMessageEndingWith(String suffix) {
        return executeQuery(
                "SELECT r FROM ResponseEntity r WHERE r.message LIKE :suffix",
                Map.of("suffix", "%" + suffix)
        );
    }

    /**
     * Получить статистику по количеству ответов на каждом языке
     * @return Список массивов объектов [язык, количество]
     */
    public Map<Language, Long> getResponsesCountByLanguage() {
        List<Object[]> results = executeCustomQuery(
                "SELECT r.language, COUNT(r) FROM ResponseEntity r GROUP BY r.language",
                Object[].class
        );
        return results.stream()
                .collect(Collectors.toMap(
                        arr -> (Language) arr[0],
                        arr -> (Long) arr[1]
                ));
    }

    /**
     * Получить среднюю длину сообщений по всем ответам
     * @return Средняя длина сообщений
     */
    public Double getAverageMessageLength() {
        return executeCustomQuerySingle(
                "SELECT AVG(LENGTH(r.message)) FROM ResponseEntity r",
                Double.class
        );
    }

    /**
     * Получить среднюю длину сообщений по языку
     * @param language Язык ответа
     * @return Средняя длина сообщений для указанного языка
     */
    public Double getAverageMessageLengthByLanguage(Language language) {
        return executeCustomQuerySingle(
                "SELECT AVG(LENGTH(r.message)) FROM ResponseEntity r WHERE r.language = :language",
                Double.class,
                Map.of("language", language)
        );
    }

    /**
     * Получить самые короткие ответы
     * @return Список ответов с минимальной длиной сообщения
     */
    public List<ResponseEntity> findShortestResponses() {
        return executeQuery(
                "SELECT r FROM ResponseEntity r WHERE LENGTH(r.message) = " +
                        "(SELECT MIN(LENGTH(r2.message)) FROM ResponseEntity r2)"
        );
    }

    /**
     * Получить самые длинные ответы
     * @return Список ответов с максимальной длиной сообщения
     */
    public List<ResponseEntity> findLongestResponses() {
        return executeQuery(
                "SELECT r FROM ResponseEntity r WHERE LENGTH(r.message) = " +
                        "(SELECT MAX(LENGTH(r2.message)) FROM ResponseEntity r2)"
        );
    }

    /**
     * Проверить, существует ли ответ с указанным сообщением
     * @param message Сообщение ответа
     * @return true если существует, false в противном случае
     */
    public boolean existsByMessage(String message) {
        List<ResponseEntity> results = executeQuery(
                "SELECT r FROM ResponseEntity r WHERE r.message = :message",
                Map.of("message", message)
        );
        return !results.isEmpty();
    }

    /**
     * Удалить все ответы по языку
     * @param language Язык ответа
     * @return Количество удаленных записей (через кастомный запрос)
     */
    public int deleteByLanguage(Language language) {
        return executeQuery(em ->
                em.createQuery("DELETE FROM ResponseEntity r WHERE r.language = :language")
                .setParameter("language", language)
                .executeUpdate());
    }

    /**
     * Найти ответы по языку с пагинацией
     * @param language Язык ответа
     * @param page Номер страницы
     * @param size Размер страницы
     * @return Список ответов для указанной страницы
     */
    public List<ResponseEntity> findByLanguageWithPagination(Language language, int page, int size) {
        return executeQuery(em ->
                em.createQuery("SELECT r FROM ResponseEntity r WHERE r.language = :language", ResponseEntity.class)
                        .setParameter("language", language)
                        .setFirstResult(page * size)
                        .setMaxResults(size)
                        .getResultList()
        );
    }

    /**
     * Найти количество ответов по языку
     * @param language Язык ответа
     * @return Количество ответов на указанном языке
     */
    public Long countByLanguage(Language language) {
        return executeCustomQuerySingle(
                "SELECT COUNT(r) FROM ResponseEntity r WHERE r.language = :language",
                Long.class,
                Map.of("language", language)
        );
    }

    /**
     * Найти ответы по нескольким языкам
     * @param languages Список языков
     * @return Список ответов на указанных языках
     */
    public List<ResponseEntity> findByLanguages(List<Language> languages) {
        return executeQuery(
                "SELECT r FROM ResponseEntity r WHERE r.language IN :languages",
                Map.of("languages", languages)
        );
    }

    /**
     * Найти ответы с сообщениями определенной длины
     * @param exactLength Точная длина сообщения
     * @return Список ответов с сообщениями указанной длины
     */
    public List<ResponseEntity> findByMessageLength(int exactLength) {
        return executeQuery(
                "SELECT r FROM ResponseEntity r WHERE LENGTH(r.message) = :exactLength",
                Map.of("exactLength", exactLength)
        );
    }
}