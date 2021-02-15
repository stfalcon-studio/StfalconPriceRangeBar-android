package com.stfalcon.stfalconrangebarchartexample;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.stfalcon.pricerangebar.RangeBarWithChart;
import com.stfalcon.pricerangebar.SeekBarWithChart;
import com.stfalcon.pricerangebar.model.BarEntry;
import com.stfalcon.stfalconrangebarchartexample.fixture.FixtureUtils;

import java.util.ArrayList;

import kotlin.Unit;

public class Sample extends AppCompatActivity {

    private static final String TAG = "Sample";

    private ArrayList<BarEntry> seekBarEntries;
    private ArrayList<BarEntry> rangeBarEntries;

    private SeekBarWithChart seekBar;
    private RangeBarWithChart rangeBar;
    private TextView seekBarAreaInfo;
    private TextView seekbarAreaValue;
    private TextView rangeBarValue;
    private TextView rangeBarInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seekBar = findViewById(R.id.seekBar);
        rangeBar = findViewById(R.id.rangeBar);
        seekBarAreaInfo = findViewById(R.id.seekbarAreaInfo);
        seekbarAreaValue = findViewById(R.id.seekbarAreaValue);
        rangeBarValue = findViewById(R.id.rangeBarValue);
        rangeBarInfo = findViewById(R.id.rangeBarInfo);

        initFixture();
        initSeekBar();
        initRangeBar();
    }

    private void initFixture() {
        seekBarEntries = FixtureUtils.Companion.prepareSeekBarFixtures();
        rangeBarEntries = FixtureUtils.Companion.prepareAreaFixtures();
    }

    private void initSeekBar() {
        seekBar.setEntries(seekBarEntries);
        seekBar.setOnPinPositionChanged(this::onPinPositionChanged);
        seekBar.setOnSelectedEntriesSizeChanged(this::onSelectedEntriesSizeChanged);
        seekBar.setOnSelectedItemsSizeChanged(this::onSelectedItemsSizeChanged);

        float perimeter = seekBarEntries.get(seekBarEntries.size() - 1).getX();

        seekbarAreaValue.setText(getString(R.string.formatter_meter, Float.toString(perimeter)));

        int totalSelectedSize = 0;
        for (BarEntry entry : seekBarEntries) {
            totalSelectedSize += entry.getY();
        }

        seekBarAreaInfo.setText(getString(R.string.formatter_elements, Float.toString(totalSelectedSize)));
    }

    private void initRangeBar() {
        rangeBar.setEntries(rangeBarEntries);
        rangeBar.setOnRangeChanged(this::onRangeChanged);
        rangeBar.setOnLeftPinChanged(this::onLeftPinChanged);
        rangeBar.setOnRightPinChanged(this::onRightPinChanged);
        rangeBar.setOnSelectedEntriesSizeChanged(this::onSelectedRangeEntriesSizeChanged);
        rangeBar.setOnSelectedItemsSizeChanged(this::onRangeSelectedItemsSizeChanged);

        int totalSelectedSize = 0;
        for (BarEntry entry : rangeBarEntries) {
            totalSelectedSize += entry.getY();
        }
        rangeBarInfo.setText(getString(R.string.formatter_elements, Float.toString(totalSelectedSize)));
        rangeBarValue.setText(
                getString(
                        R.string.area_range,
                        Float.toString(rangeBarEntries.get(0).getX()),
                        Float.toString(rangeBarEntries.get(rangeBarEntries.size() - 1).getX())
                )
        );
    }

    private Unit onPinPositionChanged(Integer index, String pinValue) {
        seekbarAreaValue.setText(getString(R.string.formatter_meter, pinValue));
        Log.d(TAG, "Index value = " + index);
        return Unit.INSTANCE;
    }

    private Unit onSelectedEntriesSizeChanged(Integer selectedEntriesSize) {
        Log.d(TAG, "SelectedEntriesSize = " + selectedEntriesSize);
        return Unit.INSTANCE;
    }

    private Unit onSelectedItemsSizeChanged(Integer selectedItemsSize) {
        seekBarAreaInfo.setText(getString(R.string.formatter_elements_int, selectedItemsSize));
        return Unit.INSTANCE;
    }

    private Unit onRangeChanged(String leftPinValue, String rightPinValue) {
        rangeBarValue.setText(getString(R.string.area_range, leftPinValue, rightPinValue));
        return Unit.INSTANCE;
    }

    private Unit onLeftPinChanged(Integer index, String leftPinValue) {
        Log.d(TAG, "index = " + index + " $leftPinValue = " + leftPinValue);
        return Unit.INSTANCE;
    }

    private Unit onRightPinChanged(Integer index, String rightPinValue) {
        Log.d(TAG, "index = " + index + " rightPinValue = " + rightPinValue);
        return Unit.INSTANCE;
    }

    private Unit onSelectedRangeEntriesSizeChanged(Integer entriesSize) {
        Log.d(TAG, "Range EntriesSize = " + entriesSize);
        return Unit.INSTANCE;
    }

    private Unit onRangeSelectedItemsSizeChanged(Integer selectedItemsSize) {
        rangeBarInfo.setText(getString(R.string.formatter_elements, selectedItemsSize.toString()));
        return Unit.INSTANCE;
    }
}
