package com.stfalcon.stfalconrangebarchartexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stfalcon.pricerangebar.model.BarEntry
import com.stfalcon.stfalconrangebarchartexample.fixture.FixtureUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

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

        seekBar.setEntries(seekBarEntries)
    }

    private fun initRangeBar() {
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

        rangeBar.setEntries(rangeBarEntries)
    }
}
