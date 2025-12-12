package by.losik.lab6omis.service.general.types;

import by.losik.lab6omis.entities.general.enums.Language;
import by.losik.lab6omis.entities.general.types.Solution;
import by.losik.lab6omis.repository.general.types.SolutionRepository;
import by.losik.lab6omis.service.base.BaseService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.NotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Сервис для управления решениями (Solution).
 * Предоставляет полный набор операций CRUD, поиска, фильтрации и статистики.
 * Использует {@link BaseService} для общей логики работы с сущностями.
 *
 * @see Solution
 * @see SolutionRepository
 * @author Losik Yaroslav
 * @version 1.0
 */
@ApplicationScoped
@Transactional
public class SolutionService extends BaseService<Solution, Long> {

    @Inject
    SolutionRepository solutionRepository;

    /**
     * Создает новое решение в системе.
     *
     * @param solution объект решения для создания (должен быть валидным)
     * @return сохраненный объект решения с присвоенным ID
     * @throws IllegalArgumentException если решение с таким сообщением уже существует
     *                                  или параметры не соответствуют требованиям
     */
    public Solution createSolution(@Valid Solution solution) {
        return executeWithLogging(
                String.format("Создание решения: язык='%s', длина сообщения=%d",
                        solution.getLanguage(), solution.getMessage().length()),
                () -> {
                    validateSolution(solution);
                    validateUnique(solution.getMessage(),
                            () -> solutionRepository.existsByMessage(solution.getMessage()),
                            "сообщением", "Решение");

                    return solutionRepository.create(solution);
                }
        );
    }

    /**
     * Получает решение по его идентификатору.
     *
     * @param id идентификатор решения
     * @return найденный объект решения
     * @throws NotFoundException если решение с указанным ID не найдено
     */
    public Solution getById(Long id) {
        return getEntityById(
                id,
                () -> solutionRepository.findById(id),
                "Решение"
        );
    }

    /**
     * Получает все решения из системы.
     *
     * @return список всех решений
     */
    public List<Solution> getAllSolutions() {
        return executeWithLogging(
                "Получение всех решений",
                solutionRepository::findAll
        );
    }

    /**
     * Получает решения с пагинацией.
     *
     * @param page номер страницы (начинается с 0)
     * @param size количество элементов на странице
     * @return список решений на указанной странице
     * @throws IllegalArgumentException если параметры пагинации некорректны
     */
    public List<Solution> getAllSolutions(int page, int size) {
        return executeWithLogging(
                String.format("Получение решений с пагинацией: page=%d, size=%d", page, size),
                () -> {
                    validatePagination(page, size);
                    return solutionRepository.findAll(page, size);
                }
        );
    }

    /**
     * Обновляет существующее решение.
     *
     * @param id идентификатор обновляемого решения
     * @param updatedSolution обновленные данные решения (должны быть валидными)
     * @return обновленный объект решения
     * @throws NotFoundException если решение с указанным ID не найдено
     * @throws IllegalArgumentException если новое сообщение уже существует у другого решения
     */
    public Solution updateSolution(Long id, @Valid Solution updatedSolution) {
        return executeWithLogging(
                String.format("Обновление решения ID %d", id),
                () -> {
                    Solution existingSolution = getById(id);
                    validateSolution(updatedSolution);

                    if (!existingSolution.getMessage().equals(updatedSolution.getMessage())) {
                        validateUnique(updatedSolution.getMessage(),
                                () -> solutionRepository.existsByMessage(updatedSolution.getMessage()),
                                "сообщением", "Решение");
                    }

                    existingSolution.setLanguage(updatedSolution.getLanguage());
                    existingSolution.setMessage(updatedSolution.getMessage());

                    return solutionRepository.save(existingSolution);
                }
        );
    }

    /**
     * Удаляет решение по его идентификатору.
     *
     * @param id идентификатор удаляемого решения
     * @throws NotFoundException если решение с указанным ID не найдено
     */
    public void deleteSolution(Long id) {
        executeVoidWithLogging(
                String.format("Удаление решения ID %d", id),
                () -> {
                    ensureEntityExists(id,
                            () -> solutionRepository.existsById(id),
                            "Решение");
                    solutionRepository.deleteById(id);
                }
        );
    }

    /**
     * Получает все решения на указанном языке.
     *
     * @param language язык для фильтрации решений
     * @return список решений на указанном языке
     */
    public List<Solution> getByLanguage(Language language) {
        return executeWithLogging(
                String.format("Поиск решений по языку: %s", language),
                () -> {
                    validateNotNull(language, "Язык решения");
                    return solutionRepository.findByLanguage(language);
                }
        );
    }

    /**
     * Получает решения на указанном языке с пагинацией.
     *
     * @param language язык для фильтрации
     * @param page номер страницы
     * @param size размер страницы
     * @return список решений на указанной странице
     * @throws IllegalArgumentException если параметры пагинации некорректны
     */
    public List<Solution> getByLanguageWithPagination(Language language, int page, int size) {
        return executeWithLogging(
                String.format("Поиск решений на языке %s с пагинацией: page=%d, size=%d", language, page, size),
                () -> {
                    validateNotNull(language, "Язык решения");
                    validatePagination(page, size);
                    return solutionRepository.findByLanguageWithPagination(language, page, size);
                }
        );
    }

    /**
     * Ищет решения по частичному совпадению сообщения.
     *
     * @param searchText текст для поиска в сообщении решения
     * @return список найденных решений
     */
    public List<Solution> searchByMessage(String searchText) {
        return executeWithLogging(
                String.format("Поиск решений по сообщению: '%s'", searchText),
                () -> {
                    if (searchText == null || searchText.trim().isEmpty()) {
                        return List.of();
                    }
                    return solutionRepository.findByMessageContaining(searchText.trim());
                }
        );
    }

    /**
     * Ищет решения по ключевым словам.
     *
     * @param keywords список ключевых слов для поиска
     * @return список найденных решений
     * @throws IllegalArgumentException если список ключевых слов пуст
     */
    public List<Solution> searchByKeywords(List<String> keywords) {
        return executeWithLogging(
                String.format("Поиск решений по ключевым словам: %s", keywords),
                () -> {
                    List<String> cleanedKeywords = cleanStringList(keywords);
                    if (cleanedKeywords.isEmpty()) {
                        throw new IllegalArgumentException("Список ключевых слов не может быть пустым");
                    }
                    return solutionRepository.searchByKeywords(cleanedKeywords);
                }
        );
    }

    /**
     * Получает решения с сообщениями в заданном диапазоне длины.
     *
     * @param minLength минимальная длина сообщения
     * @param maxLength максимальная длина сообщения
     * @return список решений с сообщениями указанной длины
     * @throws IllegalArgumentException если параметры длины некорректны
     */
    public List<Solution> getByMessageLengthBetween(int minLength, int maxLength) {
        return executeWithLogging(
                String.format("Поиск решений с длиной сообщения от %d до %d символов", minLength, maxLength),
                () -> {
                    if (minLength < 0 || maxLength < 0 || minLength > maxLength) {
                        throw new IllegalArgumentException("Некорректные параметры длины сообщения");
                    }
                    return solutionRepository.findByMessageLengthBetween(minLength, maxLength);
                }
        );
    }

    /**
     * Получает решения с короткими сообщениями.
     *
     * @param maxLength максимальная длина сообщения
     * @return список решений с короткими сообщениями
     * @throws IllegalArgumentException если параметр длины некорректен
     */
    public List<Solution> getShortMessages(int maxLength) {
        return executeWithLogging(
                String.format("Поиск решений с сообщениями короче %d символов", maxLength),
                () -> {
                    if (maxLength <= 0) {
                        throw new IllegalArgumentException("Максимальная длина должна быть положительной");
                    }
                    return solutionRepository.findByShortMessage(maxLength);
                }
        );
    }

    /**
     * Получает решения с длинными сообщениями.
     *
     * @param minLength минимальная длина сообщения
     * @return список решений с длинными сообщениями
     * @throws IllegalArgumentException если параметр длины некорректен
     */
    public List<Solution> getLongMessages(int minLength) {
        return executeWithLogging(
                String.format("Поиск решений с сообщениями длиннее %d символов", minLength),
                () -> {
                    if (minLength < 0) {
                        throw new IllegalArgumentException("Минимальная длина не может быть отрицательной");
                    }
                    return solutionRepository.findByLongMessage(minLength);
                }
        );
    }

    /**
     * Получает статистику количества решений по языкам.
     *
     * @return карта, где ключ - язык, значение - количество решений
     */
    public Map<Language, Long> getSolutionsCountByLanguage() {
        return executeWithLogging(
                "Получение статистики решений по языкам",
                solutionRepository::getSolutionsCountByLanguage
        );
    }

    /**
     * Рассчитывает среднюю длину сообщений всех решений.
     *
     * @return средняя длина сообщений
     */
    public Double getAverageMessageLength() {
        return executeWithLogging(
                "Расчет средней длины сообщений решений",
                () -> {
                    Double avgLength = solutionRepository.getAverageMessageLength();
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
                    validateNotNull(language, "Язык решения");
                    Double avgLength = solutionRepository.getAverageMessageLengthByLanguage(language);
                    return avgLength != null ? avgLength : 0.0;
                }
        );
    }

    /**
     * Получает самые короткие решения в системе.
     *
     * @return список решений с минимальной длиной сообщения
     */
    public List<Solution> getShortestSolutions() {
        return executeWithLogging(
                "Поиск самых коротких решений",
                solutionRepository::findShortestSolutions
        );
    }

    /**
     * Получает самые длинные решения в системе.
     *
     * @return список решений с максимальной длиной сообщения
     */
    public List<Solution> getLongestSolutions() {
        return executeWithLogging(
                "Поиск самых длинных решений",
                solutionRepository::findLongestSolutions
        );
    }

    /**
     * Проверяет существование решения с указанным сообщением.
     *
     * @param message сообщение решения для проверки
     * @return true если решение существует, false в противном случае
     */
    public boolean existsByMessage(String message) {
        return executeWithLogging(
                String.format("Проверка существования решения с сообщением: '%s'", message),
                () -> {
                    if (message == null || message.trim().isEmpty()) {
                        return false;
                    }
                    return solutionRepository.existsByMessage(message.trim());
                }
        );
    }

    /**
     * Получает решение по точному совпадению сообщения.
     *
     * @param message точное сообщение решения
     * @return Optional содержащий найденное решение или пустой
     */
    public Optional<Solution> getByMessage(String message) {
        return executeWithLogging(
                String.format("Поиск решения по точному сообщению: '%s'", message),
                () -> {
                    if (message == null || message.trim().isEmpty()) {
                        return Optional.empty();
                    }
                    return solutionRepository.findByMessage(message.trim());
                }
        );
    }

    /**
     * Удаляет все решения на указанном языке.
     *
     * @param language язык решений для удаления
     * @return количество удаленных решений
     */
    public int deleteByLanguage(Language language) {
        return executeWithLogging(
                String.format("Удаление всех решений на языке: %s", language),
                () -> {
                    validateNotNull(language, "Язык решения");
                    return solutionRepository.deleteByLanguage(language);
                }
        );
    }

    /**
     * Получает общее количество решений в системе.
     *
     * @return общее количество решений
     */
    public long getTotalSolutionsCount() {
        return executeWithLogging(
                "Получение общего количества решений",
                solutionRepository::count
        );
    }

    /**
     * Получает количество решений на указанном языке.
     *
     * @param language язык для подсчета
     * @return количество решений на указанном языке
     */
    public Long countByLanguage(Language language) {
        return executeWithLogging(
                String.format("Подсчет количества решений на языке: %s", language),
                () -> {
                    validateNotNull(language, "Язык решения");
                    Long count = solutionRepository.countByLanguage(language);
                    return count != null ? count : 0L;
                }
        );
    }

    /**
     * Получает решения, сообщения которых начинаются с указанного префикса.
     *
     * @param prefix префикс сообщения
     * @return список решений, чьи сообщения начинаются с указанного префикса
     */
    public List<Solution> getByMessageStartingWith(String prefix) {
        return executeWithLogging(
                String.format("Поиск решений по префиксу сообщения: '%s'", prefix),
                () -> {
                    if (prefix == null || prefix.trim().isEmpty()) {
                        return List.of();
                    }
                    return solutionRepository.findByMessageStartingWith(prefix.trim());
                }
        );
    }

    /**
     * Получает решения, сообщения которых заканчиваются на указанный суффикс.
     *
     * @param suffix суффикс сообщения
     * @return список решений, чьи сообщения заканчиваются на указанный суффикс
     */
    public List<Solution> getByMessageEndingWith(String suffix) {
        return executeWithLogging(
                String.format("Поиск решений по суффиксу сообщения: '%s'", suffix),
                () -> {
                    if (suffix == null || suffix.trim().isEmpty()) {
                        return List.of();
                    }
                    return solutionRepository.findByMessageEndingWith(suffix.trim());
                }
        );
    }

    /**
     * Получает решения по нескольким языкам.
     *
     * @param languages список языков
     * @return список решений на указанных языках
     * @throws IllegalArgumentException если список языков пуст
     */
    public List<Solution> getByLanguages(List<Language> languages) {
        return executeWithLogging(
                String.format("Поиск решений по языкам: %s", languages),
                () -> {
                    if (languages == null || languages.isEmpty()) {
                        throw new IllegalArgumentException("Список языков не может быть пустым");
                    }
                    return solutionRepository.findByLanguages(languages);
                }
        );
    }

    /**
     * Получает решения с сообщениями точной длины.
     *
     * @param exactLength точная длина сообщения
     * @return список решений с сообщениями указанной длины
     * @throws IllegalArgumentException если длина некорректна
     */
    public List<Solution> getByMessageLength(int exactLength) {
        return executeWithLogging(
                String.format("Поиск решений с длиной сообщения: %d символов", exactLength),
                () -> {
                    if (exactLength < 0) {
                        throw new IllegalArgumentException("Длина сообщения не может быть отрицательной");
                    }
                    return solutionRepository.findByMessageLength(exactLength);
                }
        );
    }

    /**
     * Получает решения по языку с сортировкой по длине сообщения.
     *
     * @param language язык решения
     * @param ascending true - по возрастанию, false - по убыванию
     * @return отсортированный список решений
     */
    public List<Solution> getByLanguageOrderByMessageLength(Language language, boolean ascending) {
        return executeWithLogging(
                String.format("Поиск решений по языку %s с сортировкой по длине сообщения (ascending=%s)",
                        language, ascending),
                () -> {
                    validateNotNull(language, "Язык решения");
                    return solutionRepository.findByLanguageOrderByMessageLength(language, ascending);
                }
        );
    }

    /**
     * Проверяет, является ли решение новым (не сохраненным в БД).
     *
     * @param solution объект решения
     * @return true если решение новое, false если существует в БД
     */
    public boolean isNewSolution(Solution solution) {
        return executeWithLogging(
                "Проверка, является ли решение новым",
                () -> solutionRepository.isNew(solution)
        );
    }

    /**
     * Валидирует объект решения.
     *
     * @param solution объект решения для валидации
     * @throws IllegalArgumentException если решение не соответствует требованиям
     */
    private void validateSolution(Solution solution) {
        validateNotNull(solution, "Решение");
        validateNotNull(solution.getLanguage(), "Язык решения");
        validateMessageLength(solution.getMessage());
    }

    /**
     * Валидирует длину сообщения решения.
     *
     * @param message сообщение для валидации
     * @throws IllegalArgumentException если длина сообщения некорректна
     */
    private void validateMessageLength(String message) {
        validateNotEmpty(message, "Сообщение решения");
        validateStringLength(message, "Сообщение решения", 10, 2000);
    }
}