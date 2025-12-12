package by.losik.lab6omis.repository.general.types;

import by.losik.lab6omis.entities.general.types.Sound;
import by.losik.lab6omis.repository.base.BaseRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Репозиторий для управления звуками (Sound).
 * Предоставляет полный набор операций CRUD, поиска, фильтрации и статистики.
 *
 * @see Sound
 * @author Losik Yaroslav
 * @version 1.0
 */
@ApplicationScoped
public class SoundRepository extends BaseRepository<Sound, Long> {

    @Inject
    public SoundRepository() {}

    /**
     * Найти звуки по типу шума (точное совпадение)
     * @param noise Тип шума
     * @return Optional с найденным звуком или пустой
     */
    public Optional<Sound> findByNoise(String noise) {
        return executeQuerySingle(
                "SELECT s FROM Sound s WHERE s.noise = :noise",
                Map.of("noise", noise)
        );
    }

    /**
     * Найти звуки, содержащие указанный текст в типе шума
     * @param text Текст для поиска
     * @return Список звуков, содержащих указанный текст в типе шума
     */
    public List<Sound> findByNoiseContaining(String text) {
        return executeQuery(
                "SELECT s FROM Sound s WHERE LOWER(s.noise) LIKE LOWER(:text)",
                Map.of("text", "%" + text + "%")
        );
    }

    /**
     * Найти звуки по частоте
     * @param frequency Частота в Гц
     * @return Список звуков с указанной частотой
     */
    public List<Sound> findByFrequency(Integer frequency) {
        return executeQuery(
                "SELECT s FROM Sound s WHERE s.frequency = :frequency",
                Map.of("frequency", frequency)
        );
    }

    /**
     * Найти звуки с частотой в заданном диапазоне
     * @param minFrequency Минимальная частота
     * @param maxFrequency Максимальная частота
     * @return Список звуков с частотой в указанном диапазоне
     */
    public List<Sound> findByFrequencyBetween(Integer minFrequency, Integer maxFrequency) {
        return executeQuery(
                "SELECT s FROM Sound s WHERE s.frequency BETWEEN :minFrequency AND :maxFrequency",
                Map.of("minFrequency", minFrequency, "maxFrequency", maxFrequency)
        );
    }

    /**
     * Найти звуки с низкой частотой (меньше указанной)
     * @param maxFrequency Максимальная частота
     * @return Список звуков с низкой частотой
     */
    public List<Sound> findByLowFrequency(Integer maxFrequency) {
        return executeQuery(
                "SELECT s FROM Sound s WHERE s.frequency < :maxFrequency",
                Map.of("maxFrequency", maxFrequency)
        );
    }

    /**
     * Найти звуки с высокой частотой (больше указанной)
     * @param minFrequency Минимальная частота
     * @return Список звуков с высокой частотой
     */
    public List<Sound> findByHighFrequency(Integer minFrequency) {
        return executeQuery(
                "SELECT s FROM Sound s WHERE s.frequency > :minFrequency",
                Map.of("minFrequency", minFrequency)
        );
    }

    /**
     * Найти звуки по типу шума с сортировкой по частоте (по возрастанию)
     * @param noise Тип шума (может быть частью)
     * @return Отсортированный список звуков
     */
    public List<Sound> findByNoiseOrderByFrequencyAsc(String noise) {
        return executeQuery(
                "SELECT s FROM Sound s WHERE s.noise LIKE :noise ORDER BY s.frequency ASC",
                Map.of("noise", "%" + noise + "%")
        );
    }

    /**
     * Найти звуки по типу шума с сортировкой по частоте (по убыванию)
     * @param noise Тип шума (может быть частью)
     * @return Отсортированный список звуков
     */
    public List<Sound> findByNoiseOrderByFrequencyDesc(String noise) {
        return executeQuery(
                "SELECT s FROM Sound s WHERE s.noise LIKE :noise ORDER BY s.frequency DESC",
                Map.of("noise", "%" + noise + "%")
        );
    }

    /**
     * Найти звуки, тип шума которых начинается с указанного текста
     * @param prefix Префикс типа шума
     * @return Список звуков, чьи типы шума начинаются с указанного префикса
     */
    public List<Sound> findByNoiseStartingWith(String prefix) {
        return executeQuery(
                "SELECT s FROM Sound s WHERE s.noise LIKE :prefix",
                Map.of("prefix", prefix + "%")
        );
    }

    /**
     * Найти звуки, тип шума которых заканчивается на указанный текст
     * @param suffix Суффикс типа шума
     * @return Список звуков, чьи типы шума заканчиваются на указанный суффикс
     */
    public List<Sound> findByNoiseEndingWith(String suffix) {
        return executeQuery(
                "SELECT s FROM Sound s WHERE s.noise LIKE :suffix",
                Map.of("suffix", "%" + suffix)
        );
    }

    /**
     * Получить статистику по средним частотам для каждого типа шума
     * @return Карта [тип шума, средняя частота]
     */
    public Map<String, Double> getAverageFrequencyByNoiseType() {
        List<Object[]> results = executeCustomQuery(
                "SELECT s.noise, AVG(s.frequency) FROM Sound s GROUP BY s.noise",
                Object[].class
        );
        return results.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Double) arr[1]
                ));
    }

    /**
     * Получить среднюю частоту всех звуков
     * @return Средняя частота
     */
    public Double getAverageFrequency() {
        return executeCustomQuerySingle(
                "SELECT AVG(s.frequency) FROM Sound s",
                Double.class
        );
    }

    /**
     * Получить звуки с минимальной частотой
     * @return Список звуков с минимальной частотой
     */
    public List<Sound> findLowestFrequencySounds() {
        return executeQuery(
                "SELECT s FROM Sound s WHERE s.frequency = " +
                        "(SELECT MIN(s2.frequency) FROM Sound s2)"
        );
    }

    /**
     * Получить звуки с максимальной частотой
     * @return Список звуков с максимальной частотой
     */
    public List<Sound> findHighestFrequencySounds() {
        return executeQuery(
                "SELECT s FROM Sound s WHERE s.frequency = " +
                        "(SELECT MAX(s2.frequency) FROM Sound s2)"
        );
    }

    /**
     * Проверить, существует ли звук с указанным типом шума
     * @param noise Тип шума
     * @return true если существует, false в противном случае
     */
    public boolean existsByNoise(String noise) {
        List<Sound> results = executeQuery(
                "SELECT s FROM Sound s WHERE s.noise = :noise",
                Map.of("noise", noise)
        );
        return !results.isEmpty();
    }

    /**
     * Удалить все звуки по частоте
     * @param frequency Частота
     * @return Количество удаленных записей
     */
    public int deleteByFrequency(Integer frequency) {
        return executeQuery(em ->
                em.createQuery("DELETE FROM Sound s WHERE s.frequency = :frequency")
                .setParameter("frequency", frequency)
                .executeUpdate());
    }

    /**
     * Найти звуки по частоте с пагинацией
     * @param frequency Частота
     * @param page Номер страницы
     * @param size Размер страницы
     * @return Список звуков для указанной страницы
     */
    public List<Sound> findByFrequencyWithPagination(Integer frequency, int page, int size) {
        return executeQuery(em ->
                em.createQuery("SELECT s FROM Sound s WHERE s.frequency = :frequency", Sound.class)
                        .setParameter("frequency", frequency)
                        .setFirstResult(page * size)
                        .setMaxResults(size)
                        .getResultList()
        );
    }

    /**
     * Найти количество звуков по частоте
     * @param frequency Частота
     * @return Количество звуков с указанной частотой
     */
    public Long countByFrequency(Integer frequency) {
        return executeCustomQuerySingle(
                "SELECT COUNT(s) FROM Sound s WHERE s.frequency = :frequency",
                Long.class,
                Map.of("frequency", frequency)
        );
    }

    /**
     * Найти звуки по нескольким типам шума
     * @param noises Список типов шума
     * @return Список звуков с указанными типами шума
     */
    public List<Sound> findByNoises(List<String> noises) {
        return executeQuery(
                "SELECT s FROM Sound s WHERE s.noise IN :noises",
                Map.of("noises", noises)
        );
    }

    /**
     * Найти звуки с частотой в нескольких диапазонах
     * @param frequencyRanges Список диапазонов [min, max]
     * @return Список звуков, попадающих в указанные диапазоны
     */
    public List<Sound> findByFrequencyRanges(List<int[]> frequencyRanges) {
        if (frequencyRanges.isEmpty()) {
            return List.of();
        }

        StringBuilder jpql = new StringBuilder("SELECT s FROM Sound s WHERE ");
        Map<String, Object> params = new java.util.HashMap<>();

        for (int i = 0; i < frequencyRanges.size(); i++) {
            int[] range = frequencyRanges.get(i);
            if (i > 0) {
                jpql.append(" OR ");
            }
            jpql.append("(s.frequency BETWEEN :min").append(i).append(" AND :max").append(i).append(")");
            params.put("min" + i, range[0]);
            params.put("max" + i, range[1]);
        }

        return executeQuery(jpql.toString(), params);
    }

    /**
     * Получить статистику по количеству звуков в разных частотных диапазонах
     * @param rangeSize Размер диапазона (например, 1000 для диапазонов 0-1000, 1001-2000 и т.д.)
     * @return Карта [диапазон, количество звуков]
     */
    public Map<String, Long> getSoundCountByFrequencyRange(int rangeSize) {
        String jpql = "SELECT CONCAT(FLOOR(s.frequency / :rangeSize) * :rangeSize, '-', " +
                "(FLOOR(s.frequency / :rangeSize) + 1) * :rangeSize - 1), " +
                "COUNT(s) FROM Sound s GROUP BY FLOOR(s.frequency / :rangeSize)";

        List<Object[]> results = executeCustomQuery(
                jpql,
                Object[].class,
                Map.of("rangeSize", rangeSize)
        );

        return results.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));
    }
}