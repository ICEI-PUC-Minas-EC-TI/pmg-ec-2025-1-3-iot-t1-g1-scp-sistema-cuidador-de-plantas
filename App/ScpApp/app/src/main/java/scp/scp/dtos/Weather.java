package scp.scp.dtos;

public class Weather {
    private int id;

    public int getId() {
        return id;
    }

    public WeatherType getType() {
        if(id >= 200 && id <= 622) {
            return WeatherType.Rain;
        }

        if((id >= 701 && id <= 781) || (id >= 801 && id <= 804)) {
            return WeatherType.CloseDay;
        }

        return WeatherType.Clear;
    }
}
