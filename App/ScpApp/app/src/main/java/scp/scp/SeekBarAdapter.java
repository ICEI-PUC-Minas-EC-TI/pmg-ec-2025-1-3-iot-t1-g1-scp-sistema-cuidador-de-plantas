package scp.scp;

import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.function.Consumer;

public class SeekBarAdapter {
    private final SeekBar seekBar;
    private final TextView labelSeekBar;
    private final String template;
    private final int min;
    private final int step;

    public SeekBarAdapter(@NonNull SeekBar seekBar, @NonNull TextView labelSeekBar, int min, int max, int step, @NonNull String template, int initialValue) {
        this.seekBar = seekBar;
        this.labelSeekBar = labelSeekBar;
        this.min = min;
        this.step = step;
        this.template = template;

        labelSeekBar.setText(String.format(template, initialValue));
        seekBar.setMax( (max - min) / step );
        seekBar.setProgress(initialValue);
    }

    public void addEventListener(@NonNull Consumer<Integer> callback) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int value = min + (progress * step);
                labelSeekBar.setText(String.format(template, value));
                callback.accept(value);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
}
