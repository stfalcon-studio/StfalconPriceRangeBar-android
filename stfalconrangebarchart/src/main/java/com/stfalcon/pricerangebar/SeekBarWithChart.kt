package com.stfalcon.pricerangebar

import android.annotation.SuppressLint
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.stfalcon.pricerangebar.model.SeekBarEntry
import kotlinx.android.synthetic.main.item_seek_bar.view.*
import java.util.*

class SeekBarWithChart @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), SimpleRangeView.OnTrackRangeListener {

    private var entries: ArrayList<Entry> = ArrayList()
    private var unselectedDataSet: ArrayList<Entry> = ArrayList()
    private var selectedDataSet: ArrayList<Entry> = ArrayList()
    private var mainData: LineData? = null

    var onPinPositionChanged: ((index: Int, rightPinValue: String?) -> Unit)? = null
    var onSelectedItemsSizeChanged: ((sizeSelectedItems: Int) -> Unit)? = null
    var onSelectedEntriesSizeChanged: ((sizeSelectedEntries: Int) -> Unit)? = null

    /**
     * Background color of selected part in chart
     * */
    private var chartSelectedBackgroundColor: Int =
        ContextCompat.getColor(context, R.color.colorChartSelected)

    /**
     * Background color of not selected part in chart
     * */
    private var chartUnselectedBackgroundColor: Int =
        ContextCompat.getColor(context, R.color.colorChartUnselected)

    /**
     * Color of selected line part in chart
     * */
    private var chartSelectedLineColor: Int =
        ContextCompat.getColor(context, R.color.colorChartSelectedLine)

    /**
     * Color of not selected line part in chart
     * */
    private var chartUnSelectedLineColor: Int =
        ContextCompat.getColor(context, R.color.colorChartUnselectedLine)

    /**
     * Color of selected part of seekbar
     * */
    private var selectedSeekColor: Int = ContextCompat.getColor(context, R.color.colorRangeSelected)
        set(value) {
            field = value
            applySeekBarStyle()
        }

    /**
     * Color of not selected part of seekbar
     * */
    private var unselectedSeekColor: Int =
        ContextCompat.getColor(context, R.color.colorChartUnselectedLine)
        set(value) {
            field = value
            applySeekBarStyle()
        }

    /**
     * Color of active thumb in seekbar
     * */
    private var thumbColor: Int = ContextCompat.getColor(context, R.color.colorRangeSelected)
        set(value) {
            field = value
            applySeekBarStyle()
        }

    /**
     * Radius of active tick in seekbar
     * */
    private var tickRadius: Float =
        resources.getDimensionPixelSize(R.dimen.default_active_tick_radius).toFloat()
        set(value) {
            field = value
            applySeekBarStyle()
        }

    private var oldRightPinIndex = 0

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.item_seek_bar, this, true)

        attrs?.let {
            parseAttr(it)
        }

        initChart()
    }

    override fun onEndRangeChanged(rangeView: SimpleRangeView, rightPinIndex: Int) {
        onRangeChanged(rightPinIndex)
    }

    override fun onStartRangeChanged(rangeView: SimpleRangeView, leftPinIndex: Int) {
        // We donn`t use it
    }

    /**
     * Set the data to display
     * */
    fun setEntries(seekBarEntries: ArrayList<SeekBarEntry>) {
        this.entries.clear()
        this.entries.addAll(seekBarEntries.map { Entry(it.x, it.y) })
        initSeekBar()
    }


    /**
     * Apply style for seekbar
     * */
    private fun applySeekBarStyle() {
        elementSeekBar.apply {
            activeLineColor = selectedSeekColor
            lineColor = unselectedSeekColor
        }
        elementSeekBar?.activeThumbColor = thumbColor
        elementSeekBar?.activeTickRadius = tickRadius
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
        var centerDataSet = LineDataSet(selectedDataSet, "")
        centerDataSet = styleDataSet(dataSet = centerDataSet, isSelected = true)

        var rightDataSet = LineDataSet(unselectedDataSet, "")
        rightDataSet = styleDataSet(dataSet = rightDataSet, isSelected = false)

        mainData = LineData()

        if (selectedDataSet.isNotEmpty()) {
            mainData?.addDataSet(centerDataSet)
        }
        if (unselectedDataSet.isNotEmpty()) {
            mainData?.addDataSet(rightDataSet)
        }
        chartSeekbar.apply {
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

            invalidate()
        }
    }

    /**
     * Initialize chart
     * */
    private fun initChart() {
        chartSeekbar.apply {
            setPinchZoom(false)
            setScaleEnabled(false)
            isDoubleTapToZoomEnabled = false
        }
    }

    /**
     * Initialize seekbar
     * */
    private fun initSeekBar() {
        elementSeekBar.apply {
            onTrackRangeListener = this@SeekBarWithChart
            count = entries.size
            start = 0
            end = entries.size
            startFixed = 0
        }
        onRangeChanged(entries.size)
        drawChart()
    }

    /**
     * Draw chart and calculate all data.
     * @param pinIndex passed pin index from seekbar
     * */
    private fun onRangeChanged(pinIndex: Int) {
        selectedDataSet.clear()
        unselectedDataSet.clear()

        entries.forEachIndexed { index, item ->
            if (index in 0..pinIndex) {
                selectedDataSet.add(item)
            }
            if (index >= pinIndex) {
                unselectedDataSet.add(item)
            }
        }

        if (oldRightPinIndex != pinIndex) {
            if (pinIndex >= 0 && pinIndex < entries.size) {
                onPinPositionChanged?.invoke(
                    pinIndex,
                    selectedDataSet[pinIndex - 1].x.toInt().toString()
                )
            }
            oldRightPinIndex = pinIndex
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
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.StfalconRangeBarWithChart)

        chartSelectedBackgroundColor = typedArray.getColor(
            R.styleable.StfalconRangeBarWithChart_seekChartSelectedBackgroundColor,
            chartSelectedBackgroundColor
        )

        chartUnselectedBackgroundColor = typedArray.getColor(
            R.styleable.StfalconRangeBarWithChart_seekChartUnselectedBackgroundColor,
            chartUnselectedBackgroundColor
        )

        chartSelectedLineColor = typedArray.getColor(
            R.styleable.StfalconRangeBarWithChart_seekChartSelectedLineColor,
            chartSelectedLineColor
        )

        chartUnSelectedLineColor = typedArray.getColor(
            R.styleable.StfalconRangeBarWithChart_seekChartUnSelectedLineColor,
            chartUnSelectedLineColor
        )

        selectedSeekColor = typedArray.getColor(
            R.styleable.StfalconRangeBarWithChart_seekActiveLineColor,
            selectedSeekColor
        )

        unselectedSeekColor = typedArray.getColor(
            R.styleable.StfalconRangeBarWithChart_seekLineColor,
            unselectedSeekColor
        )

        thumbColor = typedArray.getColor(
            R.styleable.StfalconRangeBarWithChart_seekActiveThumbColor,
            thumbColor
        )

        tickRadius = typedArray.getDimension(
            R.styleable.StfalconRangeBarWithChart_seekActiveTickRadius,
            tickRadius
        )

        typedArray.recycle()
    }
}