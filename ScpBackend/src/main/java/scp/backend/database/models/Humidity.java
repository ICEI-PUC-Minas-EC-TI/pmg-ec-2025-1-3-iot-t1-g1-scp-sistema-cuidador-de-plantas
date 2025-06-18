package scp.backend.database.models;

import scp.backend.dtos.ChartElementDto;
import scp.backend.dtos.Chartable;

import java.util.Calendar;

public class Humidity implements Chartable {
    private int id;
    private final double humidity;
    private Calendar createdAt;

    public Humidity(String humidity) {
        this.humidity = Double.parseDouble(humidity);
    }

    public Humidity(int id, double humidity, Calendar createdAt) {
        this.id = id;
        this.humidity = humidity;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public double getHumidity() {
        return humidity;
    }

    public Calendar getCreatedAt() {
        return createdAt;
    }

    @Override
    public ChartElementDto toChartElementDto() {
        return new ChartElementDto((float) humidity, createdAt);
    }
}
