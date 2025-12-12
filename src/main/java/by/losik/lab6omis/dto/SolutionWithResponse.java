package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.types.ResponseEntity;
import by.losik.lab6omis.entities.general.types.Solution;

/**
 * DTO для возврата решения с ответом.
 * Используется для структурированных REST ответов при получении решения с связанным ответом.
 */
public class SolutionWithResponse {
    private final Solution solution;
    private final ResponseEntity response;
    private final boolean hasResponse;

    /**
     * Создает объект с решением и связанным ответом.
     *
     * @param solution объект решения
     * @param response связанный ответ (может быть null, если ответ отсутствует)
     * @param hasResponse флаг, указывающий наличие ответа
     */
    public SolutionWithResponse(Solution solution, ResponseEntity response, boolean hasResponse) {
        this.solution = solution;
        this.response = response;
        this.hasResponse = hasResponse;
    }

    /**
     * Возвращает объект решения.
     *
     * @return объект решения
     */
    public Solution getSolution() {
        return solution;
    }

    /**
     * Возвращает связанный ответ.
     *
     * @return объект ответа или null, если ответ отсутствует
     */
    public ResponseEntity getResponse() {
        return response;
    }

    /**
     * Проверяет наличие связанного ответа.
     *
     * @return true если ответ существует, false в противном случае
     */
    public boolean isHasResponse() {
        return hasResponse;
    }
}