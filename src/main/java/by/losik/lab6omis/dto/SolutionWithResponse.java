package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.types.ResponseEntity;
import by.losik.lab6omis.entities.general.types.Solution;

/**
 * DTO для возврата решения с ответом.
 */
public class SolutionWithResponse {
    private final Solution solution;
    private final ResponseEntity response;
    private final boolean hasResponse;

    public SolutionWithResponse(Solution solution, ResponseEntity response, boolean hasResponse) {
        this.solution = solution;
        this.response = response;
        this.hasResponse = hasResponse;
    }

    public Solution getSolution() { return solution; }
    public ResponseEntity getResponse() { return response; }
    public boolean isHasResponse() { return hasResponse; }
}