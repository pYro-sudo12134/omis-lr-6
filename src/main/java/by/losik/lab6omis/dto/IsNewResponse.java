package by.losik.lab6omis.dto;

/**
 * Ответ с результатом проверки нового объекта.
 * Используется для структурированных REST ответов при проверке, является ли объект новым.
 */
public class IsNewResponse {
    private final boolean isNew;

    /**
     * Создает ответ с результатом проверки новизны объекта.
     *
     * @param isNew true если объект новый, false в противном случае
     */
    public IsNewResponse(boolean isNew) {
        this.isNew = isNew;
    }

    /**
     * Проверяет, является ли объект новым.
     *
     * @return true если объект новый, false в противном случае
     */
    public boolean isNew() {
        return isNew;
    }
}