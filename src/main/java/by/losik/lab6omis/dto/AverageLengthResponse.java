package by.losik.lab6omis.dto;

/**
 * DTO класс для ответа со средней длиной сообщений.
 * Используется для структурированных REST ответов при расчете средней длины.
 */
public class AverageLengthResponse {
    private final Double averageLength;
    private final String formattedLength;

    /**
     * Создает ответ со средней длиной сообщений.
     *
     * @param averageLength средняя длина сообщений (может быть null, если данные отсутствуют)
     */
    public AverageLengthResponse(Double averageLength) {
        this.averageLength = averageLength;
        this.formattedLength = averageLength != null ?
                String.format("%.2f символов", averageLength) : "N/A";
    }

    /**
     * Возвращает среднюю длину сообщений.
     *
     * @return средняя длина сообщений или null, если данные отсутствуют
     */
    public Double getAverageLength() {
        return averageLength;
    }

    /**
     * Возвращает отформатированное представление средней длины.
     *
     * @return отформатированная строка со средней длиной или "N/A", если данные отсутствуют
     */
    public String getFormattedLength() {
        return formattedLength;
    }
}