package by.losik.lab6omis.dto;

import java.util.List;

/**
 * Ответ с результатами поиска.
 */
public class SearchResponse<T> {
    private final List<T> results;
    private final int count;

    public SearchResponse(List<T> results) {
        this.results = results;
        this.count = results != null ? results.size() : 0;
    }

    public List<T> getResults() {
        return results;
    }

    public int getCount() {
        return count;
    }
}