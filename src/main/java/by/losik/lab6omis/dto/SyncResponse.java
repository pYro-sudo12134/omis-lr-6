package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.types.ResponseEntity;
import by.losik.lab6omis.entities.general.types.Solution;

/**
 * DTO для возврата информации о синхронизации.
 * Используется для структурированных REST ответов при синхронизации решений и ответов.
 */
public class SyncResponse {
    private final Solution solution;
    private final ResponseEntity response;
    private final boolean responseCreated;

    /**
     * Создает ответ с информацией о синхронизации.
     *
     * @param solution синхронизированное решение
     * @param response связанный ответ (может быть null, если ответ не создавался)
     * @param responseCreated флаг, указывающий был ли создан новый ответ
     */
    public SyncResponse(Solution solution, ResponseEntity response, boolean responseCreated) {
        this.solution = solution;
        this.response = response;
        this.responseCreated = responseCreated;
    }

    /**
     * Возвращает синхронизированное решение.
     *
     * @return объект решения
     */
    public Solution getSolution() {
        return solution;
    }

    /**
     * Возвращает связанный ответ.
     *
     * @return объект ответа или null, если ответ не создавался
     */
    public ResponseEntity getResponse() {
        return response;
    }

    /**
     * Проверяет, был ли создан новый ответ при синхронизации.
     *
     * @return true если был создан новый ответ, false в противном случае
     */
    public boolean isResponseCreated() {
        return responseCreated;
    }
}