package scp.scp;

import android.location.Location;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import scp.scp.dtos.WeatherResponse;

public class WeatherApi {
    private static final String LogTag = "WeatherApi";
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public Optional<WeatherResponse> GetWeatherInformation(Location location) {
        String url = String.format(Locale.getDefault(),
                "https://api.openweathermap.org/data/2.5/weather?lat=%f&lon=%f&appid=%s",
                location.getLatitude(), location.getLongitude(), BuildConfig.WEATHER_KEY);
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if(!response.isSuccessful()) {
                return Optional.empty();
            }

            ResponseBody body = response.body();
            if(body == null) {
                return Optional.empty();
            }

            String bodyString = body.string();
            WeatherResponse responseBodyParsed = gson.fromJson(bodyString, WeatherResponse.class);

            return Optional.of(responseBodyParsed);
        } catch (IOException e) {
            Log.e(LogTag, "Erro ao obter as informações de clima", e);
            return Optional.empty();
        }
    }

}
