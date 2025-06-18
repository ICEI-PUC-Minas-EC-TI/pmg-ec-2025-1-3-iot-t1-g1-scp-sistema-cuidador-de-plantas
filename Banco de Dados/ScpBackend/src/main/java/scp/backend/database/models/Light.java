package scp.backend.database.models;

import scp.backend.dtos.ChartElementDto;
import scp.backend.dtos.Chartable;

import java.util.Calendar;

public class Light implements Chartable {
    private int id;
    private final double lux;
    private final double percentage;
    private Calendar createdAt;

    public Light(String message) {
        lux = Double.parseDouble(message);
        percentage = (lux / 90) * 100;
    }

    public Light(int id, double lux, double percentage, Calendar createdAt) {
        this.id = id;
        this.lux = lux;
        this.percentage = percentage;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public double getLux() {
        return lux;
    }

    public double getPercentage() {
        return percentage;
    }

    public Calendar getCreatedAt() {
        return createdAt;
    }

    @Override
    public ChartElementDto toChartElementDto() {
        return new ChartElementDto((float) percentage, createdAt);
    }
}
