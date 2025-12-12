package by.losik.lab6omis.dto;

/**
 * DTO для возврата информации об удалении решения с ответом.
 */
public class DeleteWithResponseResponse {
    private final int deletedSolutions;
    private final int deletedResponses;

    public DeleteWithResponseResponse(int deletedSolutions, int deletedResponses) {
        this.deletedSolutions = deletedSolutions;
        this.deletedResponses = deletedResponses;
    }

    public int getDeletedSolutions() { return deletedSolutions; }
    public int getDeletedResponses() { return deletedResponses; }
}