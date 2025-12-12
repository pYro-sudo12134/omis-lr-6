package by.losik.lab6omis.service.general.types;

import by.losik.lab6omis.entities.general.enums.Language;
import by.losik.lab6omis.entities.general.types.ResponseEntity;
import by.losik.lab6omis.repository.general.types.ResponseRepository;
import by.losik.lab6omis.service.base.BaseService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Map;

/**
 * Сервис для управления ответами (ResponseEntity).
 * Предоставляет полный набор операций CRUD, поиска, фильтрации и статистики.
 *
 * @see ResponseEntity
 * @see ResponseRepository
 * @author Losik Yaroslav
 * @version 1.0
 */
@ApplicationScoped
@Transactional
public class ResponseService extends BaseService<ResponseEntity, Long> {

    @Inject
    ResponseRepository responseRepository;

    /**
     * Создает новый ответ в системе.
     *
     * @param responseEntity объект ответа для создания (должен быть валидным)
     * @return сохраненный объект ответа
     * @throws IllegalArgumentException если ответ с таким сообщением уже существует
     */
    public ResponseEntity createResponse(@Valid ResponseEntity responseEntity) {
        return executeWithLogging(
                String.format("Создание нового ответа: язык=%s, длина сообщения=%d",
                        responseEntity.getLanguage(), responseEntity.getMessage().length()),
                () -> {
                    validateResponse(responseEntity);
                    validateUnique(responseEntity.getMessage(),
                            () -> responseRepository.existsByMessage(responseEntity.getMessage()),
                            "сообщением", "Ответ");

                    return responseRepository.create(responseEntity);
                }
        );
    }

    /**
     * Получает ответ по его идентификатору.
     *
     * @param id идентификатор ответа
     * @return найденный объект ответа
     * @throws NotFoundException если ответ с указанным ID не найден
     */
    public ResponseEntity getById(Long id) {
        return getEntityById(
                id,
                () -> responseRepository.findById(id),
                "Ответ"
        );
    }

    /**
     * Получает все ответы из системы.
     *
     * @return список всех ответов
     */
    public List<ResponseEntity> getAllResponses() {
        return executeWithLogging(
                "Получение всех ответов",
                responseRepository::findAll
        );
    }

    /**
     * Получает ответы с пагинацией.
     *
     * @param page номер страницы (начинается с 0)
     * @param size количество элементов на странице
     * @return список ответов на указанной странице
     * @throws IllegalArgumentException если параметры пагинации некорректны
     */
    public List<ResponseEntity> getAllResponses(int page, int size) {
        return executeWithLogging(
                String.format("Получение ответов с пагинацией: page=%d, size=%d", page, size),
                () -> {
                    validatePagination(page, size);
                    return responseRepository.findAll(page, size);
                }
        );
    }

    /**
     * Обновляет существующий ответ.
     *
     * @param id идентификатор обновляемого ответа
     * @param updatedResponseEntity обновленные данные ответа
     * @return обновленный объект ответа
     * @throws NotFoundException если ответ с указанным ID не найден
     * @throws IllegalArgumentException если новое сообщение уже существует у другого ответа
     */
    public ResponseEntity updateResponse(Long id, @Valid ResponseEntity updatedResponseEntity) {
        return executeWithLogging(
                String.format("Обновление ответа с ID %d", id),
                () -> {
                    ResponseEntity existingResponseEntity = getById(id);
                    validateResponse(updatedResponseEntity);

                    if (!existingResponseEntity.getMessage().equals(updatedResponseEntity.getMessage())) {
                        validateUnique(updatedResponseEntity.getMessage(),
                                () -> responseRepository.existsByMessage(updatedResponseEntity.getMessage()),
                                "сообщением", "Ответ");
                    }

                    existingResponseEntity.setLanguage(updatedResponseEntity.getLanguage());
                    existingResponseEntity.setMessage(updatedResponseEntity.getMessage());

                    return responseRepository.save(existingResponseEntity);
                }
        );
    }

    /**
     * Удаляет ответ по его идентификатору.
     *
     * @param id идентификатор удаляемого ответа
     * @throws NotFoundException если ответ с указанным ID не найден
     */
    public void deleteResponse(Long id) {
        executeVoidWithLogging(
                String.format("Удаление ответа ID %d", id),
                () -> {
                    ensureEntityExists(id,
                            () -> responseRepository.existsById(id),
                            "Ответ");
                    responseRepository.deleteById(id);
                }
        );
    }

    /**
     * Получает все ответы на указанном языке.
     *
     * @param language язык для фильтрации ответов
     * @return список ответов на указанном языке
     */
    public List<ResponseEntity> getByLanguage(Language language) {
        return executeWithLogging(
                String.format("Поиск ответов по языку: %s", language),
                () -> {
                    validateNotNull(language, "Язык");
                    return responseRepository.findByLanguage(language);
                }
        );
    }

    /**
     * Получает ответ по точному совпадению сообщения.
     *
     * @param message сообщение ответа
     * @return найденный ответ
     * @throws IllegalArgumentException если сообщение некорректно
     * @throws NotFoundException если ответ не найден
     */
    public ResponseEntity getByMessage(String message) {
        return executeWithLogging(
                String.format("Поиск ответа по точному сообщению: '%s'", message),
                () -> {
                    validateMessage(message);
                    return responseRepository.findByMessage(message.trim())
                            .orElseThrow(() -> new NotFoundException(
                                    String.format("Ответ с сообщением '%s' не найден", message)
                            ));
                }
        );
    }

    /**
     * Ищет ответы по частичному совпадению сообщения.
     *
     * @param searchText текст для поиска в сообщении ответа
     * @return список найденных ответов
     */
    public List<ResponseEntity> searchByMessage(String searchText) {
        return executeWithLogging(
                String.format("Поиск ответов по сообщению: '%s'", searchText),
                () -> {
                    if (searchText == null || searchText.trim().isEmpty()) {
                        return List.of();
                    }
                    return responseRepository.findByMessageContaining(searchText.trim());
                }
        );
    }

    /**
     * Получает ответы с сообщениями определенной длины.
     *
     * @param minLength минимальная длина сообщения
     * @param maxLength максимальная длина сообщения
     * @return список ответов с сообщениями указанной длины
     * @throws IllegalArgumentException если параметры длины некорректны
     */
    public List<ResponseEntity> getByMessageLengthBetween(int minLength, int maxLength) {
        return executeWithLogging(
                String.format("Поиск ответов с длиной сообщения от %d до %d символов", minLength, maxLength),
                () -> {
                    if (minLength < 0 || maxLength < 0 || minLength > maxLength) {
                        throw new IllegalArgumentException(
                                String.format("Некорректные параметры длины: min=%d, max=%d", minLength, maxLength)
                        );
                    }
                    return responseRepository.findByMessageLengthBetween(minLength, maxLength);
                }
        );
    }

    /**
     * Получает ответы с короткими сообщениями.
     *
     * @param maxLength максимальная длина сообщения
     * @return список ответов с короткими сообщениями
     * @throws IllegalArgumentException если параметр длины некорректен
     */
    public List<ResponseEntity> getShortMessages(int maxLength) {
        return executeWithLogging(
                String.format("Поиск ответов с сообщениями короче %d символов", maxLength),
                () -> {
                    validatePositive(maxLength, "Максимальная длина сообщения");
                    return responseRepository.findByShortMessage(maxLength);
                }
        );
    }

    /**
     * Получает ответы с длинными сообщениями.
     *
     * @param minLength минимальная длина сообщения
     * @return список ответов с длинными сообщениями
     * @throws IllegalArgumentException если параметр длины некорректен
     */
    public List<ResponseEntity> getLongMessages(int minLength) {
        return executeWithLogging(
                String.format("Поиск ответов с сообщениями длиннее %d символов", minLength),
                () -> {
                    if (minLength < 0) {
                        throw new IllegalArgumentException("Минимальная длина не может быть отрицательной");
                    }
                    return responseRepository.findByLongMessage(minLength);
                }
        );
    }

    /**
     * Получает ответы по языку с сортировкой по длине сообщения (по возрастанию).
     *
     * @param language язык ответа
     * @return отсортированный список ответов
     */
    public List<ResponseEntity> getByLanguageOrderByMessageLengthAsc(Language language) {
        return executeWithLogging(
                String.format("Поиск ответов на языке %s с сортировкой по длине (возр.)", language),
                () -> {
                    validateNotNull(language, "Язык");
                    return responseRepository.findByLanguageOrderByMessageLengthAsc(language);
                }
        );
    }

    /**
     * Получает ответы по языку с сортировкой по длине сообщения (по убыванию).
     *
     * @param language язык ответа
     * @return отсортированный список ответов
     */
    public List<ResponseEntity> getByLanguageOrderByMessageLengthDesc(Language language) {
        return executeWithLogging(
                String.format("Поиск ответов на языке %s с сортировкой по длине (убыв.)", language),
                () -> {
                    validateNotNull(language, "Язык");
                    return responseRepository.findByLanguageOrderByMessageLengthDesc(language);
                }
        );
    }

    /**
     * Получает ответы, сообщения которых начинаются с указанного текста.
     *
     * @param prefix префикс сообщения
     * @return список ответов, чьи сообщения начинаются с указанного префикса
     */
    public List<ResponseEntity> getByMessageStartingWith(String prefix) {
        return executeWithLogging(
                String.format("Поиск ответов по префиксу сообщения: '%s'", prefix),
                () -> {
                    if (prefix == null || prefix.trim().isEmpty()) {
                        return List.of();
                    }
                    return responseRepository.findByMessageStartingWith(prefix.trim());
                }
        );
    }

    /**
     * Получает ответы, сообщения которых заканчиваются на указанный текст.
     *
     * @param suffix суффикс сообщения
     * @return список ответов, чьи сообщения заканчиваются на указанный суффикс
     */
    public List<ResponseEntity> getByMessageEndingWith(String suffix) {
        return executeWithLogging(
                String.format("Поиск ответов по суффиксу сообщения: '%s'", suffix),
                () -> {
                    if (suffix == null || suffix.trim().isEmpty()) {
                        return List.of();
                    }
                    return responseRepository.findByMessageEndingWith(suffix.trim());
                }
        );
    }

    /**
     * Получает статистику количества ответов по языкам.
     *
     * @return карта, где ключ - язык, значение - количество ответов
     */
    public Map<Language, Long> getResponsesCountByLanguage() {
        return executeWithLogging(
                "Получение статистики ответов по языкам",
                responseRepository::getResponsesCountByLanguage
        );
    }

    /**
     * Рассчитывает среднюю длину сообщений всех ответов.
     *
     * @return средняя длина сообщений
     */
    public Double getAverageMessageLength() {
        return executeWithLogging(
                "Расчет средней длины сообщений",
                () -> {
                    Double avgLength = responseRepository.getAverageMessageLength();
                    return avgLength != null ? avgLength : 0.0;
                }
        );
    }

    /**
     * Рассчитывает среднюю длину сообщений по указанному языку.
     *
     * @param language язык для расчета статистики
     * @return средняя длина сообщений для указанного языка
     */
    public Double getAverageMessageLengthByLanguage(Language language) {
        return executeWithLogging(
                String.format("Расчет средней длины сообщений для языка: %s", language),
                () -> {
                    validateNotNull(language, "Язык");
                    Double avgLength = responseRepository.getAverageMessageLengthByLanguage(language);
                    return avgLength != null ? avgLength : 0.0;
                }
        );
    }

    /**
     * Получает самые короткие ответы в системе.
     *
     * @return список ответов с минимальной длиной сообщения
     */
    public List<ResponseEntity> getShortestResponses() {
        return executeWithLogging(
                "Поиск самых коротких ответов",
                responseRepository::findShortestResponses
        );
    }

    /**
     * Получает самые длинные ответы в системе.
     *
     * @return список ответов с максимальной длиной сообщения
     */
    public List<ResponseEntity> getLongestResponses() {
        return executeWithLogging(
                "Поиск самых длинных ответов",
                responseRepository::findLongestResponses
        );
    }

    /**
     * Проверяет существование ответа с указанным сообщением.
     *
     * @param message сообщение ответа для проверки
     * @return true если ответ существует, false в противном случае
     */
    public boolean existsByMessage(String message) {
        return executeWithLogging(
                String.format("Проверка существования ответа с сообщением: '%s'", message),
                () -> {
                    if (message == null || message.trim().isEmpty()) {
                        return false;
                    }
                    return responseRepository.existsByMessage(message.trim());
                }
        );
    }

    /**
     * Удаляет все ответы на указанном языке.
     *
     * @param language язык ответов для удаления
     * @return количество удаленных ответов
     */
    public int deleteByLanguage(Language language) {
        return executeWithLogging(
                String.format("Удаление всех ответов на языке: %s", language),
                () -> {
                    validateNotNull(language, "Язык");
                    return responseRepository.deleteByLanguage(language);
                }
        );
    }

    /**
     * Получает ответы по языку с пагинацией.
     *
     * @param language язык ответа
     * @param page номер страницы (начинается с 0)
     * @param size размер страницы
     * @return список ответов для указанной страницы
     * @throws IllegalArgumentException если параметры некорректны
     */
    public List<ResponseEntity> getByLanguageWithPagination(Language language, int page, int size) {
        return executeWithLogging(
                String.format("Поиск ответов на языке %s с пагинацией: page=%d, size=%d", language, page, size),
                () -> {
                    validateNotNull(language, "Язык");
                    validatePagination(page, size);
                    return responseRepository.findByLanguageWithPagination(language, page, size);
                }
        );
    }

    /**
     * Получает количество ответов на указанном языке.
     *
     * @param language язык для подсчета
     * @return количество ответов на указанном языке
     */
    public Long countByLanguage(Language language) {
        return executeWithLogging(
                String.format("Подсчет количества ответов на языке: %s", language),
                () -> {
                    validateNotNull(language, "Язык");
                    Long count = responseRepository.countByLanguage(language);
                    return count != null ? count : 0L;
                }
        );
    }

    /**
     * Получает ответы по нескольким языкам.
     *
     * @param languages список языков
     * @return список ответов на указанных языках
     */
    public List<ResponseEntity> getByLanguages(List<Language> languages) {
        return executeWithLogging(
                String.format("Поиск ответов по %d языкам", languages != null ? languages.size() : 0),
                () -> {
                    if (languages == null || languages.isEmpty()) {
                        return List.of();
                    }
                    return responseRepository.findByLanguages(languages);
                }
        );
    }

    /**
     * Получает ответы с сообщениями определенной точной длины.
     *
     * @param exactLength точная длина сообщения
     * @return список ответов с сообщениями указанной длины
     * @throws IllegalArgumentException если длина некорректна
     */
    public List<ResponseEntity> getByMessageLength(int exactLength) {
        return executeWithLogging(
                String.format("Поиск ответов с точной длиной сообщения: %d символов", exactLength),
                () -> {
                    if (exactLength < 0) {
                        throw new IllegalArgumentException("Длина сообщения не может быть отрицательной");
                    }
                    return responseRepository.findByMessageLength(exactLength);
                }
        );
    }

    /**
     * Получает общее количество ответов в системе.
     *
     * @return общее количество ответов
     */
    public long getTotalResponsesCount() {
        return executeWithLogging(
                "Получение общего количества ответов",
                responseRepository::count
        );
    }

    /**
     * Проверяет, является ли ответ новым (не сохраненным в БД).
     *
     * @param responseEntity объект ответа
     * @return true если ответ новый, false если существует в БД
     */
    public boolean isNewResponse(ResponseEntity responseEntity) {
        return executeWithLogging(
                "Проверка, является ли ответ новым",
                () -> responseRepository.isNew(responseEntity)
        );
    }

    /**
     * Валидирует объект ответа.
     *
     * @param responseEntity объект ответа для валидации
     * @throws IllegalArgumentException если ответ не соответствует требованиям
     */
    private void validateResponse(ResponseEntity responseEntity) {
        validateNotNull(responseEntity, "Ответ");
        validateNotNull(responseEntity.getLanguage(), "Язык ответа");
        validateMessage(responseEntity.getMessage());
    }

    /**
     * Валидирует сообщение ответа.
     *
     * @param message сообщение для валидации
     * @throws IllegalArgumentException если сообщение некорректно
     */
    private void validateMessage(String message) {
        validateNotEmpty(message, "Сообщение ответа");
        validateStringLength(message, "Сообщение ответа", 1, 1000);
    }
}