package scp.scp;

import com.github.mikephil.charting.data.BarEntry;

public class ChartEntry {
    private float value;
    private float timestamp;

    public BarEntry getEntry() {
        return new BarEntry(timestamp, value);
    }
}
