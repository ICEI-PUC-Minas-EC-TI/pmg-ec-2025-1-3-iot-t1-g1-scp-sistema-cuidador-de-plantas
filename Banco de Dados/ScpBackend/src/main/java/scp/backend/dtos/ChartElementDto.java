package scp.backend.dtos;

import java.util.Calendar;

public class ChartElementDto {
    private final float value;
    private final float timestamp;

    public ChartElementDto(float value, Calendar createdAt) {
        this.value = value;
        this.timestamp = (float) createdAt.getTimeInMillis() / 1000f;
    }

    public float getValue() {
        return value;
    }

    public float getTimestamp() {
        return timestamp;
    }
}