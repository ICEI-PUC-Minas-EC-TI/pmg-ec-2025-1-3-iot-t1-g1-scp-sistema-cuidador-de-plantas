package scp.backend.database.models;

import scp.backend.dtos.ChartElementDto;
import scp.backend.dtos.Chartable;

import java.util.Calendar;

public class SoilHumidity implements Chartable {
    private int id;
    private final double raw;
    private final double percentage;
    private Calendar createdAt;

    public SoilHumidity(String message) {
        raw = Double.parseDouble(message);
        percentage = ((4095 - raw) / 4095.0) * 100;
    }

    public SoilHumidity(int id, double raw, double percentage, Calendar createdAt) {
        this.id = id;
        this.raw = raw;
        this.percentage = percentage;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public double getRaw() {
        return raw;
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
