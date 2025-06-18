package scp.scp.dtos;

import java.util.List;

public class WeatherResponse {
    private List<Weather> weather;

    public List<Weather> getWeather() {
        return weather;
    }

    public Weather getFirst() {
        return weather.get(0);
    }
}
