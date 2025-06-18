package scp.backend.database.models;

import scp.backend.dtos.ChartElementDto;
import scp.backend.dtos.Chartable;

import java.util.Calendar;

public class Temperature implements Chartable {
    private int id;
    private final double temperature;
    private Calendar createdAt;

    public Temperature(String temperature) {
        this.temperature = Double.parseDouble(temperature);
    }

    public Temperature(int id, double temperature, Calendar createdAt) {
        this.id = id;
        this.temperature = temperature;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public double getTemperature() {
        return temperature;
    }

    public Calendar getCreatedAt() {
        return createdAt;
    }

    @Override
    public ChartElementDto toChartElementDto() {
        return new ChartElementDto((float) temperature, createdAt);
    }
}
