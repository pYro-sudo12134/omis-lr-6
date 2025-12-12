package by.losik.lab6omis.dto;

/**
 * Ответ с результатом проверки существования.
 */
public class ExistsResponse {
    private final boolean exists;

    public ExistsResponse(boolean exists) {
        this.exists = exists;
    }

    public boolean isExists() {
        return exists;
    }
}