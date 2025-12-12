package by.losik.lab6omis.dto;

/**
 * DTO класс для ответа с информацией об удаленных записях.
 * Используется для структурированных REST ответов при операциях удаления.
 *
 * @author Losik Yaroslav
 * @version 1.0
 */
public class DeleteResponse {

    private final int deletedCount;
    private final String message;
    private final String timestamp;

    /**
     * Создает ответ с количеством удаленных записей.
     *
     * @param deletedCount количество удаленных записей
     */
    public DeleteResponse(int deletedCount) {
        this.deletedCount = deletedCount;
        this.message = deletedCount > 0
                ? String.format("Успешно удалено %d записей", deletedCount)
                : "Записи для удаления не найдены";
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    /**
     * Создает ответ с количеством удаленных записей и пользовательским сообщением.
     *
     * @param deletedCount количество удаленных записей
     * @param message пользовательское сообщение
     */
    public DeleteResponse(int deletedCount, String message) {
        this.deletedCount = deletedCount;
        this.message = message != null ? message :
                (deletedCount > 0
                        ? String.format("Успешно удалено %d записей", deletedCount)
                        : "Записи для удаления не найдены");
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    /**
     * Создает ответ с количеством удаленных записей, сообщением и меткой времени.
     *
     * @param deletedCount количество удаленных записей
     * @param message пользовательское сообщение
     * @param timestamp метка времени операции
     */
    public DeleteResponse(int deletedCount, String message, String timestamp) {
        this.deletedCount = deletedCount;
        this.message = message;
        this.timestamp = timestamp;
    }

    /**
     * Возвращает количество удаленных записей.
     *
     * @return количество удаленных записей
     */
    public int getDeletedCount() {
        return deletedCount;
    }

    /**
     * Возвращает сообщение об операции удаления.
     *
     * @return сообщение об операции
     */
    public String getMessage() {
        return message;
    }

    /**
     * Возвращает метку времени выполнения операции.
     *
     * @return метка времени в формате ISO-8601
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Проверяет, были ли удалены какие-либо записи.
     *
     * @return true если удалены одна или более записей, false в противном случае
     */
    public boolean isSuccess() {
        return deletedCount > 0;
    }

    /**
     * Проверяет, произошла ли операция удаления без фактического удаления записей
     * (например, когда записей для удаления не найдено).
     *
     * @return true если операция выполнена, но записи не найдены, false в противном случае
     */
    public boolean isNoRecordsFound() {
        return deletedCount == 0;
    }

    @Override
    public String toString() {
        return String.format(
                "DeleteResponse{deletedCount=%d, message='%s', timestamp='%s'}",
                deletedCount, message, timestamp
        );
    }

    /**
     * Создает успешный ответ с количеством удаленных записей.
     *
     * @param deletedCount количество удаленных записей
     * @return объект DeleteResponse
     */
    public static DeleteResponse success(int deletedCount) {
        return new DeleteResponse(deletedCount);
    }

    /**
     * Создает ответ об отсутствии записей для удаления.
     *
     * @return объект DeleteResponse с нулевым количеством удаленных записей
     */
    public static DeleteResponse noRecords() {
        return new DeleteResponse(0, "Записи для удаления не найдены");
    }

    /**
     * Создает ответ с ошибкой операции удаления.
     *
     * @param errorMessage сообщение об ошибке
     * @return объект DeleteResponse с нулевым количеством удаленных записей и сообщением об ошибке
     */
    public static DeleteResponse error(String errorMessage) {
        return new DeleteResponse(0, errorMessage);
    }

    /**
     * Создает ответ с ошибкой операции удаления.
     *
     * @param errorMessage сообщение об ошибке
     * @param cause причина ошибки
     * @return объект DeleteResponse с нулевым количеством удаленных записей и сообщением об ошибке
     */
    public static DeleteResponse error(String errorMessage, Throwable cause) {
        return new DeleteResponse(
                0,
                String.format("%s: %s", errorMessage, cause.getMessage())
        );
    }
}