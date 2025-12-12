package by.losik.lab6omis.dto;

import java.util.List;

/**
 * Ответ с результатами поиска.
 * Используется для структурированных REST ответов при поиске данных.
 *
 * @param <T> тип возвращаемых объектов
 */
public class SearchResponse<T> {
    private final List<T> results;
    private final int count;

    /**
     * Создает ответ с результатами поиска.
     *
     * @param results список найденных объектов
     */
    public SearchResponse(List<T> results) {
        this.results = results;
        this.count = results != null ? results.size() : 0;
    }

    /**
     * Возвращает список найденных объектов.
     *
     * @return список результатов поиска
     */
    public List<T> getResults() {
        return results;
    }

    /**
     * Возвращает количество найденных объектов.
     *
     * @return количество результатов поиска
     */
    public int getCount() {
        return count;
    }
}