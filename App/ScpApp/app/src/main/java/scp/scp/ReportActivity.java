package scp.scp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReportActivity extends AppCompatActivity {

    private String reportName;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        barChart = findViewById(R.id.barChart);
        barChart.setNoDataText("Carregando...");

        reportName = getIntent().getStringExtra("REPORT_NAME");
        setTitleScreen(reportName);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            MqttAdapter mqttAdapter = new MqttAdapter();
            mqttAdapter.initConnection();
            try {
                Optional<String> rawReport = mqttAdapter.sendCommand("mini_estufa/comandos",
                        "report|" + reportName,
                        "mini_estufa/resultado");
                if(rawReport.isEmpty())
                    return;

                String content = rawReport.get();

                Type listType = new TypeToken<List<ChartEntry>>() {}.getType();
                List<ChartEntry> entries = new Gson()
                        .fromJson(content, listType);

                initChart(reportName, entries);
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void setTitleScreen(String reportName) {

        TextView titleView = findViewById(R.id.titleTextView);
        titleView.setText(reportName);
    }

    private void initChart(String reportName, List<ChartEntry> entriesReceived) {
        List<BarEntry> entries = new ArrayList<>();
        for (ChartEntry element : entriesReceived) {
            entries.add(element.getEntry());
        }

        runOnUiThread(() -> {
            BarDataSet dataSet = new BarDataSet(entries, reportName);

            BarData barData = new BarData(dataSet);
            barData.setBarWidth(200);
            barChart.setData(barData);

            XAxis xAxis = formatXAxis(barChart);;

            barChart.getDescription().setEnabled(false);
            barChart.animateY(1000);
            barChart.invalidate();
        });
    }

    @NonNull
    private static XAxis formatXAxis(BarChart chart) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter() {
            private final SimpleDateFormat sdf
                    = new SimpleDateFormat("dd-HH:mm", Locale.getDefault());


            @Override
            public String getFormattedValue(float value) {
                long millis = ((long) value * 1000L) - 10800000L;
                return sdf.format(new Date(millis));
            }
        });

//        xAxis.setGranularity(1f);
//        xAxis.setGranularityEnabled(false);
        return xAxis;
    }
}