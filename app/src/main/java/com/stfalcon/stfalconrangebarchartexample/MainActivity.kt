package com.stfalcon.stfalconrangebarchartexample

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.stfalcon.pricerangebar.model.SeekBarEntry
import com.stfalcon.stfalconrangebarchartexample.fixture.FixtureUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private var seekBarEntries: ArrayList<SeekBarEntry> = ArrayList()
    private var rangeBarEntries: ArrayList<SeekBarEntry> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initFixture()
        initSeekBar()
        initRangeBar()
    }

    private fun initFixture() {
        seekBarEntries = FixtureUtils.prepareSeekBarFixtures()
        rangeBarEntries = FixtureUtils.prepareAreaFixtures()
    }

    private fun initSeekBar() {
        seekBar.setEntries(seekBarEntries)
        seekBar.onPinPositionChanged = { index, pinValue ->
            seekbarAreaValue.text = "$pinValue m"
        }
        seekBar.onSelectedEntriesSizeChanged = { selectedEntriesSize ->
            println("$selectedEntriesSize")
        }
        seekBar.onSelectedItemsSizeChanged = { selectedItemsSize ->
            seekbarAreaInfo.text = "$selectedItemsSize elements was selected"
        }

        val perimetr = seekBarEntries.last().x.toInt()
        seekbarAreaValue.text = "$perimetr m"

        var totalSelectedSize = 0
        seekBarEntries.forEach { entry ->
            totalSelectedSize += entry.y.toInt()
        }

        seekbarAreaInfo.text = "$totalSelectedSize elements was selected"
    }

    private fun initRangeBar() {
        rangeBar.setEntries(rangeBarEntries)
        rangeBar.onRangeChanged = { leftPinValue, rightPinValue ->
            rangeBarValue.text = getString(R.string.area_range, leftPinValue, rightPinValue)
        }
        rangeBar.onLeftPinChanged = { index, leftPinValue ->
            println("$index $leftPinValue")
        }
        rangeBar.onRightPinChanged = { index, rightPinValue ->
            println("$index $rightPinValue")
        }
        rangeBar.onSelectedEntriesSizeChanged = { selectedEntriesSize ->
            println("$selectedEntriesSize")
        }
        rangeBar.onSelectedItemsSizeChanged = { selectedItemsSize ->
            rangeBarInfo.text = "$selectedItemsSize elements was selected"
        }

        var totalSelectedSize = 0
        rangeBarEntries.forEach { entry ->
            totalSelectedSize += entry.y.toInt()
        }
        rangeBarInfo.text = "$totalSelectedSize elements was selected"
        rangeBarValue.text = getString(R.string.area_range,
            rangeBarEntries.first().x.toInt().toString(),
            rangeBarEntries.last().x.toInt().toString())
    }
}
