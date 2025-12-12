package by.losik.lab6omis.dto;

/**
 * DTO класс для ответа с количеством записей.
 * Используется для структурированных REST ответов при операциях подсчета.
 * Содержит только количество записей без дополнительной мета-информации.
 *
 * @author Losik Yaroslav
 * @version 1.0
 */
public class CountResponse {
    private final long count;

    /**
     * Создает ответ с указанным количеством записей.
     *
     * @param count количество записей
     */
    public CountResponse(long count) {
        this.count = count;
    }

    /**
     * Возвращает количество записей.
     *
     * @return количество записей
     */
    public long getCount() {
        return count;
    }
}