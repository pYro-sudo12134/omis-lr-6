package by.losik.lab6omis.service.general.types;

import by.losik.lab6omis.entities.general.types.Sensor;
import by.losik.lab6omis.repository.general.types.SensorRepository;
import by.losik.lab6omis.service.base.BaseService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Map;

/**
 * Сервис для управления сенсорами (Sensor).
 * Предоставляет полный набор операций CRUD, поиска, фильтрации и статистики.
 *
 * @see Sensor
 * @see SensorRepository
 * @author Losik Yaroslav
 * @version 1.0
 */
@ApplicationScoped
@Transactional
public class SensorService extends BaseService<Sensor, Long> {

    @Inject
    SensorRepository sensorRepository;

    /**
     * Создает новый сенсор.
     *
     * @param sensor объект сенсора для создания (должен быть валидным)
     * @return созданный сенсор с присвоенным ID
     * @throws IllegalArgumentException если сенсор с таким именем уже существует
     */
    public Sensor createSensor(@Valid Sensor sensor) {
        return executeWithLogging(
                String.format("Создание сенсора: имя='%s', тип='%s'", sensor.getName(), sensor.getType()),
                () -> {
                    validateSensor(sensor);
                    validateUnique(sensor.getName(),
                            () -> sensorRepository.existsByName(sensor.getName()),
                            "именем", "Сенсор");

                    if (sensor.getIsActive() == null) {
                        sensor.setIsActive(true);
                    }

                    return sensorRepository.create(sensor);
                }
        );
    }

    /**
     * Получает сенсор по его идентификатору.
     *
     * @param id идентификатор сенсора
     * @return найденный сенсор
     * @throws NotFoundException если сенсор с указанным ID не найден
     */
    public Sensor getById(Long id) {
        return getEntityById(
                id,
                () -> sensorRepository.findById(id),
                "Сенсор"
        );
    }

    /**
     * Получает все сенсоры.
     *
     * @return список всех сенсоров
     */
    public List<Sensor> getAllSensors() {
        return executeWithLogging(
                "Получение всех сенсоров",
                sensorRepository::findAll
        );
    }

    /**
     * Получает сенсоры с пагинацией.
     *
     * @param page номер страницы (начинается с 0)
     * @param size размер страницы
     * @return список сенсоров для указанной страницы
     * @throws IllegalArgumentException если параметры пагинации некорректны
     */
    public List<Sensor> getAllSensors(int page, int size) {
        return executeWithLogging(
                String.format("Получение сенсоров с пагинацией: page=%d, size=%d", page, size),
                () -> {
                    validatePagination(page, size);
                    return sensorRepository.findAll(page, size);
                }
        );
    }

    /**
     * Обновляет существующий сенсор.
     *
     * @param id идентификатор обновляемого сенсора
     * @param updatedSensor объект с обновленными данными
     * @return обновленный сенсор
     * @throws NotFoundException если сенсор с указанным ID не найден
     * @throws IllegalArgumentException если новое имя сенсора уже используется
     */
    public Sensor updateSensor(Long id, @Valid Sensor updatedSensor) {
        return executeWithLogging(
                String.format("Обновление сенсора ID %d", id),
                () -> {
                    Sensor existingSensor = getById(id);
                    validateSensor(updatedSensor);

                    if (!existingSensor.getName().equals(updatedSensor.getName())) {
                        validateUnique(updatedSensor.getName(),
                                () -> sensorRepository.existsByName(updatedSensor.getName()),
                                "именем", "Сенсор");
                    }

                    existingSensor.setName(updatedSensor.getName());
                    existingSensor.setType(updatedSensor.getType());
                    existingSensor.setLocation(updatedSensor.getLocation());
                    existingSensor.setIsActive(updatedSensor.getIsActive());

                    return sensorRepository.save(existingSensor);
                }
        );
    }

    /**
     * Удаляет сенсор по идентификатору.
     *
     * @param id идентификатор сенсора для удаления
     * @throws NotFoundException если сенсор с указанным ID не найден
     */
    public void deleteSensor(Long id) {
        executeVoidWithLogging(
                String.format("Удаление сенсора ID %d", id),
                () -> {
                    ensureEntityExists(id,
                            () -> sensorRepository.existsById(id),
                            "Сенсор");
                    sensorRepository.deleteById(id);
                }
        );
    }

    /**
     * Получает сенсор по точному имени.
     *
     * @param name имя сенсора для поиска
     * @return найденный сенсор
     * @throws IllegalArgumentException если имя не соответствует ограничениям
     * @throws NotFoundException если сенсор с указанным именем не найден
     */
    public Sensor getByName(String name) {
        return executeWithLogging(
                String.format("Поиск сенсора по имени: '%s'", name),
                () -> {
                    validateStringLength(name, "Имя сенсора", 2, 100);
                    return sensorRepository.findByName(name.trim())
                            .orElseThrow(() -> new NotFoundException(
                                    String.format("Сенсор с именем '%s' не найден", name)
                            ));
                }
        );
    }

    /**
     * Ищет сенсоры по частичному совпадению имени.
     *
     * @param searchText текст для поиска в имени
     * @return список сенсоров, содержащих указанный текст в имени
     */
    public List<Sensor> searchByName(String searchText) {
        return executeWithLogging(
                String.format("Поиск сенсоров по имени: '%s'", searchText),
                () -> {
                    if (searchText == null || searchText.trim().isEmpty()) {
                        return List.of();
                    }
                    return sensorRepository.findByNameContaining(searchText.trim());
                }
        );
    }

    /**
     * Получает сенсоры по точному типу.
     *
     * @param type тип сенсора для поиска
     * @return список сенсоров указанного типа
     */
    public List<Sensor> getByType(String type) {
        return executeWithLogging(
                String.format("Поиск сенсоров по типу: '%s'", type),
                () -> {
                    if (type == null || type.trim().isEmpty()) {
                        return List.of();
                    }
                    return sensorRepository.findByType(type.trim());
                }
        );
    }

    /**
     * Ищет сенсоры по частичному совпадению типа.
     *
     * @param searchText текст для поиска в типе
     * @return список сенсоров, содержащих указанный текст в типе
     */
    public List<Sensor> searchByType(String searchText) {
        return executeWithLogging(
                String.format("Поиск сенсоров по типу: '%s'", searchText),
                () -> {
                    if (searchText == null || searchText.trim().isEmpty()) {
                        return List.of();
                    }
                    return sensorRepository.findByTypeContaining(searchText.trim());
                }
        );
    }

    /**
     * Получает сенсоры по локации.
     *
     * @param location локация для поиска
     * @return список сенсоров в указанной локации
     */
    public List<Sensor> getByLocation(String location) {
        return executeWithLogging(
                String.format("Поиск сенсоров по локации: '%s'", location),
                () -> {
                    if (location == null || location.trim().isEmpty()) {
                        return List.of();
                    }
                    return sensorRepository.findByLocation(location.trim());
                }
        );
    }

    /**
     * Получает сенсоры по статусу активности.
     *
     * @param isActive статус активности (true - активные, false - неактивные)
     * @return список сенсоров с указанным статусом активности
     */
    public List<Sensor> getByActiveStatus(Boolean isActive) {
        return executeWithLogging(
                String.format("Поиск сенсоров по статусу активности: %s", isActive),
                () -> sensorRepository.findByActiveStatus(isActive)
        );
    }

    /**
     * Получает активные сенсоры.
     *
     * @return список активных сенсоров
     */
    public List<Sensor> getActiveSensors() {
        return executeWithLogging(
                "Получение активных сенсоров",
                sensorRepository::findActiveSensors
        );
    }

    /**
     * Получает неактивные сенсоры.
     *
     * @return список неактивных сенсоров
     */
    public List<Sensor> getInactiveSensors() {
        return executeWithLogging(
                "Получение неактивных сенсоров",
                sensorRepository::findInactiveSensors
        );
    }

    /**
     * Ищет сенсоры по частичному совпадению локации.
     *
     * @param searchText текст для поиска в локации
     * @return список сенсоров, содержащих указанный текст в локации
     */
    public List<Sensor> searchByLocation(String searchText) {
        return executeWithLogging(
                String.format("Поиск сенсоров по локации: '%s'", searchText),
                () -> {
                    if (searchText == null || searchText.trim().isEmpty()) {
                        return List.of();
                    }
                    return sensorRepository.findByLocationContaining(searchText.trim());
                }
        );
    }

    /**
     * Получает сенсоры по имени и типу.
     *
     * @param name имя сенсора
     * @param type тип сенсора
     * @return список сенсоров, соответствующих обоим критериям
     */
    public List<Sensor> getByNameAndType(String name, String type) {
        return executeWithLogging(
                String.format("Поиск сенсоров по имени '%s' и типу '%s'", name, type),
                () -> {
                    if (name == null || name.trim().isEmpty() || type == null || type.trim().isEmpty()) {
                        return List.of();
                    }
                    return sensorRepository.findByNameAndType(name.trim(), type.trim());
                }
        );
    }

    /**
     * Получает сенсоры по типу с сортировкой по имени (по возрастанию).
     *
     * @param type тип сенсора
     * @return отсортированный список сенсоров
     */
    public List<Sensor> getByTypeOrderByNameAsc(String type) {
        return executeWithLogging(
                String.format("Поиск сенсоров по типу '%s' с сортировкой по имени (возр.)", type),
                () -> {
                    if (type == null || type.trim().isEmpty()) {
                        return List.of();
                    }
                    return sensorRepository.findByTypeOrderByNameAsc(type.trim());
                }
        );
    }

    /**
     * Получает сенсоры по типу с сортировкой по имени (по убыванию).
     *
     * @param type тип сенсора
     * @return отсортированный список сенсоров
     */
    public List<Sensor> getByTypeOrderByNameDesc(String type) {
        return executeWithLogging(
                String.format("Поиск сенсоров по типу '%s' с сортировкой по имени (убыв.)", type),
                () -> {
                    if (type == null || type.trim().isEmpty()) {
                        return List.of();
                    }
                    return sensorRepository.findByTypeOrderByNameDesc(type.trim());
                }
        );
    }

    /**
     * Получает сенсоры, имена которых начинаются с указанного префикса.
     *
     * @param prefix префикс имени
     * @return список сенсоров, чьи имена начинаются с указанного префикса
     */
    public List<Sensor> getByNameStartingWith(String prefix) {
        return executeWithLogging(
                String.format("Поиск сенсоров по префиксу имени: '%s'", prefix),
                () -> {
                    if (prefix == null || prefix.trim().isEmpty()) {
                        return List.of();
                    }
                    return sensorRepository.findByNameStartingWith(prefix.trim());
                }
        );
    }

    /**
     * Получает сенсоры, имена которых заканчиваются на указанный суффикс.
     *
     * @param suffix суффикс имени
     * @return список сенсоров, чьи имена заканчиваются на указанный суффикс
     */
    public List<Sensor> getByNameEndingWith(String suffix) {
        return executeWithLogging(
                String.format("Поиск сенсоров по суффиксу имени: '%s'", suffix),
                () -> {
                    if (suffix == null || suffix.trim().isEmpty()) {
                        return List.of();
                    }
                    return sensorRepository.findByNameEndingWith(suffix.trim());
                }
        );
    }

    /**
     * Получает статистику по количеству сенсоров каждого типа.
     *
     * @return карта [тип сенсора, количество]
     */
    public Map<String, Long> getSensorCountByType() {
        return executeWithLogging(
                "Получение статистики сенсоров по типам",
                sensorRepository::getSensorCountByType
        );
    }

    /**
     * Получает статистику по количеству сенсоров в каждой локации.
     *
     * @return карта [локация, количество сенсоров]
     */
    public Map<String, Long> getSensorCountByLocation() {
        return executeWithLogging(
                "Получение статистики сенсоров по локациям",
                sensorRepository::getSensorCountByLocation
        );
    }

    /**
     * Получает статистику по количеству активных/неактивных сенсоров.
     *
     * @return карта [статус активности, количество]
     */
    public Map<Boolean, Long> getSensorCountByActiveStatus() {
        return executeWithLogging(
                "Получение статистики сенсоров по статусу активности",
                sensorRepository::getSensorCountByActiveStatus
        );
    }

    /**
     * Проверяет, существует ли сенсор с указанным именем.
     *
     * @param name имя сенсора для проверки
     * @return true если существует, false в противном случае
     */
    public boolean existsByName(String name) {
        return executeWithLogging(
                String.format("Проверка существования сенсора с именем: '%s'", name),
                () -> sensorRepository.existsByName(name)
        );
    }

    /**
     * Удаляет все сенсоры указанного типа.
     *
     * @param type тип сенсора для удаления
     * @return количество удаленных записей
     */
    public int deleteByType(String type) {
        return executeWithLogging(
                String.format("Удаление сенсоров типа: '%s'", type),
                () -> {
                    if (type == null || type.trim().isEmpty()) {
                        return 0;
                    }
                    return sensorRepository.deleteByType(type.trim());
                }
        );
    }

    /**
     * Удаляет все сенсоры в указанной локации.
     *
     * @param location локация для удаления
     * @return количество удаленных записей
     */
    public int deleteByLocation(String location) {
        return executeWithLogging(
                String.format("Удаление сенсоров в локации: '%s'", location),
                () -> {
                    if (location == null || location.trim().isEmpty()) {
                        return 0;
                    }
                    return sensorRepository.deleteByLocation(location.trim());
                }
        );
    }

    /**
     * Получает сенсоры по типу с пагинацией.
     *
     * @param type тип сенсора
     * @param page номер страницы (начинается с 0)
     * @param size размер страницы
     * @return список сенсоров для указанной страницы
     * @throws IllegalArgumentException если параметры пагинации некорректны
     */
    public List<Sensor> getByTypeWithPagination(String type, int page, int size) {
        return executeWithLogging(
                String.format("Поиск сенсоров по типу '%s' с пагинацией: page=%d, size=%d", type, page, size),
                () -> {
                    validatePagination(page, size);
                    if (type == null || type.trim().isEmpty()) {
                        return List.of();
                    }
                    return sensorRepository.findByTypeWithPagination(type.trim(), page, size);
                }
        );
    }

    /**
     * Получает количество сенсоров указанного типа.
     *
     * @param type тип сенсора
     * @return количество сенсоров указанного типа
     */
    public Long countByType(String type) {
        return executeWithLogging(
                String.format("Получение количества сенсоров типа: '%s'", type),
                () -> {
                    if (type == null || type.trim().isEmpty()) {
                        return 0L;
                    }
                    return sensorRepository.countByType(type.trim());
                }
        );
    }

    /**
     * Получает сенсоры по нескольким типам.
     *
     * @param types список типов сенсоров
     * @return список сенсоров указанных типов
     */
    public List<Sensor> getByTypes(List<String> types) {
        return executeWithLogging(
                String.format("Поиск сенсоров по типам: %s", types),
                () -> {
                    List<String> cleanedTypes = cleanStringList(types);
                    if (cleanedTypes.isEmpty()) {
                        return List.of();
                    }
                    return sensorRepository.findByTypes(cleanedTypes);
                }
        );
    }

    /**
     * Получает сенсоры по нескольким локациям.
     *
     * @param locations список локаций
     * @return список сенсоров в указанных локациях
     */
    public List<Sensor> getByLocations(List<String> locations) {
        return executeWithLogging(
                String.format("Поиск сенсоров по локациям: %s", locations),
                () -> {
                    List<String> cleanedLocations = cleanStringList(locations);
                    if (cleanedLocations.isEmpty()) {
                        return List.of();
                    }
                    return sensorRepository.findByLocations(cleanedLocations);
                }
        );
    }

    /**
     * Получает сенсоры без указанной локации.
     *
     * @return список сенсоров без локации
     */
    public List<Sensor> getSensorsWithoutLocation() {
        return executeWithLogging(
                "Поиск сенсоров без локации",
                sensorRepository::findSensorsWithoutLocation
        );
    }

    /**
     * Обновляет статус активности сенсора.
     *
     * @param sensorId ID сенсора
     * @param isActive новый статус активности
     * @return количество обновленных записей (обычно 1)
     * @throws NotFoundException если сенсор с указанным ID не найден
     */
    public int updateSensorActivity(Long sensorId, Boolean isActive) {
        return executeWithLogging(
                String.format("Обновление активности сенсора ID %d на %s", sensorId, isActive),
                () -> {
                    ensureEntityExists(sensorId,
                            () -> sensorRepository.existsById(sensorId),
                            "Сенсор");
                    return sensorRepository.updateSensorActivity(sensorId, isActive);
                }
        );
    }

    /**
     * Активирует все сенсоры указанного типа.
     *
     * @param type тип сенсора
     * @return количество обновленных записей
     */
    public int activateSensorsByType(String type) {
        return executeWithLogging(
                String.format("Активация всех сенсоров типа: '%s'", type),
                () -> {
                    if (type == null || type.trim().isEmpty()) {
                        return 0;
                    }
                    return sensorRepository.activateSensorsByType(type.trim());
                }
        );
    }

    /**
     * Деактивирует все сенсоры указанного типа.
     *
     * @param type тип сенсора
     * @return количество обновленных записей
     */
    public int deactivateSensorsByType(String type) {
        return executeWithLogging(
                String.format("Деактивация всех сенсоров типа: '%s'", type),
                () -> {
                    if (type == null || type.trim().isEmpty()) {
                        return 0;
                    }
                    return sensorRepository.deactivateSensorsByType(type.trim());
                }
        );
    }

    /**
     * Ищет сенсоры по шаблону имени.
     *
     * @param namePattern паттерн имени (можно использовать % и _)
     * @return список сенсоров, имена которых соответствуют паттерну
     */
    public List<Sensor> getByNamePattern(String namePattern) {
        return executeWithLogging(
                String.format("Поиск сенсоров по шаблону имени: '%s'", namePattern),
                () -> {
                    if (namePattern == null || namePattern.trim().isEmpty()) {
                        return List.of();
                    }
                    return sensorRepository.findByNamePattern(namePattern.trim());
                }
        );
    }

    /**
     * Получает сенсоры с количеством связанных данных.
     *
     * @return список массивов [сенсор, количество данных]
     */
    public List<Object[]> getSensorsWithDataCount() {
        return executeWithLogging(
                "Получение сенсоров с количеством связанных данных",
                sensorRepository::getSensorsWithDataCount
        );
    }

    /**
     * Получает сенсоры без связанных данных.
     *
     * @return список сенсоров без данных
     */
    public List<Sensor> getSensorsWithoutData() {
        return executeWithLogging(
                "Поиск сенсоров без связанных данных",
                sensorRepository::findSensorsWithoutData
        );
    }

    /**
     * Получает сенсоры с наибольшим количеством данных.
     *
     * @param limit максимальное количество возвращаемых записей
     * @return список сенсоров с наибольшим количеством данных
     * @throws IllegalArgumentException если limit <= 0
     */
    public List<Sensor> getTopSensorsByDataCount(int limit) {
        return executeWithLogging(
                String.format("Получение топ-%d сенсоров по количеству данных", limit),
                () -> {
                    if (limit <= 0) {
                        throw new IllegalArgumentException("Лимит должен быть положительным числом");
                    }
                    return sensorRepository.findTopSensorsByDataCount(limit);
                }
        );
    }

    /**
     * Получает общее количество сенсоров.
     *
     * @return общее количество сенсоров
     */
    public long countAllSensors() {
        return executeWithLogging(
                "Получение общего количества сенсоров",
                sensorRepository::count
        );
    }

    /**
     * Проверяет, является ли сенсор новым (не сохраненным в БД).
     *
     * @param sensor объект сенсора
     * @return true если сенсор новый, false если существует в БД
     */
    public boolean isNewSensor(Sensor sensor) {
        return executeWithLogging(
                "Проверка, является ли сенсор новым",
                () -> sensorRepository.isNew(sensor)
        );
    }

    /**
     * Валидирует объект сенсора.
     *
     * @param sensor объект сенсора для валидации
     * @throws IllegalArgumentException если сенсор не соответствует требованиям
     */
    private void validateSensor(Sensor sensor) {
        validateNotNull(sensor, "Сенсор");
        validateStringLength(sensor.getName(), "Имя сенсора", 2, 100);
        validateStringLength(sensor.getType(), "Тип сенсора", 2, 50);

        if (sensor.getLocation() != null && !sensor.getLocation().trim().isEmpty()) {
            validateStringLength(sensor.getLocation().trim(), "Локация сенсора", 1, 200);
        }
    }
}