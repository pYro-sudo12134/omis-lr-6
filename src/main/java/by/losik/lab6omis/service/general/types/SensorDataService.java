package by.losik.lab6omis.service.general.types;

import by.losik.lab6omis.entities.general.types.Sensor;
import by.losik.lab6omis.entities.general.types.SensorData;
import by.losik.lab6omis.repository.general.types.SensorDataRepository;
import by.losik.lab6omis.service.base.BaseService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Сервис для управления данными сенсоров (SensorData).
 * Предоставляет полный набор операций CRUD, поиска, фильтрации и статистики.
 *
 * @see SensorData
 * @see SensorDataRepository
 * @author Losik Yaroslav
 * @version 1.0
 */
@ApplicationScoped
@Transactional
public class SensorDataService extends BaseService<SensorData, Long> {

    @Inject
    SensorDataRepository sensorDataRepository;

    /**
     * Создает новые данные сенсора в системе.
     *
     * @param sensorData объект данных сенсора для создания (должен быть валидным)
     * @return сохраненный объект данных сенсора
     * @throws IllegalArgumentException если параметры данных некорректны
     */
    public SensorData createSensorData(@Valid SensorData sensorData) {
        return executeWithLogging(
                String.format("Создание данных сенсора: сенсор ID=%d, назначение='%s', время=%s",
                        sensorData.getSensor().getId(), sensorData.getPurpose(), sensorData.getTimestamp()),
                () -> {
                    validateSensorData(sensorData);
                    return sensorDataRepository.create(sensorData);
                }
        );
    }

    /**
     * Получает данные сенсора по его идентификатору.
     *
     * @param id идентификатор данных сенсора
     * @return найденный объект данных сенсора
     * @throws NotFoundException если данные сенсора с указанным ID не найдены
     */
    public SensorData getById(Long id) {
        return getEntityById(
                id,
                () -> sensorDataRepository.findById(id),
                "Данные сенсора"
        );
    }

    /**
     * Получает все данные сенсоров из системы.
     *
     * @return список всех данных сенсоров
     */
    public List<SensorData> getAllSensorData() {
        return executeWithLogging(
                "Получение всех данных сенсоров",
                sensorDataRepository::findAll
        );
    }

    /**
     * Получает данные сенсоров с пагинацией.
     *
     * @param page номер страницы (начинается с 0)
     * @param size количество элементов на странице
     * @return список данных сенсоров на указанной странице
     * @throws IllegalArgumentException если параметры пагинации некорректны
     */
    public List<SensorData> getAllSensorData(int page, int size) {
        return executeWithLogging(
                String.format("Получение данных сенсоров с пагинацией: page=%d, size=%d", page, size),
                () -> {
                    validatePagination(page, size);
                    return sensorDataRepository.findAll(page, size);
                }
        );
    }

    /**
     * Обновляет существующие данные сенсора.
     *
     * @param id идентификатор обновляемых данных сенсора
     * @param updatedSensorData обновленные данные сенсора
     * @return обновленный объект данных сенсора
     * @throws NotFoundException если данные сенсора с указанным ID не найдены
     * @throws IllegalArgumentException если параметры данных некорректны
     */
    public SensorData updateSensorData(Long id, @Valid SensorData updatedSensorData) {
        return executeWithLogging(
                String.format("Обновление данных сенсора ID %d", id),
                () -> {
                    SensorData existingSensorData = getById(id);
                    validateSensorData(updatedSensorData);

                    existingSensorData.setTimestamp(updatedSensorData.getTimestamp());
                    existingSensorData.setPurpose(updatedSensorData.getPurpose());
                    existingSensorData.setSensor(updatedSensorData.getSensor());

                    return sensorDataRepository.save(existingSensorData);
                }
        );
    }

    /**
     * Удаляет данные сенсора по его идентификатору.
     *
     * @param id идентификатор удаляемых данных сенсора
     * @throws NotFoundException если данные сенсора с указанным ID не найдены
     */
    public void deleteSensorData(Long id) {
        executeVoidWithLogging(
                String.format("Удаление данных сенсора ID %d", id),
                () -> {
                    ensureEntityExists(id,
                            () -> sensorDataRepository.existsById(id),
                            "Данные сенсора");
                    sensorDataRepository.deleteById(id);
                }
        );
    }

    /**
     * Получает данные сенсора по сенсору.
     *
     * @param sensor сенсор для фильтрации данных
     * @return список данных указанного сенсора
     */
    public List<SensorData> getBySensor(Sensor sensor) {
        return executeWithLogging(
                String.format("Поиск данных по сенсору ID: %d", sensor.getId()),
                () -> {
                    validateNotNull(sensor, "Сенсор");
                    return sensorDataRepository.findBySensor(sensor);
                }
        );
    }

    /**
     * Получает данные сенсора по ID сенсора.
     *
     * @param sensorId ID сенсора
     * @return список данных указанного сенсора
     * @throws IllegalArgumentException если ID сенсора некорректен
     */
    public List<SensorData> getBySensorId(Long sensorId) {
        return executeWithLogging(
                String.format("Поиск данных по ID сенсора: %d", sensorId),
                () -> {
                    validatePositive(sensorId, "ID сенсора");
                    return sensorDataRepository.findBySensorId(sensorId);
                }
        );
    }

    /**
     * Получает данные по назначению (точное совпадение).
     *
     * @param purpose назначение данных
     * @return найденные данные
     * @throws IllegalArgumentException если назначение некорректно
     * @throws NotFoundException если данные не найдены
     */
    public SensorData getByPurpose(String purpose) {
        return executeWithLogging(
                String.format("Поиск данных по назначению: '%s'", purpose),
                () -> {
                    validatePurpose(purpose);
                    return sensorDataRepository.findByPurpose(purpose)
                            .orElseThrow(() -> new NotFoundException(
                                    String.format("Данные с назначением '%s' не найдены", purpose)
                            ));
                }
        );
    }

    /**
     * Ищет данные сенсоров по частичному совпадению назначения.
     *
     * @param searchText текст для поиска в назначении
     * @return список найденных данных
     */
    public List<SensorData> searchByPurpose(String searchText) {
        return executeWithLogging(
                String.format("Поиск данных по назначению: '%s'", searchText),
                () -> {
                    if (searchText == null || searchText.trim().isEmpty()) {
                        return List.of();
                    }
                    return sensorDataRepository.findByPurposeContaining(searchText.trim());
                }
        );
    }

    /**
     * Получает данные по временной метке.
     *
     * @param timestamp временная метка
     * @return список данных с указанной временной меткой
     */
    public List<SensorData> getByTimestamp(LocalDateTime timestamp) {
        return executeWithLogging(
                String.format("Поиск данных по временной метке: %s", timestamp),
                () -> {
                    validateTimestamp(timestamp);
                    return sensorDataRepository.findByTimestamp(timestamp);
                }
        );
    }

    /**
     * Получает данные сенсоров за определенный период времени.
     *
     * @param startDate начальная дата (включительно)
     * @param endDate конечная дата (включительно)
     * @return список данных за указанный период
     * @throws IllegalArgumentException если временной диапазон некорректен
     */
    public List<SensorData> getByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return executeWithLogging(
                String.format("Поиск данных за период: с %s по %s", startDate, endDate),
                () -> {
                    validateTimeRange(startDate, endDate);
                    return sensorDataRepository.findByTimestampBetween(startDate, endDate);
                }
        );
    }

    /**
     * Получает данные сенсоров за последние N дней.
     *
     * @param days количество дней
     * @return список данных за последние N дней
     * @throws IllegalArgumentException если количество дней некорректно
     */
    public List<SensorData> getRecentData(int days) {
        return executeWithLogging(
                String.format("Получение данных за последние %d дней", days),
                () -> {
                    validatePositive(days, "Количество дней");
                    return sensorDataRepository.findRecentData(days);
                }
        );
    }

    /**
     * Получает данные сенсора за определенный период.
     *
     * @param sensor сенсор
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @return список данных сенсора за указанный период
     * @throws IllegalArgumentException если параметры некорректны
     */
    public List<SensorData> getBySensorAndTimestampBetween(Sensor sensor, LocalDateTime startDate, LocalDateTime endDate) {
        return executeWithLogging(
                String.format("Поиск данных сенсора ID=%d за период: с %s по %s", sensor.getId(), startDate, endDate),
                () -> {
                    validateNotNull(sensor, "Сенсор");
                    validateTimeRange(startDate, endDate);
                    return sensorDataRepository.findBySensorAndTimestampBetween(sensor, startDate, endDate);
                }
        );
    }

    /**
     * Получает данные по назначению с сортировкой по времени (по возрастанию).
     *
     * @param purpose назначение данных
     * @return отсортированный список данных
     */
    public List<SensorData> getByPurposeOrderByTimestampAsc(String purpose) {
        return executeWithLogging(
                String.format("Поиск данных по назначению '%s' с сортировкой по времени (возр.)", purpose),
                () -> {
                    validatePurpose(purpose);
                    return sensorDataRepository.findByPurposeOrderByTimestampAsc(purpose);
                }
        );
    }

    /**
     * Получает данные по назначению с сортировкой по времени (по убыванию).
     *
     * @param purpose назначение данных
     * @return отсортированный список данных
     */
    public List<SensorData> getByPurposeOrderByTimestampDesc(String purpose) {
        return executeWithLogging(
                String.format("Поиск данных по назначению '%s' с сортировкой по времени (убыв.)", purpose),
                () -> {
                    validatePurpose(purpose);
                    return sensorDataRepository.findByPurposeOrderByTimestampDesc(purpose);
                }
        );
    }

    /**
     * Получает данные по сенсору с сортировкой по времени.
     *
     * @param sensor сенсор
     * @param ascending true - по возрастанию, false - по убыванию
     * @return отсортированный список данных
     */
    public List<SensorData> getBySensorOrderByTimestamp(Sensor sensor, boolean ascending) {
        return executeWithLogging(
                String.format("Поиск данных сенсора ID=%d с сортировкой по времени (%s)",
                        sensor.getId(), ascending ? "возрастание" : "убывание"),
                () -> {
                    validateNotNull(sensor, "Сенсор");
                    return sensorDataRepository.findBySensorOrderByTimestamp(sensor, ascending);
                }
        );
    }

    /**
     * Получает данные, назначение которых начинается с указанного текста.
     *
     * @param prefix префикс назначения
     * @return список данных, чьи назначения начинаются с указанного префикса
     */
    public List<SensorData> getByPurposeStartingWith(String prefix) {
        return executeWithLogging(
                String.format("Поиск данных по префиксу назначения: '%s'", prefix),
                () -> {
                    if (prefix == null || prefix.trim().isEmpty()) {
                        return List.of();
                    }
                    return sensorDataRepository.findByPurposeStartingWith(prefix.trim());
                }
        );
    }

    /**
     * Получает данные, назначение которых заканчивается на указанный текст.
     *
     * @param suffix суффикс назначения
     * @return список данных, чьи назначения заканчиваются на указанный суффикс
     */
    public List<SensorData> getByPurposeEndingWith(String suffix) {
        return executeWithLogging(
                String.format("Поиск данных по суффиксу назначения: '%s'", suffix),
                () -> {
                    if (suffix == null || suffix.trim().isEmpty()) {
                        return List.of();
                    }
                    return sensorDataRepository.findByPurposeEndingWith(suffix.trim());
                }
        );
    }

    /**
     * Получает статистику по количеству данных для каждого сенсора.
     *
     * @return карта, где ключ - ID сенсора, значение - количество данных
     */
    public Map<Long, Long> getDataCountBySensor() {
        return executeWithLogging(
                "Получение статистики данных по сенсорам",
                sensorDataRepository::getDataCountBySensor
        );
    }

    /**
     * Получает статистику по количеству данных за последние дни.
     *
     * @param days количество дней
     * @return карта, где ключ - дата, значение - количество данных
     * @throws IllegalArgumentException если количество дней некорректно
     */
    public Map<String, Long> getDataCountByDay(int days) {
        return executeWithLogging(
                String.format("Получение статистики данных по дням за последние %d дней", days),
                () -> {
                    validatePositive(days, "Количество дней");
                    return sensorDataRepository.getDataCountByDay(days);
                }
        );
    }

    /**
     * Получает последние данные для каждого сенсора.
     *
     * @return список последних данных по каждому сенсору
     */
    public List<SensorData> getLatestDataForEachSensor() {
        return executeWithLogging(
                "Поиск последних данных для каждого сенсора",
                sensorDataRepository::findLatestDataForEachSensor
        );
    }

    /**
     * Получает самые старые данные сенсоров.
     *
     * @return список данных с минимальной временной меткой
     */
    public List<SensorData> getOldestData() {
        return executeWithLogging(
                "Поиск самых старых данных сенсоров",
                sensorDataRepository::findOldestData
        );
    }

    /**
     * Получает самые новые данные сенсоров.
     *
     * @return список данных с максимальной временной меткой
     */
    public List<SensorData> getNewestData() {
        return executeWithLogging(
                "Поиск самых новых данных сенсоров",
                sensorDataRepository::findNewestData
        );
    }

    /**
     * Проверяет существование данных для указанного сенсора.
     *
     * @param sensor сенсор для проверки
     * @return true если данные существуют, false в противном случае
     */
    public boolean existsBySensor(Sensor sensor) {
        return executeWithLogging(
                String.format("Проверка существования данных для сенсора ID: %d", sensor.getId()),
                () -> {
                    validateNotNull(sensor, "Сенсор");
                    return sensorDataRepository.existsBySensor(sensor);
                }
        );
    }

    /**
     * Удаляет все данные для указанного сенсора.
     *
     * @param sensor сенсор, данные которого нужно удалить
     * @return количество удаленных записей
     */
    public int deleteBySensor(Sensor sensor) {
        return executeWithLogging(
                String.format("Удаление всех данных для сенсора ID: %d", sensor.getId()),
                () -> {
                    validateNotNull(sensor, "Сенсор");
                    return sensorDataRepository.deleteBySensor(sensor);
                }
        );
    }

    /**
     * Удаляет старые данные (до указанной даты).
     *
     * @param cutoffDate дата, до которой удалять данные
     * @return количество удаленных записей
     * @throws IllegalArgumentException если дата некорректна
     */
    public int deleteOldData(LocalDateTime cutoffDate) {
        return executeWithLogging(
                String.format("Удаление старых данных до даты: %s", cutoffDate),
                () -> {
                    validateTimestamp(cutoffDate);
                    return sensorDataRepository.deleteOldData(cutoffDate);
                }
        );
    }

    /**
     * Получает данные по сенсору с пагинацией.
     *
     * @param sensor сенсор
     * @param page номер страницы (начинается с 0)
     * @param size размер страницы
     * @return список данных для указанной страницы
     * @throws IllegalArgumentException если параметры некорректны
     */
    public List<SensorData> getBySensorWithPagination(Sensor sensor, int page, int size) {
        return executeWithLogging(
                String.format("Поиск данных сенсора ID=%d с пагинацией: page=%d, size=%d", sensor.getId(), page, size),
                () -> {
                    validateNotNull(sensor, "Сенсор");
                    validatePagination(page, size);
                    return sensorDataRepository.findBySensorWithPagination(sensor, page, size);
                }
        );
    }

    /**
     * Получает количество данных для указанного сенсора.
     *
     * @param sensor сенсор для подсчета
     * @return количество данных для указанного сенсора
     */
    public Long countBySensor(Sensor sensor) {
        return executeWithLogging(
                String.format("Подсчет количества данных для сенсора ID: %d", sensor.getId()),
                () -> {
                    validateNotNull(sensor, "Сенсор");
                    Long count = sensorDataRepository.countBySensor(sensor);
                    return count != null ? count : 0L;
                }
        );
    }

    /**
     * Получает данные по нескольким сенсорам.
     *
     * @param sensors список сенсоров
     * @return список данных для указанных сенсоров
     */
    public List<SensorData> getBySensors(List<Sensor> sensors) {
        return executeWithLogging(
                String.format("Поиск данных по %d сенсорам", sensors.size()),
                () -> {
                    if (sensors.isEmpty()) {
                        return List.of();
                    }
                    return sensorDataRepository.findBySensors(sensors);
                }
        );
    }

    /**
     * Получает данные по типу назначения и временному диапазону.
     *
     * @param purposePattern паттерн назначения
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @return список данных, соответствующих критериям
     * @throws IllegalArgumentException если параметры некорректны
     */
    public List<SensorData> getByPurposePatternAndTimeRange(String purposePattern, LocalDateTime startDate, LocalDateTime endDate) {
        return executeWithLogging(
                String.format("Поиск данных по паттерну '%s' за период: с %s по %s", purposePattern, startDate, endDate),
                () -> {
                    if (purposePattern == null || purposePattern.trim().isEmpty()) {
                        return List.of();
                    }
                    validateTimeRange(startDate, endDate);
                    return sensorDataRepository.findByPurposePatternAndTimeRange(purposePattern.trim(), startDate, endDate);
                }
        );
    }

    /**
     * Получает распределение данных по часам суток.
     *
     * @return карта, где ключ - час (0-23), значение - количество данных
     */
    public Map<Integer, Long> getDataDistributionByHour() {
        return executeWithLogging(
                "Получение распределения данных по часам суток",
                sensorDataRepository::getDataDistributionByHour
        );
    }

    /**
     * Получает общее количество данных сенсоров в системе.
     *
     * @return общее количество данных сенсоров
     */
    public long getTotalSensorDataCount() {
        return executeWithLogging(
                "Получение общего количества данных сенсоров",
                sensorDataRepository::count
        );
    }

    /**
     * Проверяет, являются ли данные новыми (не сохраненными в БД).
     *
     * @param sensorData объект данных сенсора
     * @return true если данные новые, false если существуют в БД
     */
    public boolean isNewSensorData(SensorData sensorData) {
        return executeWithLogging(
                "Проверка, являются ли данные новыми",
                () -> sensorDataRepository.isNew(sensorData)
        );
    }

    /**
     * Валидирует объект данных сенсора.
     *
     * @param sensorData объект данных сенсора для валидации
     * @throws IllegalArgumentException если данные не соответствуют требованиям
     */
    private void validateSensorData(SensorData sensorData) {
        validateNotNull(sensorData, "Данные сенсора");
        validateNotNull(sensorData.getSensor(), "Сенсор");
        validatePurpose(sensorData.getPurpose());
        validateTimestamp(sensorData.getTimestamp());
    }

    /**
     * Валидирует назначение данных.
     *
     * @param purpose назначение для валидации
     * @throws IllegalArgumentException если назначение некорректно
     */
    private void validatePurpose(String purpose) {
        validateNotEmpty(purpose, "Назначение данных");
        validateStringLength(purpose, "Назначение данных", 3, 500);
    }

    /**
     * Валидирует временную метку.
     *
     * @param timestamp временная метка для валидации
     * @throws IllegalArgumentException если временная метка некорректна
     */
    private void validateTimestamp(LocalDateTime timestamp) {
        validateNotNull(timestamp, "Временная метка");

        if (timestamp.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Временная метка не может быть в будущем");
        }
    }

    /**
     * Валидирует временной диапазон.
     *
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @throws IllegalArgumentException если временной диапазон некорректен
     */
    private void validateTimeRange(LocalDateTime startDate, LocalDateTime endDate) {
        validateTimestamp(startDate);
        validateTimestamp(endDate);

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Начальная дата не может быть позже конечной");
        }
    }
}