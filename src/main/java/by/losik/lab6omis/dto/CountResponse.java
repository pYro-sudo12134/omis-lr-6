package by.losik.lab6omis.dto;

public class CountResponse {
    private final long count;

    public CountResponse(long count) {
        this.count = count;
    }

    public long getCount() {
        return count;
    }
}