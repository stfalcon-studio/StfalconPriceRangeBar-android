/*******************************************************************************
 * Copyright 2018 stfalcon.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.stfalcon.pricerangebar

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.stfalcon.pricerangebar.extension.safeLet
import com.stfalcon.pricerangebar.model.BarEntry
import kotlinx.android.synthetic.main.item_range_bar.view.*
import java.util.*

open class RangeBarWithChart @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr),
    SimpleRangeView.OnTrackRangeListener {

    var onLeftPinChanged: ((index: Int, leftPinValue: String?) -> Unit)? = null
    var onRangeChanged: ((leftPinValue: String?, rightPinValue: String?) -> Unit)? = null
    var onRightPinChanged: ((index: Int, rightPinValue: String?) -> Unit)? = null
    var onSelectedItemsSizeChanged: ((sizeItemsSelected: Int) -> Unit)? = null
    var onSelectedEntriesSizeChanged: ((entriesSize: Int) -> Unit)? = null

    /**
     * Background color of selected part in chart
     * */
    var chartSelectedBackgroundColor: Int =
        ContextCompat.getColor(context, R.color.colorChartSelected)

    /**
     * Background color of not selected part in chart
     * */
    var chartUnselectedBackgroundColor: Int =
        ContextCompat.getColor(context, R.color.colorChartUnselected)

    /**
     * Color of selected line part in chart
     * */
    var chartSelectedLineColor: Int =
        ContextCompat.getColor(context, R.color.colorChartSelectedLine)

    /**
     * Color of not selected line part in chart
     * */
    var chartUnSelectedLineColor: Int =
        ContextCompat.getColor(context, R.color.colorChartUnselectedLine)

    /**
     * Color of selected part of rangebar
     * */
    var selectedSeekColor: Int = ContextCompat.getColor(context, R.color.colorRangeSelected)
        set(value) {
            field = value
            applyRangeBarStyle()
        }

    /**
     * Color of not selected part of rangebar
     * */
    var unselectedSeekColor: Int =
        ContextCompat.getColor(context, R.color.colorChartUnselectedLine)
        set(value) {
            field = value
            applyRangeBarStyle()
        }

    /**
     * Color of active thumb in rangebar
     * */
    var thumbColor: Int = ContextCompat.getColor(context, R.color.colorRangeSelected)
        set(value) {
            field = value
            applyRangeBarStyle()
        }

    /**
     * Color of active thumb in rangebar
     * */
    var thumbActiveColor: Int = ContextCompat.getColor(context, R.color.colorRangeSelected)
        set(value) {
            field = value
            applyRangeBarStyle()
        }

    /**
     * Radius of active tick in rangebar
     * */
    var tickRadius: Float =
        resources.getDimensionPixelSize(R.dimen.default_active_tick_radius).toFloat()
        set(value) {
            field = value
            applyRangeBarStyle()
        }

    private var entries: ArrayList<Entry> = ArrayList()
    private var leftUnselectedDataSet: ArrayList<Entry> = ArrayList()
    private var rightUnselectedDataSet: ArrayList<Entry> = ArrayList()
    private var selectedDataSet: ArrayList<Entry> = ArrayList()
    private var mainData: LineData? = null

    private var oldLeftPinIndex = 0
    private var oldRightPinIndex = 0

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.item_range_bar, this, true)

        attrs?.let {
            parseAttr(it)
        }

        initChart()
    }

    override fun onEndRangeChanged(rangeView: SimpleRangeView, rightPinIndex: Int) {
        onRangeChange(oldLeftPinIndex, rightPinIndex)
    }

    override fun onStartRangeChanged(rangeView: SimpleRangeView, leftPinIndex: Int) {
        onRangeChange(leftPinIndex, oldRightPinIndex)
    }

    /**
     * Set selected values
     * */
    fun setSelectedEntries(selectedBarEntries: ArrayList<BarEntry>) {
        val leftEntry = selectedBarEntries.firstOrNull()
        val rightEntry = selectedBarEntries.lastOrNull()

        val leftValue = entries.indexOfFirst { entry -> entry.x == leftEntry?.x }
        val rightValue = entries.indexOfLast { entry -> entry.x == rightEntry?.x }

        safeLet(leftValue, rightValue) { left, right ->
            elementRangeBar?.start = left
            elementRangeBar?.end = right

            onRangeChange(left, right)
        }
    }

    /**
     * Set selected values
     * */
    fun setSelectedEntries(startSelectedValue: Int, endSelectedValue: Int) {
        if (startSelectedValue <= 0 || endSelectedValue <= 0) {
            Log.e(this.javaClass.canonicalName,"You can't set values less than 0 or 0.")
            return
        }
        if (startSelectedValue > endSelectedValue) {
            Log.e(this.javaClass.canonicalName,"You can't set startSelectedValue greater than endSelectedValue.")
            return
        }

        elementRangeBar?.start = startSelectedValue
        elementRangeBar?.end = endSelectedValue
        onRangeChange(startSelectedValue, endSelectedValue)
    }

    /**
     * Set the data to display
     * */
    fun setEntries(entries: ArrayList<BarEntry>) {
        this.entries.clear()
        this.entries.addAll(entries.map { Entry(it.x, it.y) })
        initRangeBar()
    }

    /**
     * Apply style for rangebar
     * */
    private fun applyRangeBarStyle() {
        elementRangeBar.apply {
            activeLineColor = selectedSeekColor
            lineColor = unselectedSeekColor
        }
        elementRangeBar.activeThumbColor = thumbColor
        elementRangeBar.activeFocusThumbColor = thumbActiveColor
        elementRangeBar.activeTickRadius = tickRadius
    }

    /**
     * Calculate all selected items
     * */
    private fun calculateSelectedItemsSize() {
        var totalSelectedSize = 0
        selectedDataSet.forEach { entry ->
            totalSelectedSize += entry.y.toInt()
        }
        onSelectedItemsSizeChanged?.invoke(totalSelectedSize)
    }

    /**
     * Calculate selected entries
     * */
    private fun calculateSelectedEntriesSize() {
        onSelectedEntriesSizeChanged?.invoke(selectedDataSet.size)
    }

    /**
     * Prepare data and draw chart
     * */
    private fun drawChart() {
        var leftDataSet = LineDataSet(leftUnselectedDataSet, "")
        leftDataSet = styleDataSet(dataSet = leftDataSet, isSelected = false)

        var centerDataSet = LineDataSet(selectedDataSet, "")
        centerDataSet = styleDataSet(dataSet = centerDataSet, isSelected = true)

        var rightDataSet = LineDataSet(rightUnselectedDataSet, "")
        rightDataSet = styleDataSet(dataSet = rightDataSet, isSelected = false)

        mainData = LineData()
        if (leftUnselectedDataSet.isNotEmpty()) {
            mainData?.addDataSet(leftDataSet)
        }
        if (selectedDataSet.isNotEmpty()) {
            mainData?.addDataSet(centerDataSet)
        }
        if (rightUnselectedDataSet.isNotEmpty()) {
            mainData?.addDataSet(rightDataSet)
        }
        chart.apply {
            data = mainData
            legend.isEnabled = false
            description.isEnabled = false
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.isGranularityEnabled = false
            xAxis.labelCount = 0
            xAxis.isEnabled = false
            axisLeft.axisMinimum = 0f
            axisRight.isEnabled = false
            axisLeft.isEnabled = false
            isClickable = false
            data.isHighlightEnabled = false
            setDrawMarkers(false)
            setDrawGridBackground(false)
        }

        chart.invalidate()
    }

    /**
     * Initialize chart
     * */
    private fun initChart() {
        chart.apply {
            setPinchZoom(false)
            setScaleEnabled(false)
            isDoubleTapToZoomEnabled = false
        }
    }

    /**
     * Initialize rangebar
     * */
    private fun initRangeBar() {
        elementRangeBar.apply {
            onTrackRangeListener = this@RangeBarWithChart
            count = entries.size
            start = 0
            end = entries.size
        }

        onRangeChange(0, entries.size - 1)

        drawChart()
    }

    /**
     * Draw chart and calculate all data.
     * @param leftPinIndex passed left pin index from seekbar
     * @param rightPinIndex passed right pin index from seekbar
     * */
    private fun onRangeChange(leftPinIndex: Int, rightPinIndex: Int) {
        leftUnselectedDataSet.clear()
        selectedDataSet.clear()
        rightUnselectedDataSet.clear()

        entries.forEachIndexed { index, item ->
            if (index <= leftPinIndex) {
                leftUnselectedDataSet.add(item)
            }

            if (index in leftPinIndex..rightPinIndex) {
                selectedDataSet.add(item)
            }

            if (index >= rightPinIndex) {
                rightUnselectedDataSet.add(item)
            }
        }

        if ((leftPinIndex >= 0 && leftPinIndex < entries.size)
            && (rightPinIndex >= 0 && rightPinIndex < entries.size)
        ) {
            val leftVal = entries[leftPinIndex].x.toInt().toString()
            val rightVal = entries[rightPinIndex].x.toInt().toString()
            onRangeChanged?.invoke(leftVal, rightVal)
        }

        if (oldLeftPinIndex != leftPinIndex) {
            if (leftPinIndex >= 0 && leftPinIndex < entries.size) {
                onLeftPinChanged?.invoke(leftPinIndex, entries[leftPinIndex].x.toString())
            }
            oldLeftPinIndex = leftPinIndex
        }
        if (oldRightPinIndex != rightPinIndex) {
            if (rightPinIndex >= 0 && rightPinIndex < entries.size) {
                onRightPinChanged?.invoke(
                    rightPinIndex,
                    entries[rightPinIndex].x.toString()
                )
            }
            oldRightPinIndex = rightPinIndex
        }

        calculateSelectedItemsSize()
        calculateSelectedEntriesSize()

        drawChart()
    }

    /**
     * Styling data for chart
     * @param dataSet passed prepared data for chart
     * @param isSelected indicate selected part of chart
     * */
    private fun styleDataSet(dataSet: LineDataSet, isSelected: Boolean = false): LineDataSet {
        if (!isSelected) {
            dataSet.apply {
                fillColor = chartUnselectedBackgroundColor
                color = chartUnSelectedLineColor
            }
        } else {
            dataSet.apply {
                fillColor = chartSelectedBackgroundColor
                color = chartSelectedLineColor
            }
        }
        dataSet.apply {
            setDrawCircles(false)
            setDrawValues(false)
            setDrawFilled(true)
        }

        return dataSet
    }

    /**
     * Parse attributes from xml.
     * @param attrs passed attributes from XML file
     * */
    @SuppressLint("CustomViewStyleable")
    private fun parseAttr(attrs: AttributeSet) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.PriceRangeBar)

        chartSelectedBackgroundColor = typedArray.getColor(
            R.styleable.PriceRangeBar_barChartSelectedBackgroundColor,
            chartSelectedBackgroundColor
        )

        chartUnselectedBackgroundColor = typedArray.getColor(
            R.styleable.PriceRangeBar_barChartUnselectedBackgroundColor,
            chartUnselectedBackgroundColor
        )

        chartSelectedLineColor = typedArray.getColor(
            R.styleable.PriceRangeBar_barChartSelectedLineColor,
            chartSelectedLineColor
        )

        chartUnSelectedLineColor = typedArray.getColor(
            R.styleable.PriceRangeBar_barChartUnSelectedLineColor,
            chartUnSelectedLineColor
        )

        selectedSeekColor = typedArray.getColor(
            R.styleable.PriceRangeBar_barActiveLineColor,
            selectedSeekColor
        )

        unselectedSeekColor = typedArray.getColor(
            R.styleable.PriceRangeBar_barLineColor,
            unselectedSeekColor
        )

        thumbColor = typedArray.getColor(
            R.styleable.PriceRangeBar_barThumbColor,
            thumbColor
        )

        thumbActiveColor = typedArray.getColor(
            R.styleable.PriceRangeBar_barActiveThumbColor,
            thumbActiveColor
        )

        tickRadius = typedArray.getDimension(
            R.styleable.PriceRangeBar_barActiveTickRadius,
            tickRadius
        )

        typedArray.recycle()
    }
}
