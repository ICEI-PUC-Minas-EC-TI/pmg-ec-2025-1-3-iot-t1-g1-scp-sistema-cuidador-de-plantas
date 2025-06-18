package scp.scp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import scp.scp.dtos.WeatherResponse;


public class MainActivity extends AppCompatActivity {
    private final MqttAdapter mqttAdapter;
    private float lux = 0;
    private static final int REQUEST_LOCATION_PERMISSION = 100;
    private FusedLocationProviderClient fusedLocationClient;

    public MainActivity() {
        mqttAdapter = new MqttAdapter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        connectMqtt();
        configureSeekBars();

        configureClickReportView();
        requestLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                getWeatherInfo();
            }

        } else {
            TextView weatherResumeText = findViewById(R.id.weatherResume);
            weatherResumeText.setText("É necessario permissão de localização para ver o estado do tempo");
        }
    }

    private void requestLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            getWeatherInfo();
        }
    }


    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    private void getWeatherInfo() {
        LocationRequest request = new LocationRequest.Builder(1000)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                        .build();

        fusedLocationClient.requestLocationUpdates(
                request,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult result) {
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        Location location =  result.getLastLocation();
                        if(location == null) {
                            runOnUiThread(() -> {
                                TextView weatherResumeText = findViewById(R.id.weatherResume);
                                weatherResumeText.setText("Erro ao obter informações do tempo");
                            });
                            return;
                        }

                        executor.execute(() -> {
                            WeatherApi api = new WeatherApi();
                            Optional<WeatherResponse> weather = api.GetWeatherInformation(location);
                            if (weather.isEmpty())
                                return;
                            String finalWeatherScreen = getFinalWeather(weather.get());
                            runOnUiThread(() -> {
                                TextView weatherResumeText = findViewById(R.id.weatherResume);
                                weatherResumeText.setText(String.format("A previsão para hoje é %s", finalWeatherScreen));
                            });
                        });
                        fusedLocationClient.removeLocationUpdates(this);
                    }
                },
                Looper.getMainLooper()
        );
    }

    @NonNull
    private static String getFinalWeather(WeatherResponse weather) {
        String weatherScreen = "";
        switch (weather.getFirst().getType()) {
            case Rain:
                weatherScreen = "chuva";
                break;
            case CloseDay:
                weatherScreen = "nublado";
                break;
            case Clear:
                weatherScreen = "ensolarado";
                break;
        }

        return weatherScreen;
    }

    private void configureClickReportView() {
        openNextView(findViewById(R.id.vwSoilHumidity), "Umidade da Terra");
        openNextView(findViewById(R.id.vwHumidity), "Umidade da Estufa");
        openNextView(findViewById(R.id.vwLight), "Luminosidade");
        openNextView(findViewById(R.id.vwTemperature), "Temperatura");
    }

    private void openNextView(View view, String reportName) {
        view.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ReportActivity.class);
                intent.putExtra("REPORT_NAME", reportName);
                MainActivity.this.startActivity(intent);
            }
        });

    }

    private void configureSeekBars() {
        configureLightSeekBar();
        configureIrrigation();
        configureTempSeekBar();
    }

    private void configureLightSeekBar() {
        Optional<String> sensibilityRaw = mqttAdapter.receiveUnique("mini_estufa/barra_sensi");
        Optional<String> initialLuxRaw = mqttAdapter.receiveUnique("mini_estufa/luminosidade");
        if (sensibilityRaw.isPresent() && initialLuxRaw.isPresent()) {
            float sensibility = Float.parseFloat(sensibilityRaw.get());
            lux = Float.parseFloat(initialLuxRaw.get());

            double calc = 100 - ((lux * sensibility / 255.0) * 100);
            int finalSensibility = (int) calc;

            SeekBarAdapter seekBarLightSensitivity = new SeekBarAdapter(
                    findViewById(R.id.seekBarLightSensitivity),
                    findViewById(R.id.tvLightSensitivity),
                    0,
                    100,
                    1,
                    "%d%%",
                    finalSensibility
            );

            seekBarLightSensitivity.addEventListener((value) -> {
                double finalValue = (255 - ((value / 100.0) * 255)) / lux;
                mqttAdapter.sendMessage("mini_estufa/sensibilidade", Double.toString(finalValue));
            });
        }
    }

    private void configureTempSeekBar() {
        Optional<String> tempRaw = mqttAdapter.receiveUnique("mini_estufa/barra_temp");
        if (tempRaw.isEmpty())
            return;

        int temp = (int) Math.floor(Float.parseFloat(tempRaw.get()));
        SeekBarAdapter seekBarTemperature = new SeekBarAdapter(
                findViewById(R.id.seekBarTemperature),
                findViewById(R.id.tvTemperatureThreshold),
                10,
                50,
                1,
                "%d°C",
                temp
        );

        seekBarTemperature.addEventListener(t -> mqttAdapter.sendMessage("mini_estufa/temp_value", Integer.toString(t)));
    }

    private void configureIrrigation() {
        Optional<String> humidityRaw = mqttAdapter.receiveUnique("mini_estufa/umidade_value_atual");
        if (humidityRaw.isEmpty())
            return;

        int humidity = (int) Math.floor(((4095 - Float.parseFloat(humidityRaw.get())) / 4095) * 100);

        SeekBarAdapter irrigationSeekbar = new SeekBarAdapter(
                findViewById(R.id.seekBarHumidity),
                findViewById(R.id.tvHumidityThreshold),
                0,
                100,
                1,
                "%d%%",
                humidity
        );

        irrigationSeekbar.addEventListener(value -> {
            int newIrrigationValue = (int) (4095 - (value / 100.0) * 4095);
            mqttAdapter.sendMessage("mini_estufa/umidade_value", Integer.toString(newIrrigationValue));
        });
    }


    private void connectMqtt() {
        mqttAdapter.initConnection();
        mqttAdapter.receiveMessages("mini_estufa/temperatura", this::handleTempGreenHouse);
        mqttAdapter.receiveMessages("mini_estufa/luminosidade", this::handleLightGreenHouse);
        mqttAdapter.receiveMessages("mini_estufa/umidade_solo", this::handleSoilMoisture);
        mqttAdapter.receiveMessages("mini_estufa/umidade_ar", this::handleHumidityGreenHouse);
    }

    private void handleTempGreenHouse(@NonNull String temp) {
        runOnUiThread(() -> {
            TextView tempView = findViewById(R.id.tvTemperature);
            tempView.setText(String.format("%s °C", temp.replace(".", ",")));
        });
    }

    private void handleLightGreenHouse(@NonNull String lightString) {
        runOnUiThread(() -> {
            TextView lightView = findViewById(R.id.tvLightIntensity);
            float light = Float.parseFloat(lightString);
            lux = light;

            lightView.setText(String.format(new Locale("pt", "BR"), "%.2f%%", (light / 90) * 100));
        });
    }

    private void handleSoilMoisture(@NonNull String humidity) {
        runOnUiThread(() -> {
            TextView humidityText = findViewById(R.id.tvSoilMoisture);
            float humidityFloat = Float.parseFloat(humidity);

            humidityText.setText(String.format(new Locale("pt", "BR"), "%.2f%%", ((4095 - humidityFloat) / 4095) * 100));
        });
    }

    private void handleHumidityGreenHouse(@NonNull String humidity) {
        runOnUiThread(() -> {
            TextView humidityText = findViewById(R.id.tvGreenhouseHumidity);
            humidityText.setText(String.format("%s %%", humidity.replace(".", ",")));
        });
    }

}