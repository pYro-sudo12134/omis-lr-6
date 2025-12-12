package by.losik.lab6omis.service.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Базовый сервис с общими методами для работы с сущностями.
 * Предоставляет общие операции: логирование, обработка ошибок, валидация.
 *
 * @param <T> тип сущности
 * @param <ID> тип идентификатора сущности
 */
public abstract class BaseService<T, ID> {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    /**
     * Выполняет операцию с логированием и обработкой ошибок.
     *
     * @param operationName название операции для логирования
     * @param operation операция для выполнения
     * @param <R> тип возвращаемого значения
     * @return результат операции
     */
    public <R> R executeWithLogging(String operationName, Supplier<R> operation) {
        LOG.debug("Начало выполнения операции: {}", operationName);

        try {
            R result = operation.get();
            LOG.debug("Операция '{}' выполнена успешно", operationName);
            return result;

        } catch (NotFoundException e) {
            LOG.warn("Операция '{}' не выполнена: {}", operationName, e.getMessage());
            throw e;

        } catch (IllegalArgumentException e) {
            LOG.warn("Некорректные параметры в операции '{}': {}", operationName, e.getMessage());
            throw e;

        } catch (Exception e) {
            LOG.error("Ошибка при выполнении операции '{}': {}", operationName, e.getMessage(), e);
            throw new RuntimeException(
                    String.format("Ошибка при выполнении операции '%s': %s", operationName, e.getMessage()),
                    e
            );
        }
    }

    /**
     * Выполняет операцию без возвращаемого значения с логированием и обработкой ошибок.
     *
     * @param operationName название операции для логирования
     * @param operation операция для выполнения
     */
    public void executeVoidWithLogging(String operationName, Runnable operation) {
        LOG.debug("Начало выполнения операции: {}", operationName);

        try {
            operation.run();
            LOG.debug("Операция '{}' выполнена успешно", operationName);

        } catch (NotFoundException e) {
            LOG.warn("Операция '{}' не выполнена: {}", operationName, e.getMessage());
            throw e;

        } catch (IllegalArgumentException e) {
            LOG.warn("Некорректные параметры в операции '{}': {}", operationName, e.getMessage());
            throw e;

        } catch (Exception e) {
            LOG.error("Ошибка при выполнении операции '{}': {}", operationName, e.getMessage(), e);
            throw new RuntimeException(
                    String.format("Ошибка при выполнении операции '%s': %s", operationName, e.getMessage()),
                    e
            );
        }
    }

    /**
     * Получает сущность по ID с обработкой Optional.
     *
     * @param id идентификатор сущности
     * @param findOperation операция поиска
     * @param entityName название сущности для сообщений об ошибках
     * @return найденная сущность
     * @throws NotFoundException если сущность не найдена
     */
    protected T getEntityById(ID id, Supplier<Optional<T>> findOperation, String entityName) {
        return executeWithLogging(
                String.format("Получение %s по ID: %s", entityName, id),
                () -> findOperation.get()
                        .orElseThrow(() -> new NotFoundException(
                                String.format("%s с ID %s не найден", entityName, id)
                        ))
        );
    }

    /**
     * Проверяет существование сущности по ID.
     *
     * @param id идентификатор сущности
     * @param existsOperation операция проверки существования
     * @param entityName название сущности для сообщений об ошибках
     * @throws NotFoundException если сущность не существует
     */
    protected void ensureEntityExists(ID id, Supplier<Boolean> existsOperation, String entityName) {
        executeVoidWithLogging(
                String.format("Проверка существования %s по ID: %s", entityName, id),
                () -> {
                    if (!existsOperation.get()) {
                        throw new NotFoundException(
                                String.format("%s с ID %s не найден", entityName, id)
                        );
                    }
                }
        );
    }

    /**
     * Проверяет, что объект не является null.
     *
     * @param obj объект для проверки
     * @param fieldName название поля для сообщения об ошибке
     * @throws IllegalArgumentException если объект null
     */
    protected void validateNotNull(Object obj, String fieldName) {
        if (obj == null) {
            throw new IllegalArgumentException(fieldName + " не может быть null");
        }
    }

    /**
     * Проверяет, что строка не является null или пустой.
     *
     * @param str строка для проверки
     * @param fieldName название поля для сообщения об ошибке
     * @throws IllegalArgumentException если строка null или пустая
     */
    protected void validateNotEmpty(String str, String fieldName) {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " не может быть пустым");
        }
    }

    /**
     * Проверяет длину строки.
     *
     * @param str строка для проверки
     * @param fieldName название поля для сообщения об ошибке
     * @param min минимальная длина
     * @param max максимальная длина
     * @throws IllegalArgumentException если строка не соответствует ограничениям
     */
    protected void validateStringLength(String str, String fieldName, int min, int max) {
        validateNotEmpty(str, fieldName);

        int length = str.trim().length();
        if (length < min || length > max) {
            throw new IllegalArgumentException(
                    String.format("%s должна содержать от %d до %d символов. Получено: %d",
                            fieldName, min, max, length)
            );
        }
    }

    /**
     * Проверяет числовое значение.
     *
     * @param value числовое значение
     * @param fieldName название поля для сообщения об ошибке
     * @param min минимальное значение
     * @param max максимальное значение
     * @throws IllegalArgumentException если значение не соответствует ограничениям
     */
    protected void validateNumberRange(Number value, String fieldName, Number min, Number max) {
        validateNotNull(value, fieldName);

        double numValue = value.doubleValue();
        if (numValue < min.doubleValue() || numValue > max.doubleValue()) {
            throw new IllegalArgumentException(
                    String.format("%s должен быть в диапазоне от %s до %s. Получено: %s",
                            fieldName, min, max, value)
            );
        }
    }

    /**
     * Проверяет, что значение положительное.
     *
     * @param value числовое значение
     * @param fieldName название поля для сообщения об ошибке
     * @throws IllegalArgumentException если значение не положительное
     */
    protected void validatePositive(Number value, String fieldName) {
        validateNotNull(value, fieldName);

        if (value.doubleValue() <= 0) {
            throw new IllegalArgumentException(
                    String.format("%s должен быть положительным числом. Получено: %s", fieldName, value)
            );
        }
    }

    /**
     * Очищает список строк (удаляет null, обрезает пробелы, удаляет пустые).
     *
     * @param strings список строк для очистки
     * @return очищенный список
     */
    protected List<String> cleanStringList(List<String> strings) {
        if (strings == null) {
            return List.of();
        }

        return strings.stream()
                .filter(java.util.Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Преобразует список с обработкой исключений.
     *
     * @param list список для преобразования
     * @param mapper функция преобразования
     * @param operationName название операции для логирования
     * @param <S> тип исходных элементов
     * @param <R> тип результирующих элементов
     * @return преобразованный список
     */
    protected <S, R> List<R> mapListWithLogging(
            List<S> list,
            Function<S, R> mapper,
            String operationName) {

        return executeWithLogging(
                String.format("Преобразование списка: %s", operationName),
                () -> list.stream()
                        .map(mapper)
                        .collect(java.util.stream.Collectors.toList())
        );
    }

    /**
     * Выполняет операцию для каждого элемента списка с обработкой исключений.
     *
     * @param list список элементов
     * @param consumer операция для выполнения
     * @param operationName название операции для логирования
     * @param <S> тип элементов
     */
    protected <S> void forEachWithLogging(
            List<S> list,
            Consumer<S> consumer,
            String operationName) {

        executeVoidWithLogging(
                String.format("Обработка списка: %s", operationName),
                () -> list.forEach(consumer)
        );
    }

    /**
     * Обрабатывает Optional результат с логированием.
     *
     * @param optional Optional результат
     * @param entityName название сущности для логирования
     * @param id идентификатор для логирования
     * @return результат или null если отсутствует
     */
    protected T handleOptionalResult(Optional<T> optional, String entityName, Object id) {
        if (optional.isPresent()) {
            LOG.debug("{} с идентификатором {} найден", entityName, id);
            return optional.get();
        } else {
            LOG.debug("{} с идентификатором {} не найден", entityName, id);
            return null;
        }
    }

    /**
     * Создает стандартный ответ для REST контроллеров.
     *
     * @param entity сущность
     * @param status статус ответа
     * @return ResponseEntity
     */
    protected Response buildResponse(T entity, Response.Status status) {
        return Response.status(status)
                .entity(entity)
                .build();
    }

    /**
     * Создает стандартный ответ для REST контроллеров с сообщением.
     *
     * @param message сообщение
     * @param status статус ответа
     * @return ResponseEntity
     */
    protected Response buildResponse(String message, Response.Status status) {
        return Response.status(status)
                .entity(java.util.Map.of("message", message))
                .build();
    }

    /**
     * Проверяет уникальность значения.
     *
     * @param value проверяемое значение
     * @param checkOperation операция проверки уникальности
     * @param fieldName название поля
     * @param entityName название сущности
     * @throws IllegalArgumentException если значение не уникально
     */
    protected void validateUnique(Object value, Supplier<Boolean> checkOperation,
                                  String fieldName, String entityName) {
        if (checkOperation.get()) {
            throw new IllegalArgumentException(
                    String.format("%s с таким %s уже существует", entityName, fieldName)
            );
        }
    }

    /**
     * Валидирует параметры пагинации.
     *
     * @param page номер страницы
     * @param size размер страницы
     * @throws IllegalArgumentException если параметры некорректны
     */
    protected void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Номер страницы не может быть отрицательным");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Размер страницы должен быть положительным");
        }
    }
}