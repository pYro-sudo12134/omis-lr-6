package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.types.Solution;
import java.util.List;

/**
 * DTO для возврата статистики по решениям и ответам.
 * Используется для структурированных REST ответов при анализе соответствия решений и ответов.
 */
public class SolutionsWithResponsesStats {
    private final long totalSolutions;
    private final long totalResponses;
    private final long solutionsWithoutResponses;
    private final List<Solution> unmatchedSolutions;

    /**
     * Создает объект статистики по решениям и ответам.
     *
     * @param totalSolutions общее количество решений
     * @param totalResponses общее количество ответов
     * @param solutionsWithoutResponses количество решений без ответов
     * @param unmatchedSolutions список решений без соответствующих ответов
     */
    public SolutionsWithResponsesStats(
            long totalSolutions,
            long totalResponses,
            long solutionsWithoutResponses,
            List<Solution> unmatchedSolutions) {

        this.totalSolutions = totalSolutions;
        this.totalResponses = totalResponses;
        this.solutionsWithoutResponses = solutionsWithoutResponses;
        this.unmatchedSolutions = unmatchedSolutions;
    }

    /**
     * Возвращает общее количество решений.
     *
     * @return общее количество решений
     */
    public long getTotalSolutions() {
        return totalSolutions;
    }

    /**
     * Возвращает общее количество ответов.
     *
     * @return общее количество ответов
     */
    public long getTotalResponses() {
        return totalResponses;
    }

    /**
     * Возвращает количество решений без ответов.
     *
     * @return количество решений без ответов
     */
    public long getSolutionsWithoutResponses() {
        return solutionsWithoutResponses;
    }

    /**
     * Возвращает список решений без соответствующих ответов.
     *
     * @return список решений без ответов
     */
    public List<Solution> getUnmatchedSolutions() {
        return unmatchedSolutions;
    }
}