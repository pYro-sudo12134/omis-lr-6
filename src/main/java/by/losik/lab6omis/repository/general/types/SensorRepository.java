package by.losik.lab6omis.repository.general.types;

import by.losik.lab6omis.entities.general.types.Sensor;
import by.losik.lab6omis.repository.base.BaseRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Репозиторий для управления сенсорами (Sensor).
 * Предоставляет полный набор операций CRUD, поиска, фильтрации и статистики.
 *
 * @see Sensor
 * @author Losik Yaroslav
 * @version 1.0
 */
@ApplicationScoped
public class SensorRepository extends BaseRepository<Sensor, Long> {

    @Inject
    public SensorRepository() {}

    /**
     * Найти сенсор по имени (точное совпадение)
     * @param name Имя сенсора
     * @return Optional с найденным сенсором или пустой
     */
    public Optional<Sensor> findByName(String name) {
        return executeQuerySingle(
                "SELECT s FROM Sensor s WHERE s.name = :name",
                Map.of("name", name)
        );
    }

    /**
     * Найти сенсоры, содержащие указанный текст в имени
     * @param text Текст для поиска
     * @return Список сенсоров, содержащих указанный текст в имени
     */
    public List<Sensor> findByNameContaining(String text) {
        return executeQuery(
                "SELECT s FROM Sensor s WHERE LOWER(s.name) LIKE LOWER(:text)",
                Map.of("text", "%" + text + "%")
        );
    }

    /**
     * Найти сенсоры по типу (точное совпадение)
     * @param type Тип сенсора
     * @return Список сенсоров указанного типа
     */
    public List<Sensor> findByType(String type) {
        return executeQuery(
                "SELECT s FROM Sensor s WHERE s.type = :type",
                Map.of("type", type)
        );
    }

    /**
     * Найти сенсоры по типу, содержащему указанный текст
     * @param text Текст для поиска в типе
     * @return Список сенсоров, содержащих указанный текст в типе
     */
    public List<Sensor> findByTypeContaining(String text) {
        return executeQuery(
                "SELECT s FROM Sensor s WHERE LOWER(s.type) LIKE LOWER(:text)",
                Map.of("text", "%" + text + "%")
        );
    }

    /**
     * Найти сенсоры по локации
     * @param location Локация сенсора
     * @return Список сенсоров в указанной локации
     */
    public List<Sensor> findByLocation(String location) {
        return executeQuery(
                "SELECT s FROM Sensor s WHERE s.location = :location",
                Map.of("location", location)
        );
    }

    /**
     * Найти сенсоры по статусу активности
     * @param isActive Статус активности
     * @return Список сенсоров с указанным статусом активности
     */
    public List<Sensor> findByActiveStatus(Boolean isActive) {
        return executeQuery(
                "SELECT s FROM Sensor s WHERE s.isActive = :isActive",
                Map.of("isActive", isActive)
        );
    }

    /**
     * Найти активные сенсоры
     * @return Список активных сенсоров
     */
    public List<Sensor> findActiveSensors() {
        return findByActiveStatus(true);
    }

    /**
     * Найти неактивные сенсоры
     * @return Список неактивных сенсоров
     */
    public List<Sensor> findInactiveSensors() {
        return findByActiveStatus(false);
    }

    /**
     * Найти сенсоры по локации, содержащей указанный текст
     * @param text Текст для поиска в локации
     * @return Список сенсоров, содержащих указанный текст в локации
     */
    public List<Sensor> findByLocationContaining(String text) {
        return executeQuery(
                "SELECT s FROM Sensor s WHERE LOWER(s.location) LIKE LOWER(:text)",
                Map.of("text", "%" + text + "%")
        );
    }

    /**
     * Найти сенсоры по имени и типу
     * @param name Имя сенсора
     * @param type Тип сенсора
     * @return Список сенсоров, соответствующих обоим критериям
     */
    public List<Sensor> findByNameAndType(String name, String type) {
        return executeQuery(
                "SELECT s FROM Sensor s WHERE s.name = :name AND s.type = :type",
                Map.of("name", name, "type", type)
        );
    }

    /**
     * Найти сенсоры по типу с сортировкой по имени (по возрастанию)
     * @param type Тип сенсора
     * @return Отсортированный список сенсоров
     */
    public List<Sensor> findByTypeOrderByNameAsc(String type) {
        return executeQuery(
                "SELECT s FROM Sensor s WHERE s.type = :type ORDER BY s.name ASC",
                Map.of("type", type)
        );
    }

    /**
     * Найти сенсоры по типу с сортировкой по имени (по убыванию)
     * @param type Тип сенсора
     * @return Отсортированный список сенсоров
     */
    public List<Sensor> findByTypeOrderByNameDesc(String type) {
        return executeQuery(
                "SELECT s FROM Sensor s WHERE s.type = :type ORDER BY s.name DESC",
                Map.of("type", type)
        );
    }

    /**
     * Найти сенсоры, имя которых начинается с указанного текста
     * @param prefix Префикс имени
     * @return Список сенсоров, чьи имена начинаются с указанного префикса
     */
    public List<Sensor> findByNameStartingWith(String prefix) {
        return executeQuery(
                "SELECT s FROM Sensor s WHERE s.name LIKE :prefix",
                Map.of("prefix", prefix + "%")
        );
    }

    /**
     * Найти сенсоры, имя которых заканчивается на указанный текст
     * @param suffix Суффикс имени
     * @return Список сенсоров, чьи имена заканчиваются на указанный суффикс
     */
    public List<Sensor> findByNameEndingWith(String suffix) {
        return executeQuery(
                "SELECT s FROM Sensor s WHERE s.name LIKE :suffix",
                Map.of("suffix", "%" + suffix)
        );
    }

    /**
     * Получить статистику по количеству сенсоров каждого типа
     * @return Карта [тип сенсора, количество]
     */
    public Map<String, Long> getSensorCountByType() {
        List<Object[]> results = executeCustomQuery(
                "SELECT s.type, COUNT(s) FROM Sensor s GROUP BY s.type",
                Object[].class
        );
        return results.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));
    }

    /**
     * Получить статистику по количеству сенсоров в каждой локации
     * @return Карта [локация, количество сенсоров]
     */
    public Map<String, Long> getSensorCountByLocation() {
        List<Object[]> results = executeCustomQuery(
                "SELECT s.location, COUNT(s) FROM Sensor s WHERE s.location IS NOT NULL GROUP BY s.location",
                Object[].class
        );
        return results.stream()
                .collect(Collectors.toMap(
                        arr -> (String) arr[0],
                        arr -> (Long) arr[1]
                ));
    }

    /**
     * Получить статистику по количеству активных/неактивных сенсоров
     * @return Карта [статус активности, количество]
     */
    public Map<Boolean, Long> getSensorCountByActiveStatus() {
        List<Object[]> results = executeCustomQuery(
                "SELECT s.isActive, COUNT(s) FROM Sensor s GROUP BY s.isActive",
                Object[].class
        );
        return results.stream()
                .collect(Collectors.toMap(
                        arr -> (Boolean) arr[0],
                        arr -> (Long) arr[1]
                ));
    }

    /**
     * Проверить, существует ли сенсор с указанным именем
     * @param name Имя сенсора
     * @return true если существует, false в противном случае
     */
    public boolean existsByName(String name) {
        List<Sensor> results = executeQuery(
                "SELECT s FROM Sensor s WHERE s.name = :name",
                Map.of("name", name)
        );
        return !results.isEmpty();
    }

    /**
     * Удалить все сенсоры указанного типа
     * @param type Тип сенсора
     * @return Количество удаленных записей
     */
    public int deleteByType(String type) {
        return executeQuery(em -> em.createQuery("DELETE FROM Sensor s WHERE s.type = :type")
                .setParameter("type", type)
                .executeUpdate());
    }

    /**
     * Удалить все сенсоры в указанной локации
     * @param location Локация
     * @return Количество удаленных записей
     */
    public int deleteByLocation(String location) {
        return executeQuery(em -> em.createQuery("DELETE FROM Sensor s WHERE s.location = :location")
                .setParameter("location", location)
                .executeUpdate());
    }

    /**
     * Найти сенсоры по типу с пагинацией
     * @param type Тип сенсора
     * @param page Номер страницы
     * @param size Размер страницы
     * @return Список сенсоров для указанной страницы
     */
    public List<Sensor> findByTypeWithPagination(String type, int page, int size) {
        return executeQuery(em ->
                em.createQuery("SELECT s FROM Sensor s WHERE s.type = :type", Sensor.class)
                        .setParameter("type", type)
                        .setFirstResult(page * size)
                        .setMaxResults(size)
                        .getResultList()
        );
    }

    /**
     * Найти количество сенсоров по типу
     * @param type Тип сенсора
     * @return Количество сенсоров указанного типа
     */
    public Long countByType(String type) {
        return executeCustomQuerySingle(
                "SELECT COUNT(s) FROM Sensor s WHERE s.type = :type",
                Long.class,
                Map.of("type", type)
        );
    }

    /**
     * Найти сенсоры по нескольким типам
     * @param types Список типов
     * @return Список сенсоров указанных типов
     */
    public List<Sensor> findByTypes(List<String> types) {
        return executeQuery(
                "SELECT s FROM Sensor s WHERE s.type IN :types",
                Map.of("types", types)
        );
    }

    /**
     * Найти сенсоры по нескольким локациям
     * @param locations Список локаций
     * @return Список сенсоров в указанных локациях
     */
    public List<Sensor> findByLocations(List<String> locations) {
        return executeQuery(
                "SELECT s FROM Sensor s WHERE s.location IN :locations",
                Map.of("locations", locations)
        );
    }

    /**
     * Найти сенсоры без указанной локации
     * @return Список сенсоров без локации
     */
    public List<Sensor> findSensorsWithoutLocation() {
        return executeQuery(
                "SELECT s FROM Sensor s WHERE s.location IS NULL"
        );
    }

    /**
     * Обновить статус активности сенсора
     * @param sensorId ID сенсора
     * @param isActive Новый статус активности
     * @return Количество обновленных записей
     */
    public int updateSensorActivity(Long sensorId, Boolean isActive) {
        return executeQuery(em -> em.createQuery("UPDATE Sensor s SET s.isActive = :isActive WHERE s.id = :sensorId")
                .setParameter("isActive", isActive)
                .setParameter("sensorId", sensorId)
                .executeUpdate());
    }

    /**
     * Активировать все сенсоры указанного типа
     * @param type Тип сенсора
     * @return Количество обновленных записей
     */
    public int activateSensorsByType(String type) {
        return executeQuery(em -> em.createQuery("UPDATE Sensor s SET s.isActive = true WHERE s.type = :type")
                .setParameter("type", type)
                .executeUpdate());
    }

    /**
     * Деактивировать все сенсоры указанного типа
     * @param type Тип сенсора
     * @return Количество обновленных записей
     */
    public int deactivateSensorsByType(String type) {
        return executeQuery(em -> em.createQuery("UPDATE Sensor s SET s.isActive = false WHERE s.type = :type")
                .setParameter("type", type)
                .executeUpdate());
    }

    /**
     * Найти сенсоры с похожими именами (по шаблону)
     * @param namePattern Паттерн имени (можно использовать % и _)
     * @return Список сенсоров, имена которых соответствуют паттерну
     */
    public List<Sensor> findByNamePattern(String namePattern) {
        return executeQuery(
                "SELECT s FROM Sensor s WHERE s.name LIKE :namePattern",
                Map.of("namePattern", namePattern)
        );
    }

    /**
     * Получить сенсоры с количеством связанных данных
     * @return Список массивов [сенсор, количество данных]
     */
    public List<Object[]> getSensorsWithDataCount() {
        return executeCustomQuery(
                "SELECT s, COUNT(sd) FROM Sensor s LEFT JOIN s.sensorDataList sd GROUP BY s",
                Object[].class
        );
    }

    /**
     * Найти сенсоры, у которых нет связанных данных
     * @return Список сенсоров без данных
     */
    public List<Sensor> findSensorsWithoutData() {
        return executeQuery(
                "SELECT s FROM Sensor s WHERE s.sensorDataList IS EMPTY"
        );
    }

    /**
     * Найти сенсоры с наибольшим количеством данных
     * @param limit Максимальное количество возвращаемых записей
     * @return Список сенсоров с наибольшим количеством данных
     */
    public List<Sensor> findTopSensorsByDataCount(int limit) {
        return executeQuery(em ->
                em.createQuery(
                                "SELECT s FROM Sensor s LEFT JOIN s.sensorDataList sd " +
                                        "GROUP BY s ORDER BY COUNT(sd) DESC",
                                Sensor.class
                        )
                        .setMaxResults(limit)
                        .getResultList()
        );
    }
}