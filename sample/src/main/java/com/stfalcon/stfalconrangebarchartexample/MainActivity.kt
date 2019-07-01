package com.stfalcon.stfalconrangebarchartexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stfalcon.pricerangebar.model.BarEntry
import com.stfalcon.stfalconrangebarchartexample.fixture.FixtureUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    private var seekBarEntries = ArrayList<BarEntry>()
    private var rangeBarEntries = ArrayList<BarEntry>()

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
            seekbarAreaValue.text = getString(R.string.formatter_meter, pinValue)
            println("$index")
        }
        seekBar.onSelectedEntriesSizeChanged = { selectedEntriesSize ->
            println("$selectedEntriesSize")
        }
        seekBar.onSelectedItemsSizeChanged = { selectedItemsSize ->
            seekbarAreaInfo.text =
                getString(R.string.formatter_elements, selectedItemsSize.toString())
        }

        val perimetr = seekBarEntries.last().x
        seekbarAreaValue.text = getString(R.string.formatter_meter, perimetr.toString())

        var totalSelectedSize = 0
        seekBarEntries.forEach { entry ->
            totalSelectedSize += entry.y.toInt()
        }

        seekbarAreaInfo.text = getString(R.string.formatter_elements, totalSelectedSize.toString())
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
            rangeBarInfo.text = getString(R.string.formatter_elements, selectedItemsSize.toString())
        }

        var totalSelectedSize = 0
        rangeBarEntries.forEach { entry ->
            totalSelectedSize += entry.y.toInt()
        }
        rangeBarInfo.text = getString(R.string.formatter_elements, totalSelectedSize.toString())
        rangeBarValue.text = getString(
            R.string.area_range,
            rangeBarEntries.first().x.toInt().toString(),
            rangeBarEntries.last().x.toInt().toString()
        )
    }
}
