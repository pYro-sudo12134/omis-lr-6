package by.losik.lab6omis.dto;

/**
 * DTO класс для ответа со средней длиной сообщений.
 */
public class AverageLengthResponse {
    private final Double averageLength;
    private final String formattedLength;

    public AverageLengthResponse(Double averageLength) {
        this.averageLength = averageLength;
        this.formattedLength = averageLength != null ?
                String.format("%.2f символов", averageLength) : "N/A";
    }

    public Double getAverageLength() {
        return averageLength;
    }

    public String getFormattedLength() {
        return formattedLength;
    }
}