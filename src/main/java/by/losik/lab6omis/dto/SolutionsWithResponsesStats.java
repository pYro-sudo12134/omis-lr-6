package by.losik.lab6omis.dto;

import by.losik.lab6omis.entities.general.types.Solution;

import java.util.List;

/**
 * DTO для возврата статистики по решениям и ответам.
 */
public class SolutionsWithResponsesStats {
    private final long totalSolutions;
    private final long totalResponses;
    private final long solutionsWithoutResponses;
    private final List<Solution> unmatchedSolutions;

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

    public long getTotalSolutions() { return totalSolutions; }
    public long getTotalResponses() { return totalResponses; }
    public long getSolutionsWithoutResponses() { return solutionsWithoutResponses; }
    public List<Solution> getUnmatchedSolutions() { return unmatchedSolutions; }
}