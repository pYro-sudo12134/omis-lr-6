package by.losik.lab6omis.dto;

/**
 * Ответ с результатом проверки существования.
 * Используется для структурированных REST ответов при проверке существования объекта.
 */
public class ExistsResponse {
    private final boolean exists;

    /**
     * Создает ответ с результатом проверки существования объекта.
     *
     * @param exists true если объект существует, false в противном случае
     */
    public ExistsResponse(boolean exists) {
        this.exists = exists;
    }

    /**
     * Проверяет, существует ли объект.
     *
     * @return true если объект существует, false в противном случае
     */
    public boolean isExists() {
        return exists;
    }
}