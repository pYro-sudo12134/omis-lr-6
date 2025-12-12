package by.losik.lab6omis.repository.general.types;

import by.losik.lab6omis.entities.general.types.Sensor;
import by.losik.lab6omis.entities.general.types.SensorData;
import by.losik.lab6omis.repository.base.BaseRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Репозиторий для управления данными сенсоров (SensorData).
 * Предоставляет полный набор операций CRUD, поиска, фильтрации и статистики.
 *
 * @see SensorData
 * @author Losik Yaroslav
 * @version 1.0
 */
@ApplicationScoped
public class SensorDataRepository extends BaseRepository<SensorData, Long> {

    @Inject
    public SensorDataRepository() {}

    /**
     * Найти данные сенсора по сенсору
     * @param sensor Сенсор
     * @return Список данных указанного сенсора
     */
    public List<SensorData> findBySensor(Sensor sensor) {
        return executeQuery(
                "SELECT sd FROM SensorData sd WHERE sd.sensor = :sensor",
                Map.of("sensor", sensor)
        );
    }

    /**
     * Найти данные сенсора по ID сенсора
     * @param sensorId ID сенсора
     * @return Список данных указанного сенсора
     */
    public List<SensorData> findBySensorId(Long sensorId) {
        return executeQuery(
                "SELECT sd FROM SensorData sd WHERE sd.sensor.id = :sensorId",
                Map.of("sensorId", sensorId)
        );
    }

    /**
     * Найти данные по назначению (точное совпадение)
     * @param purpose Назначение данных
     * @return Optional с найденными данными или пустой
     */
    public Optional<SensorData> findByPurpose(String purpose) {
        return executeQuerySingle(
                "SELECT sd FROM SensorData sd WHERE sd.purpose = :purpose",
                Map.of("purpose", purpose)
        );
    }

    /**
     * Найти данные, содержащие указанный текст в назначении
     * @param text Текст для поиска
     * @return Список данных, содержащих указанный текст в назначении
     */
    public List<SensorData> findByPurposeContaining(String text) {
        return executeQuery(
                "SELECT sd FROM SensorData sd WHERE LOWER(sd.purpose) LIKE LOWER(:text)",
                Map.of("text", "%" + text + "%")
        );
    }

    /**
     * Найти данные по временной метке
     * @param timestamp Временная метка
     * @return Список данных с указанной временной меткой
     */
    public List<SensorData> findByTimestamp(LocalDateTime timestamp) {
        return executeQuery(
                "SELECT sd FROM SensorData sd WHERE sd.timestamp = :timestamp",
                Map.of("timestamp", timestamp)
        );
    }

    /**
     * Найти данные за определенный период
     * @param startDate Начальная дата (включительно)
     * @param endDate Конечная дата (включительно)
     * @return Список данных за указанный период
     */
    public List<SensorData> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return executeQuery(
                "SELECT sd FROM SensorData sd WHERE sd.timestamp BETWEEN :startDate AND :endDate",
                Map.of("startDate", startDate, "endDate", endDate)
        );
    }

    /**
     * Найти данные за последние N дней
     * @param days Количество дней
     * @return Список данных за последние N дней
     */
    public List<SensorData> findRecentData(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        LocalDateTime endDate = LocalDateTime.now();
        return findByTimestampBetween(startDate, endDate);
    }

    /**
     * Найти данные сенсора за определенный период
     * @param sensor Сенсор
     * @param startDate Начальная дата
     * @param endDate Конечная дата
     * @return Список данных сенсора за указанный период
     */
    public List<SensorData> findBySensorAndTimestampBetween(Sensor sensor, LocalDateTime startDate, LocalDateTime endDate) {
        return executeQuery(
                "SELECT sd FROM SensorData sd WHERE sd.sensor = :sensor AND sd.timestamp BETWEEN :startDate AND :endDate",
                Map.of("sensor", sensor, "startDate", startDate, "endDate", endDate)
        );
    }

    /**
     * Найти данные по назначению с сортировкой по времени (по возрастанию)
     * @param purpose Назначение данных
     * @return Отсортированный список данных
     */
    public List<SensorData> findByPurposeOrderByTimestampAsc(String purpose) {
        return executeQuery(
                "SELECT sd FROM SensorData sd WHERE sd.purpose LIKE :purpose ORDER BY sd.timestamp ASC",
                Map.of("purpose", "%" + purpose + "%")
        );
    }

    /**
     * Найти данные по назначению с сортировкой по времени (по убыванию)
     * @param purpose Назначение данных
     * @return Отсортированный список данных
     */
    public List<SensorData> findByPurposeOrderByTimestampDesc(String purpose) {
        return executeQuery(
                "SELECT sd FROM SensorData sd WHERE sd.purpose LIKE :purpose ORDER BY sd.timestamp DESC",
                Map.of("purpose", "%" + purpose + "%")
        );
    }

    /**
     * Найти данные по сенсору с сортировкой по времени
     * @param sensor Сенсор
     * @param ascending true - по возрастанию, false - по убыванию
     * @return Отсортированный список данных
     */
    public List<SensorData> findBySensorOrderByTimestamp(Sensor sensor, boolean ascending) {
        String orderClause = ascending ? "ASC" : "DESC";
        return executeQuery(
                "SELECT sd FROM SensorData sd WHERE sd.sensor = :sensor ORDER BY sd.timestamp " + orderClause,
                Map.of("sensor", sensor)
        );
    }

    /**
     * Найти данные, назначение которых начинается с указанного текста
     * @param prefix Префикс назначения
     * @return Список данных, чьи назначения начинаются с указанного префикса
     */
    public List<SensorData> findByPurposeStartingWith(String prefix) {
        return executeQuery(
                "SELECT sd FROM SensorData sd WHERE sd.purpose LIKE :prefix",
                Map.of("prefix", prefix + "%")
        );
    }

    /**
     * Найти данные, назначение которых заканчивается на указанный текст
     * @param suffix Суффикс назначения
     * @return Список данных, чьи назначения заканчиваются на указанный суффикс
     */
    public List<SensorData> findByPurposeEndingWith(String suffix) {
        return executeQuery(
                "SELECT sd FROM SensorData sd WHERE sd.purpose LIKE :suffix",
                Map.of("suffix", "%" + suffix)
        );
    }

    /**
     * Получить статистику по количеству данных для каждого сенсора
     * @return Карта [ID сенсора, количество данных]
     */
    public Map<Long, Long> getDataCountBySensor() {
        List<Object[]> results = executeCustomQuery(
                "SELECT sd.sensor.id, COUNT(sd) FROM SensorData sd GROUP BY sd.sensor.id",
                Object[].class
        );
        return results.stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],
                        arr -> (Long) arr[1]
                ));
    }

    /**
     * Получить статистику по количеству данных за последние дни
     * @param days Количество дней
     * @return Карта [дата (без времени), количество данных]
     */
    public Map<String, Long> getDataCountByDay(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);

        List<Object[]> results = executeCustomQuery(
                "SELECT DATE(sd.timestamp), COUNT(sd) FROM SensorData sd " +
                        "WHERE sd.timestamp >= :startDate GROUP BY DATE(sd.timestamp)",
                Object[].class,
                Map.of("startDate", startDate)
        );

        return results.stream()
                .collect(Collectors.toMap(
                        arr -> arr[0].toString(),
                        arr -> (Long) arr[1]
                ));
    }

    /**
     * Получить последние данные для каждого сенсора
     * @return Список последних данных по каждому сенсору
     */
    public List<SensorData> findLatestDataForEachSensor() {
        return executeQuery(
                "SELECT sd FROM SensorData sd WHERE sd.timestamp = " +
                        "(SELECT MAX(sd2.timestamp) FROM SensorData sd2 WHERE sd2.sensor = sd.sensor)"
        );
    }

    /**
     * Получить самые старые данные
     * @return Список данных с минимальной временной меткой
     */
    public List<SensorData> findOldestData() {
        return executeQuery(
                "SELECT sd FROM SensorData sd WHERE sd.timestamp = " +
                        "(SELECT MIN(sd2.timestamp) FROM SensorData sd2)"
        );
    }

    /**
     * Получить самые новые данные
     * @return Список данных с максимальной временной меткой
     */
    public List<SensorData> findNewestData() {
        return executeQuery(
                "SELECT sd FROM SensorData sd WHERE sd.timestamp = " +
                        "(SELECT MAX(sd2.timestamp) FROM SensorData sd2)"
        );
    }

    /**
     * Проверить, существуют ли данные для указанного сенсора
     * @param sensor Сенсор
     * @return true если существуют, false в противном случае
     */
    public boolean existsBySensor(Sensor sensor) {
        List<SensorData> results = executeQuery(
                "SELECT sd FROM SensorData sd WHERE sd.sensor = :sensor",
                Map.of("sensor", sensor)
        );
        return !results.isEmpty();
    }

    /**
     * Удалить все данные для указанного сенсора
     * @param sensor Сенсор
     * @return Количество удаленных записей
     */
    public int deleteBySensor(Sensor sensor) {
        return executeQuery(em ->
                em.createQuery("DELETE FROM SensorData sd WHERE sd.sensor = :sensor")
                .setParameter("sensor", sensor)
                .executeUpdate());
    }

    /**
     * Удалить старые данные (до указанной даты)
     * @param cutoffDate Дата, до которой удалять данные
     * @return Количество удаленных записей
     */
    public int deleteOldData(LocalDateTime cutoffDate) {
        return executeQuery(em ->
                em.createQuery("DELETE FROM SensorData sd WHERE sd.timestamp < :cutoffDate")
                .setParameter("cutoffDate", cutoffDate)
                .executeUpdate());
    }

    /**
     * Найти данные по сенсору с пагинацией
     * @param sensor Сенсор
     * @param page Номер страницы
     * @param size Размер страницы
     * @return Список данных для указанной страницы
     */
    public List<SensorData> findBySensorWithPagination(Sensor sensor, int page, int size) {
        return executeQuery(em ->
                em.createQuery("SELECT sd FROM SensorData sd WHERE sd.sensor = :sensor", SensorData.class)
                        .setParameter("sensor", sensor)
                        .setFirstResult(page * size)
                        .setMaxResults(size)
                        .getResultList()
        );
    }

    /**
     * Найти количество данных по сенсору
     * @param sensor Сенсор
     * @return Количество данных для указанного сенсора
     */
    public Long countBySensor(Sensor sensor) {
        return executeCustomQuerySingle(
                "SELECT COUNT(sd) FROM SensorData sd WHERE sd.sensor = :sensor",
                Long.class,
                Map.of("sensor", sensor)
        );
    }

    /**
     * Найти данные по нескольким сенсорам
     * @param sensors Список сенсоров
     * @return Список данных для указанных сенсоров
     */
    public List<SensorData> findBySensors(List<Sensor> sensors) {
        return executeQuery(
                "SELECT sd FROM SensorData sd WHERE sd.sensor IN :sensors",
                Map.of("sensors", sensors)
        );
    }

    /**
     * Найти данные по типу назначения и временному диапазону
     * @param purposePattern Паттерн назначения
     * @param startDate Начальная дата
     * @param endDate Конечная дата
     * @return Список данных, соответствующих критериям
     */
    public List<SensorData> findByPurposePatternAndTimeRange(String purposePattern, LocalDateTime startDate, LocalDateTime endDate) {
        return executeQuery(
                "SELECT sd FROM SensorData sd WHERE sd.purpose LIKE :purposePattern " +
                        "AND sd.timestamp BETWEEN :startDate AND :endDate",
                Map.of("purposePattern", "%" + purposePattern + "%", "startDate", startDate, "endDate", endDate)
        );
    }

    /**
     * Получить распределение данных по часам суток
     * @return Карта [час (0-23), количество данных]
     */
    public Map<Integer, Long> getDataDistributionByHour() {
        List<Object[]> results = executeCustomQuery(
                "SELECT HOUR(sd.timestamp), COUNT(sd) FROM SensorData sd GROUP BY HOUR(sd.timestamp)",
                Object[].class
        );
        return results.stream()
                .collect(Collectors.toMap(
                        arr -> (Integer) arr[0],
                        arr -> (Long) arr[1]
                ));
    }
}