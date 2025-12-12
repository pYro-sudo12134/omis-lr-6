package by.losik.lab6omis.dto;

/**
 * Ответ с результатом проверки нового объекта.
 */
public class IsNewResponse {
    private final boolean isNew;

    public IsNewResponse(boolean isNew) {
        this.isNew = isNew;
    }

    public boolean isNew() {
        return isNew;
    }
}