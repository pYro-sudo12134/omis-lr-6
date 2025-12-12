package by.losik.lab6omis.service.general.types;

import by.losik.lab6omis.entities.general.enums.Language;
import by.losik.lab6omis.entities.general.types.Request;
import by.losik.lab6omis.repository.general.types.RequestRepository;
import by.losik.lab6omis.service.base.BaseService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Map;

/**
 * Сервис для управления запросами (Request).
 * Предоставляет полный набор операций CRUD, поиска, фильтрации и статистики.
 *
 * @see Request
 * @see RequestRepository
 * @author Losik Yaroslav
 * @version 1.0
 */
@ApplicationScoped
@Transactional
public class RequestService extends BaseService<Request, Long> {

    @Inject
    RequestRepository requestRepository;

    /**
     * Создает новый запрос в системе.
     *
     * @param request объект запроса для создания (должен быть валидным)
     * @return сохраненный объект запроса
     * @throws IllegalArgumentException если запрос с такой целью уже существует
     */
    public Request createRequest(@Valid Request request) {
        return executeWithLogging(
                String.format("Создание нового запроса: цель='%s', язык=%s, точность=%.2f",
                        request.getGoal(), request.getLanguage(), request.getRecognitionAccuracy()),
                () -> {
                    validateRequest(request);
                    validateUnique(request.getGoal(),
                            () -> requestRepository.existsByGoal(request.getGoal()),
                            "целью", "Запрос");

                    return requestRepository.create(request);
                }
        );
    }

    /**
     * Получает запрос по его идентификатору.
     *
     * @param id идентификатор запроса
     * @return найденный объект запроса
     * @throws NotFoundException если запрос с указанным ID не найден
     */
    public Request getById(Long id) {
        return getEntityById(
                id,
                () -> requestRepository.findById(id),
                "Запрос"
        );
    }

    /**
     * Получает все запросы из системы.
     *
     * @return список всех запросов
     */
    public List<Request> getAllRequests() {
        return executeWithLogging(
                "Получение всех запросов",
                requestRepository::findAll
        );
    }

    /**
     * Получает запросы с пагинацией.
     *
     * @param page номер страницы (начинается с 0)
     * @param size количество элементов на странице
     * @return список запросов на указанной странице
     * @throws IllegalArgumentException если параметры пагинации некорректны
     */
    public List<Request> getAllRequests(int page, int size) {
        return executeWithLogging(
                String.format("Получение запросов с пагинацией: page=%d, size=%d", page, size),
                () -> {
                    validatePagination(page, size);
                    return requestRepository.findAll(page, size);
                }
        );
    }

    /**
     * Обновляет существующий запрос.
     *
     * @param id идентификатор обновляемого запроса
     * @param updatedRequest обновленные данные запроса
     * @return обновленный объект запроса
     * @throws NotFoundException если запрос с указанным ID не найден
     * @throws IllegalArgumentException если новая цель уже существует у другого запроса
     */
    public Request updateRequest(Long id, @Valid Request updatedRequest) {
        return executeWithLogging(
                String.format("Обновление запроса с ID %d", id),
                () -> {
                    Request existingRequest = getById(id);
                    validateRequest(updatedRequest);

                    if (!existingRequest.getGoal().equals(updatedRequest.getGoal())) {
                        validateUnique(updatedRequest.getGoal(),
                                () -> requestRepository.existsByGoal(updatedRequest.getGoal()),
                                "целью", "Запрос");
                    }

                    existingRequest.setLanguage(updatedRequest.getLanguage());
                    existingRequest.setGoal(updatedRequest.getGoal());
                    existingRequest.setRecognitionAccuracy(updatedRequest.getRecognitionAccuracy());

                    return requestRepository.save(existingRequest);
                }
        );
    }

    /**
     * Удаляет запрос по его идентификатору.
     *
     * @param id идентификатор удаляемого запроса
     * @throws NotFoundException если запрос с указанным ID не найден
     */
    public void deleteRequest(Long id) {
        executeVoidWithLogging(
                String.format("Удаление запроса ID %d", id),
                () -> {
                    ensureEntityExists(id,
                            () -> requestRepository.existsById(id),
                            "Запрос");
                    requestRepository.deleteById(id);
                }
        );
    }

    /**
     * Получает все запросы на указанном языке.
     *
     * @param language язык для фильтрации запросов
     * @return список запросов на указанном языке
     */
    public List<Request> getByLanguage(Language language) {
        return executeWithLogging(
                String.format("Поиск запросов по языку: %s", language),
                () -> {
                    validateNotNull(language, "Язык");
                    return requestRepository.findByLanguage(language);
                }
        );
    }

    /**
     * Получает запросы с точностью распознавания выше указанного значения.
     *
     * @param minAccuracy минимальное значение точности распознавания
     * @return список запросов с точностью выше указанного значения
     * @throws IllegalArgumentException если значение точности некорректно
     */
    public List<Request> getByRecognitionAccuracyGreaterThan(Double minAccuracy) {
        return executeWithLogging(
                String.format("Поиск запросов с точностью выше %.2f", minAccuracy),
                () -> {
                    validateRecognitionAccuracy(minAccuracy);
                    return requestRepository.findByRecognitionAccuracyGreaterThan(minAccuracy);
                }
        );
    }

    /**
     * Получает запросы с точностью распознавания в диапазоне.
     *
     * @param minAccuracy минимальная точность
     * @param maxAccuracy максимальная точность
     * @return список запросов в указанном диапазоне точности
     * @throws IllegalArgumentException если параметры диапазона некорректны
     */
    public List<Request> getByRecognitionAccuracyBetween(Double minAccuracy, Double maxAccuracy) {
        return executeWithLogging(
                String.format("Поиск запросов с точностью от %.2f до %.2f", minAccuracy, maxAccuracy),
                () -> {
                    validateRecognitionAccuracy(minAccuracy);
                    validateRecognitionAccuracy(maxAccuracy);

                    if (minAccuracy > maxAccuracy) {
                        throw new IllegalArgumentException(
                                String.format("Минимальная точность (%.2f) не может быть больше максимальной (%.2f)",
                                        minAccuracy, maxAccuracy)
                        );
                    }

                    return requestRepository.findByRecognitionAccuracyBetween(minAccuracy, maxAccuracy);
                }
        );
    }

    /**
     * Получает запросы по паттерну цели.
     *
     * @param goalPattern паттерн поиска цели (можно использовать % и _)
     * @return список запросов, чья цель соответствует паттерну
     */
    public List<Request> getByGoalPattern(String goalPattern) {
        return executeWithLogging(
                String.format("Поиск запросов по паттерну цели: '%s'", goalPattern),
                () -> {
                    if (goalPattern == null || goalPattern.trim().isEmpty()) {
                        return List.of();
                    }
                    return requestRepository.findByGoalLike(goalPattern.trim());
                }
        );
    }

    /**
     * Получает запросы по языку с сортировкой по точности распознавания (по убыванию).
     *
     * @param language язык запроса
     * @return отсортированный список запросов
     */
    public List<Request> getByLanguageOrderByAccuracyDesc(Language language) {
        return executeWithLogging(
                String.format("Поиск запросов на языке %s с сортировкой по точности (убыв.)", language),
                () -> {
                    validateNotNull(language, "Язык");
                    return requestRepository.findByLanguageOrderByAccuracyDesc(language);
                }
        );
    }

    /**
     * Рассчитывает среднюю точность распознавания всех запросов.
     *
     * @return средняя точность распознавания
     */
    public Double getAverageRecognitionAccuracy() {
        return executeWithLogging(
                "Расчет средней точности распознавания",
                () -> {
                    Double avgAccuracy = requestRepository.getAverageRecognitionAccuracy();
                    return avgAccuracy != null ? avgAccuracy : 0.0;
                }
        );
    }

    /**
     * Рассчитывает среднюю точность распознавания по указанному языку.
     *
     * @param language язык для расчета статистики
     * @return средняя точность распознавания для указанного языка
     */
    public Double getAverageRecognitionAccuracyByLanguage(Language language) {
        return executeWithLogging(
                String.format("Расчет средней точности распознавания для языка: %s", language),
                () -> {
                    validateNotNull(language, "Язык");
                    Double avgAccuracy = requestRepository.getAverageRecognitionAccuracyByLanguage(language);
                    return avgAccuracy != null ? avgAccuracy : 0.0;
                }
        );
    }

    /**
     * Получает статистику количества запросов по языкам.
     *
     * @return карта, где ключ - язык, значение - количество запросов
     */
    public Map<Language, Long> getRequestsCountByLanguage() {
        return executeWithLogging(
                "Получение статистики запросов по языкам",
                requestRepository::getRequestsCountByLanguage
        );
    }

    /**
     * Получает запросы с максимальной точностью распознавания.
     *
     * @return список запросов с максимальной точностью
     */
    public List<Request> getTopAccuracyRequests() {
        return executeWithLogging(
                "Поиск запросов с максимальной точностью",
                requestRepository::findTopByRecognitionAccuracy
        );
    }

    /**
     * Получает запросы с минимальной точностью распознавания.
     *
     * @return список запросов с минимальной точностью
     */
    public List<Request> getBottomAccuracyRequests() {
        return executeWithLogging(
                "Поиск запросов с минимальной точностью",
                requestRepository::findBottomByRecognitionAccuracy
        );
    }

    /**
     * Проверяет существование запроса с указанной целью.
     *
     * @param goal цель запроса для проверки
     * @return true если запрос существует, false в противном случае
     */
    public boolean existsByGoal(String goal) {
        return executeWithLogging(
                String.format("Проверка существования запроса с целью: '%s'", goal),
                () -> {
                    if (goal == null || goal.trim().isEmpty()) {
                        return false;
                    }
                    return requestRepository.existsByGoal(goal.trim());
                }
        );
    }

    /**
     * Получает запрос по точному совпадению цели.
     *
     * @param goal точная цель запроса
     * @return найденный запрос
     * @throws IllegalArgumentException если цель некорректна
     * @throws NotFoundException если запрос не найден
     */
    public Request getByGoal(String goal) {
        return executeWithLogging(
                String.format("Поиск запроса по точной цели: '%s'", goal),
                () -> {
                    validateGoal(goal);
                    return requestRepository.findByGoal(goal.trim())
                            .orElseThrow(() -> new NotFoundException(
                                    String.format("Запрос с целью '%s' не найден", goal)
                            ));
                }
        );
    }

    /**
     * Ищет запросы по частичному совпадению цели.
     *
     * @param searchText текст для поиска в цели запроса
     * @return список найденных запросов
     */
    public List<Request> searchByGoal(String searchText) {
        return executeWithLogging(
                String.format("Поиск запросов по цели: '%s'", searchText),
                () -> {
                    if (searchText == null || searchText.trim().isEmpty()) {
                        return List.of();
                    }
                    return requestRepository.findByGoalContaining(searchText.trim());
                }
        );
    }

    /**
     * Удаляет все запросы на указанном языке.
     *
     * @param language язык запросов для удаления
     * @return количество удаленных запросов
     */
    public int deleteByLanguage(Language language) {
        return executeWithLogging(
                String.format("Удаление всех запросов на языке: %s", language),
                () -> {
                    validateNotNull(language, "Язык");
                    return requestRepository.deleteByLanguage(language);
                }
        );
    }

    /**
     * Получает общее количество запросов в системе.
     *
     * @return общее количество запросов
     */
    public long getTotalRequestsCount() {
        return executeWithLogging(
                "Получение общего количества запросов",
                requestRepository::count
        );
    }

    /**
     * Проверяет, является ли запрос новым (не сохраненным в БД).
     *
     * @param request объект запроса
     * @return true если запрос новый, false если существует в БД
     */
    public boolean isNewRequest(Request request) {
        return executeWithLogging(
                "Проверка, является ли запрос новым",
                () -> requestRepository.isNew(request)
        );
    }

    /**
     * Валидирует объект запроса.
     *
     * @param request объект запроса для валидации
     * @throws IllegalArgumentException если запрос не соответствует требованиям
     */
    private void validateRequest(Request request) {
        validateNotNull(request, "Запрос");
        validateGoal(request.getGoal());
        validateNotNull(request.getLanguage(), "Язык");
        validateRecognitionAccuracy(request.getRecognitionAccuracy());
    }

    /**
     * Валидирует цель запроса.
     *
     * @param goal цель для валидации
     * @throws IllegalArgumentException если цель некорректна
     */
    private void validateGoal(String goal) {
        validateNotEmpty(goal, "Цель запроса");
        validateStringLength(goal, "Цель запроса", 3, 200);
    }

    /**
     * Валидирует точность распознавания.
     *
     * @param accuracy значение точности для валидации
     * @throws IllegalArgumentException если значение некорректно
     */
    private void validateRecognitionAccuracy(Double accuracy) {
        validateNotNull(accuracy, "Точность распознавания");
        validateNumberRange(accuracy, "Точность распознавания", 0.0, 100.0);
    }
}