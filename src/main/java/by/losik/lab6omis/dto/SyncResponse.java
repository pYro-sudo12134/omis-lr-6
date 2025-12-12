package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.types.ResponseEntity;
import by.losik.lab6omis.entities.general.types.Solution;

/**
 * DTO для возврата информации о синхронизации.
 */
public class SyncResponse {
    private final Solution solution;
    private final ResponseEntity response;
    private final boolean responseCreated;

    public SyncResponse(Solution solution, ResponseEntity response, boolean responseCreated) {
        this.solution = solution;
        this.response = response;
        this.responseCreated = responseCreated;
    }

    public Solution getSolution() { return solution; }
    public ResponseEntity getResponse() { return response; }
    public boolean isResponseCreated() { return responseCreated; }
}