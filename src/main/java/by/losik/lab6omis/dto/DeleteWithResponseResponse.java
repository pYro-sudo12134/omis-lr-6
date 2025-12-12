package by.losik.lab6omis.dto;

/**
 * DTO для возврата информации об удалении решения с ответом.
 * Используется для структурированных REST ответов при каскадном удалении решений и связанных ответов.
 */
public class DeleteWithResponseResponse {
    private final int deletedSolutions;
    private final int deletedResponses;

    /**
     * Создает ответ с количеством удаленных решений и ответов.
     *
     * @param deletedSolutions количество удаленных решений
     * @param deletedResponses количество удаленных связанных ответов
     */
    public DeleteWithResponseResponse(int deletedSolutions, int deletedResponses) {
        this.deletedSolutions = deletedSolutions;
        this.deletedResponses = deletedResponses;
    }

    /**
     * Возвращает количество удаленных решений.
     *
     * @return количество удаленных решений
     */
    public int getDeletedSolutions() {
        return deletedSolutions;
    }

    /**
     * Возвращает количество удаленных ответов.
     *
     * @return количество удаленных ответов
     */
    public int getDeletedResponses() {
        return deletedResponses;
    }
}