/*
 * Copyright (C) 2016 Vitaliy Bendik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stfalcon.pricerangebar

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.ViewCompat
import com.stfalcon.pricerangebar.extension.convertable
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty

open class SimpleRangeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var labelColor by updatePaintsAndRedraw(DEFAULT_LABEL_COLOR)
    var activeLabelColor by updatePaintsAndRedraw(DEFAULT_ACTIVE_LABEL_COLOR)
    var activeThumbLabelColor by updatePaintsAndRedraw(DEFAULT_ACTIVE_THUMB_LABEL_COLOR)
    var fixedLabelColor by updatePaintsAndRedraw(DEFAULT_FIXED_LABEL_COLOR)
    var fixedThumbLabelColor by updatePaintsAndRedraw(DEFAULT_FIXED_THUMB_LABEL_COLOR)
    var lineColor by updatePaintsAndRedraw(DEFAULT_LINE_COLOR)
    var activeLineColor by updatePaintsAndRedraw(DEFAULT_ACTIVE_LINE_COLOR)
    var fixedLineColor by updatePaintsAndRedraw(DEFAULT_FIXED_LINE_COLOR)
    var tickColor by updatePaintsAndRedraw(DEFAULT_TICK_COLOR)
    var activeTickColor by updatePaintsAndRedraw(DEFAULT_ACTIVE_TICK_COLOR)
    var fixedTickColor by updatePaintsAndRedraw(DEFAULT_FIXED_TICK_COLOR)
    var activeThumbColor by updatePaintsAndRedraw(DEFAULT_ACTIVE_THUMB_COLOR)
    var activeFocusThumbColor by updatePaintsAndRedraw(DEFAULT_ACTIVE_FOCUS_THUMB_COLOR)
    var fixedThumbColor by updatePaintsAndRedraw(DEFAULT_FIXED_THUMB_COLOR)
    var activeFocusThumbAlpha by updatePaintsAndRedraw(DEFAULT_ACTIVE_FOCUS_THUMB_ALPHA)

    var lineThickness by updateView(0f)
    var activeLineThickness by updateView(0f)
    var fixedLineThickness by updateView(0f)
    var tickRadius by updateView(0f)
    var activeThumbFocusRadius by updateView(0f)
    var activeThumbRadius by updateView(0f)
    var activeTickRadius by updateView(0f)
    var fixedThumbRadius by updateView(0f)
    var fixedTickRadius by updateView(0f)
    var labelFontSize by updateView(0f)
    var labelMarginBottom by updateView(0f)
    var minDistanceBetweenLabels by updateView(0f)

    var innerRangePaddingLeft by updateView(0f)
    var innerRangePaddingRight by updateView(0f)
    var innerRangePadding = 0f
        set(value) {
            field = value
            innerRangePaddingLeft = value
            innerRangePaddingRight = value
        }

    var count by redraw(DEFAULT_COUNT)
    var start by redraw(DEFAULT_START) { closestValidPosition(it) }
    var end by redraw(DEFAULT_END) { closestValidPosition(it) }
    var minDistance by redraw(DEFAULT_MINIMAL_DISTANCE)
    var maxDistance by redraw(DEFAULT_MAXIMAL_DISTANCE)

    var startFixed by redraw(DEFAULT_START_FIXED)
    var endFixed by redraw(DEFAULT_END_FIXED)

    var movable = DEFAULT_MOVABLE
    var showFixedLine by redraw(DEFAULT_SHOW_FIXED_LINE)
    var showTicks by redraw(DEFAULT_SHOW_TICKS)
    var showActiveTicks by redraw(DEFAULT_SHOW_ACTIVE_TICKS)
    var showFixedTicks by redraw(DEFAULT_SHOW_FIXED_TICKS)
    var showLabels by redraw(DEFAULT_SHOW_LABELS)
    var isRange by redraw(DEFAULT_IS_RANGE)

    // Callbacks

    var onChangeRangeListener: OnChangeRangeListener? = null
    var onTrackRangeListener: OnTrackRangeListener? = null
    var onRangeLabelsListener: OnRangeLabelsListener? = null


    // Internal

    private lateinit var paint: Paint
    private lateinit var paintFixed: Paint
    private lateinit var paintActive: Paint
    private lateinit var paintTick: Paint
    private lateinit var paintFixedTick: Paint
    private lateinit var paintActiveTick: Paint
    private lateinit var paintActiveThumb: Paint
    private lateinit var paintFixedThumb: Paint
    private lateinit var paintActiveFocusThumb: Paint
    private lateinit var paintText: Paint
    private lateinit var paintActiveText: Paint
    private lateinit var paintFixedText: Paint
    private lateinit var paintActiveThumbText: Paint
    private lateinit var paintFixedThumbText: Paint

    private var currentLeftFocusRadiusPx = ValueWrapper(0f)
    private var currentRightFocusRadiusPx = ValueWrapper(0f)

    private var posY = 0f
    private var lineY = 0f
    private var lineYActive = 0f
    private var lineYFixed = 0f
    private var stepPx = 0f

    private var isEndPressed: Boolean = false
    private var isStartPressed: Boolean = false
    private var isRangeMoving: Boolean = false

    private var linePosToStart = 0

    init {
        applyDefaultValues()

        if (attrs != null) {
            //get attributes passed in XML
            val styledAttrs = context.obtainStyledAttributes(
                attrs,
                R.styleable.SimpleRangeView, 0, 0
            )
            labelColor = styledAttrs.getColor(R.styleable.SimpleRangeView_labelColor, labelColor)
            activeLabelColor = styledAttrs.getColor(
                R.styleable.SimpleRangeView_activeLabelColor,
                activeLabelColor
            )
            activeThumbLabelColor = styledAttrs.getColor(
                R.styleable.SimpleRangeView_activeThumbLabelColor,
                activeThumbLabelColor
            )
            fixedLabelColor = styledAttrs.getColor(
                R.styleable.SimpleRangeView_fixedLabelColor,
                fixedLabelColor
            )
            fixedThumbLabelColor = styledAttrs.getColor(
                R.styleable.SimpleRangeView_fixedThumbLabelColor,
                fixedThumbLabelColor
            )

            lineColor = styledAttrs.getColor(R.styleable.SimpleRangeView_lineColor, lineColor)
            activeLineColor = styledAttrs.getColor(
                R.styleable.SimpleRangeView_activeLineColor,
                activeLineColor
            )
            fixedLineColor =
                    styledAttrs.getColor(R.styleable.SimpleRangeView_fixedLineColor, fixedLineColor)
            tickColor = styledAttrs.getColor(R.styleable.SimpleRangeView_tickColor, tickColor)
            activeTickColor = styledAttrs.getColor(
                R.styleable.SimpleRangeView_activeTickColor,
                activeTickColor
            )
            fixedTickColor =
                    styledAttrs.getColor(R.styleable.SimpleRangeView_fixedTickColor, fixedTickColor)
            activeThumbColor = styledAttrs.getColor(
                R.styleable.SimpleRangeView_activeThumbColor,
                activeThumbColor
            )
            activeFocusThumbColor = styledAttrs.getColor(
                R.styleable.SimpleRangeView_activeFocusThumbColor,
                activeThumbColor
            )
            fixedThumbColor = styledAttrs.getColor(
                R.styleable.SimpleRangeView_fixedThumbColor,
                fixedThumbColor
            )

            activeFocusThumbAlpha = styledAttrs.getFloat(
                R.styleable.SimpleRangeView_activeFocusThumbAlpha,
                activeFocusThumbAlpha
            )

            lineThickness = styledAttrs.getDimension(
                R.styleable.SimpleRangeView_lineThickness,
                lineThickness
            )
            activeLineThickness = styledAttrs.getDimension(
                R.styleable.SimpleRangeView_activeLineThickness,
                activeLineThickness
            )
            fixedLineThickness = styledAttrs.getDimension(
                R.styleable.SimpleRangeView_fixedLineThickness,
                fixedLineThickness
            )
            activeThumbRadius = styledAttrs.getDimension(
                R.styleable.SimpleRangeView_activeThumbRadius,
                activeThumbRadius
            )
            activeThumbFocusRadius = styledAttrs.getDimension(
                R.styleable.SimpleRangeView_activeThumbFocusRadius,
                activeThumbFocusRadius
            )
            fixedThumbRadius = styledAttrs.getDimension(
                R.styleable.SimpleRangeView_fixedThumbRadius,
                fixedThumbRadius
            )
            tickRadius =
                    styledAttrs.getDimension(R.styleable.SimpleRangeView_tickRadius, tickRadius)
            activeTickRadius = styledAttrs.getDimension(
                R.styleable.SimpleRangeView_activeTickRadius,
                activeTickRadius
            )
            fixedTickRadius = styledAttrs.getDimension(
                R.styleable.SimpleRangeView_fixedTickRadius,
                fixedTickRadius
            )
            labelMarginBottom = styledAttrs.getDimension(
                R.styleable.SimpleRangeView_labelMarginBottom,
                labelMarginBottom
            )
            labelFontSize = styledAttrs.getDimension(
                R.styleable.SimpleRangeView_labelFontSize,
                labelFontSize
            )
            minDistanceBetweenLabels = styledAttrs.getDimension(
                R.styleable.SimpleRangeView_minDistanceBetweenLabels,
                minDistanceBetweenLabels
            )
            innerRangePadding = Math.max(
                calcMaxRadius(),
                styledAttrs.getDimension(
                    R.styleable.SimpleRangeView_innerRangePadding,
                    innerRangePadding
                )
            )
            innerRangePaddingLeft = Math.max(
                calcMaxRadius(),
                styledAttrs.getDimension(
                    R.styleable.SimpleRangeView_innerRangePaddingLeft,
                    innerRangePadding
                )
            )
            innerRangePaddingRight = Math.max(
                calcMaxRadius(),
                styledAttrs.getDimension(
                    R.styleable.SimpleRangeView_innerRangePaddingRight,
                    innerRangePadding
                )
            )

            count = styledAttrs.getInt(R.styleable.SimpleRangeView_count, count)
            startFixed = styledAttrs.getInt(R.styleable.SimpleRangeView_startFixed, startFixed)
            endFixed = styledAttrs.getInt(R.styleable.SimpleRangeView_endFixed, endFixed)
            start = styledAttrs.getInt(R.styleable.SimpleRangeView_start, start)
            end = styledAttrs.getInt(R.styleable.SimpleRangeView_end, end)
            minDistance = styledAttrs.getInt(R.styleable.SimpleRangeView_minDistance, minDistance)
            maxDistance = styledAttrs.getInt(R.styleable.SimpleRangeView_maxDistance, maxDistance)

            movable = styledAttrs.getBoolean(R.styleable.SimpleRangeView_movable, movable)
            showFixedLine =
                    styledAttrs.getBoolean(R.styleable.SimpleRangeView_showFixedLine, showFixedLine)
            showTicks = styledAttrs.getBoolean(R.styleable.SimpleRangeView_showTicks, showTicks)
            showActiveTicks = styledAttrs.getBoolean(
                R.styleable.SimpleRangeView_showActiveTicks,
                showActiveTicks
            )
            showFixedTicks = styledAttrs.getBoolean(
                R.styleable.SimpleRangeView_showFixedTicks,
                showFixedTicks
            )
            showLabels = styledAttrs.getBoolean(R.styleable.SimpleRangeView_showLabels, showLabels)
            isRange = styledAttrs.getBoolean(R.styleable.SimpleRangeView_isRange, isRange)

            styledAttrs.recycle()
        }

        initPaints()
    }

    private fun applyDefaultValues() {
        val scale = context.resources.displayMetrics.density

        lineThickness = DEFAULT_LINE_THICKNESS * scale
        activeLineThickness = DEFAULT_ACTIVE_LINE_THICKNESS * scale
        fixedLineThickness = DEFAULT_FIXED_LINE_THICKNESS * scale
        activeThumbRadius = DEFAULT_ACTIVE_THUMB_RADIUS * scale
        activeThumbFocusRadius = DEFAULT_ACTIVE_THUMB_FOCUS_RADIUS * scale
        fixedThumbRadius = DEFAULT_FIXED_THUMB_RADIUS * scale
        tickRadius = DEFAULT_TICK_RADIUS * scale
        activeTickRadius = DEFAULT_ACTIVE_TICK_RADIUS * scale
        fixedTickRadius = DEFAULT_FIXED_TICK_RADIUS * scale
        labelMarginBottom = DEFAULT_LABEL_MARGIN_BOTTOM * scale
        labelFontSize = DEFAULT_LABEL_FONT_SIZE * scale
        minDistanceBetweenLabels = DEFAULT_MINIMAL_DISTANCE_BETWEEN_LABELS * scale
        innerRangePadding = DEFAULT_INNER_RANGE_PADDING * scale
        innerRangePaddingLeft = DEFAULT_INNER_RANGE_PADDING_LEFT * scale
        innerRangePaddingRight = DEFAULT_INNER_RANGE_PADDING_RIGHT * scale
    }

    private fun defaultValIfZero(value: Float, defValue: Float) =
        if (value == 0f) defValue else value

    private fun initPaints() {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.FILL
        paint.color = lineColor

        paintFixed = Paint(Paint.ANTI_ALIAS_FLAG)
        paintFixed.style = Paint.Style.FILL
        paintFixed.color = fixedLineColor

        paintFixedTick = Paint(Paint.ANTI_ALIAS_FLAG)
        paintFixedTick.style = Paint.Style.FILL
        paintFixedTick.color = fixedTickColor

        paintActive = Paint(Paint.ANTI_ALIAS_FLAG)
        paintActive.style = Paint.Style.FILL
        paintActive.color = activeLineColor

        paintTick = Paint(Paint.ANTI_ALIAS_FLAG)
        paintTick.style = Paint.Style.FILL
        paintTick.color = tickColor

        paintActiveTick = Paint(Paint.ANTI_ALIAS_FLAG)
        paintActiveTick.style = Paint.Style.FILL
        paintActiveTick.color = activeTickColor

        paintActiveThumb = Paint(Paint.ANTI_ALIAS_FLAG)
        paintActiveThumb.style = Paint.Style.FILL
        paintActiveThumb.color = activeThumbColor

        paintFixedThumb = Paint(Paint.ANTI_ALIAS_FLAG)
        paintFixedThumb.style = Paint.Style.FILL
        paintFixedThumb.color = fixedThumbColor

        paintActiveFocusThumb = Paint(Paint.ANTI_ALIAS_FLAG)
        paintActiveFocusThumb.style = Paint.Style.FILL
        paintActiveFocusThumb.color = activeFocusThumbColor
        paintActiveFocusThumb.alpha = (activeFocusThumbAlpha * 255).toInt()

        paintText = Paint(Paint.ANTI_ALIAS_FLAG)
        paintText.style = Paint.Style.FILL
        paintText.color = labelColor
        paintText.textSize = labelFontSize
        paintText.textAlign = Paint.Align.CENTER
        paintText.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)

        paintActiveText = Paint(Paint.ANTI_ALIAS_FLAG)
        paintActiveText.style = Paint.Style.FILL
        paintActiveText.color = activeLabelColor
        paintActiveText.textSize = labelFontSize
        paintActiveText.textAlign = Paint.Align.CENTER
        paintActiveText.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)

        paintFixedText = Paint(Paint.ANTI_ALIAS_FLAG)
        paintFixedText.style = Paint.Style.FILL
        paintFixedText.color = fixedLabelColor
        paintFixedText.textSize = labelFontSize
        paintFixedText.textAlign = Paint.Align.CENTER
        paintFixedText.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)

        paintActiveThumbText = Paint(Paint.ANTI_ALIAS_FLAG)
        paintActiveThumbText.style = Paint.Style.FILL
        paintActiveThumbText.color = activeThumbLabelColor
        paintActiveThumbText.textSize = labelFontSize
        paintActiveThumbText.textAlign = Paint.Align.CENTER
        paintActiveThumbText.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)

        paintFixedThumbText = Paint(Paint.ANTI_ALIAS_FLAG)
        paintFixedThumbText.style = Paint.Style.FILL
        paintFixedThumbText.color = fixedThumbLabelColor
        paintFixedThumbText.textSize = labelFontSize
        paintFixedThumbText.textAlign = Paint.Align.CENTER
        paintFixedThumbText.typeface = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.apply {

            // Draw range lines
            drawLines(canvas)

            // Draw ticks
            drawTicks(canvas)

            // Draw fixed range thumbs
            drawFixedThumbs(canvas)

            // Draw active range thumbs
            drawActiveThumbs(canvas)

            // Draw labels
            drawLabels(canvas)
        }
    }

    /**
     * Draw range lines
     */
    protected fun drawLines(canvas: Canvas) {
        val xActiveStart = getPositionX(start)
        val xActiveEnd = getPositionX(end) - xActiveStart
        val xFixedStart = getPositionX(startFixed)
        val xFixedEnd = getPositionX(endFixed) - xFixedStart

        drawLine(
            canvas,
            innerRangePaddingLeft,
            lineY,
            width.toFloat() - (innerRangePaddingRight + innerRangePaddingLeft),
            lineThickness,
            paint
        )
        drawFixedLine(canvas, xFixedStart, lineYFixed, xFixedEnd, fixedLineThickness)
        drawActiveLine(canvas, xActiveStart, lineYActive, xActiveEnd, activeLineThickness)
    }

    /**
     * Draw range ticks
     */
    private fun drawTicks(canvas: Canvas) {
        if (showTicks) {
            val left = if (showFixedLine) startFixed else start
            val right = if (showFixedLine) endFixed else Math.min(end + 1, count)

            for (i in 0 until left) {
                val x = getPositionX(i)
                canvas.drawCircle(x, getPositionY(), tickRadius, paintTick)
            }

            for (i in right until count) {
                val x = getPositionX(i)
                canvas.drawCircle(x, getPositionY(), tickRadius, paintTick)
            }
        }

        if (showFixedLine && showFixedTicks) {
            for (i in startFixed until start) {
                val x = getPositionX(i)
                canvas.drawCircle(x, getPositionY(), fixedTickRadius, paintFixedTick)
            }

            for (i in end until endFixed) {
                val x = getPositionX(i)
                canvas.drawCircle(x, getPositionY(), fixedTickRadius, paintFixedTick)
            }
        }

        if (showActiveTicks) {
            for (i in start + 1 until end) {
                val x = getPositionX(i)
                canvas.drawCircle(x, getPositionY(), tickRadius, paintActiveTick)
            }
        }
    }

    /**
     * Draw labels
     */
    protected fun drawLabels(canvas: Canvas) {
        if (showLabels) {
            val left = if (showFixedLine) startFixed else start
            val right = if (showFixedLine) endFixed else Math.min(end + 1, count)

            for (i in 0..count) {
                val x = getPositionX(i)
                when (i) {
                    start, end -> drawLabel(canvas, x, i, State.ACTIVE_THUMB, paintActiveThumbText)
                    in start + 1 until end -> drawLabel(canvas, x, i, State.ACTIVE, paintActiveText)
                    in 0 until left, in right until count -> drawLabel(
                        canvas,
                        x,
                        i,
                        State.NORMAL,
                        paintText
                    )
                    startFixed, endFixed -> drawLabel(
                        canvas,
                        x,
                        i,
                        State.FIXED_THUMB,
                        paintFixedThumbText
                    )
                    in startFixed until start, in end until endFixed -> drawLabel(
                        canvas,
                        x,
                        i,
                        State.FIXED,
                        paintFixedText
                    )
                }
            }
        }
    }

    /**
     * Draw fixed thumbs
     */
    protected fun drawFixedThumbs(canvas: Canvas) {
        if (showFixedLine) {
            for (i in listOf(startFixed, endFixed)) {
                val x = getPositionX(i)
                canvas.drawCircle(x, getPositionY(), fixedThumbRadius, paintFixedThumb)
                if (showFixedTicks) {
                    canvas.drawCircle(x, getPositionY(), fixedTickRadius, paintFixedTick)
                }
            }
        }
    }

    /**
     * Draw active thumbs
     */
    protected fun drawActiveThumbs(canvas: Canvas) {
        if (isRange) {
            drawActiveThumb(canvas, start, currentLeftFocusRadiusPx)
        }
        drawActiveThumb(canvas, end, currentRightFocusRadiusPx)
    }

    protected fun drawLine(
        canvas: Canvas,
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        paint: Paint
    ) {
        canvas.drawRect(x, y, x + w, y + h, paint)
    }

    protected fun drawFixedLine(canvas: Canvas, x: Float, y: Float, w: Float, h: Float) {
        if (showFixedLine) {
            drawLine(canvas, x, y, w, h, paintFixed)
        }
    }

    protected fun drawActiveLine(canvas: Canvas, x: Float, y: Float, w: Float, h: Float) {
        drawLine(canvas, x, y, w, h, paintActive)
    }

    protected fun drawActiveThumb(canvas: Canvas, i: Int, size: ValueWrapper<Float>) {
        val x = getPositionX(i)

        // Draw focus
        if (size.value > 0f) {
            canvas.drawCircle(x, getPositionY(), size.value, paintActiveFocusThumb)
        }

        canvas.drawCircle(x, getPositionY(), activeThumbRadius, paintActiveThumb)
        if (showActiveTicks) {
            canvas.drawCircle(x, getPositionY(), activeTickRadius, paintActiveTick)
        }
    }

    protected open fun drawLabel(canvas: Canvas, x: Float, pos: Int, state: State, paint: Paint) {
        if (showLabels) {
            val text = onRangeLabelsListener?.getLabelTextForPosition(this, pos, state)
            if (text != null) {
                canvas.drawText(text, x, getPositionY() - labelMarginBottom, paint)
            }
        }
    }

    private fun getPositionX(i: Int): Float = (innerRangePaddingLeft + stepPx * i)
    private fun getPositionY() = posY

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (!isEnabled) return false

        parent.requestDisallowInterceptTouchEvent(true)

        when (event.action) {
            MotionEvent.ACTION_MOVE, MotionEvent.ACTION_DOWN -> {

                val x = event.x
                val y = event.y

                if (isRangeMoving && isRange) {
                    val size = end - start
                    var _start = closestValidPosition(getPositionByXCoord(x) - linePosToStart)
                    val _end = closestValidPosition(_start + size)
                    _start = _end - size

                    if (validatePosition(_start) && validatePosition(_end)) {
                        start = _start
                        end = _end
                        invalidate()
                        onTrackRangeListener?.onStartRangeChanged(this, start)
                        onTrackRangeListener?.onEndRangeChanged(this, end)
                    }
                    return true
                }

                if (isStartPressed && isRange) {
                    val _start = closestValidPosition(getPositionByXCoord(x))
                    if (validatePositionForStart(_start)) {
                        if (start != _start) {
                            start = _start
                            invalidate()
                            onTrackRangeListener?.onStartRangeChanged(this, start)
                        }
                    }
                    return true
                }

                if (isEndPressed) {
                    val _end = closestValidPosition(getPositionByXCoord(x))
                    if (validatePositionForEnd(_end)) {
                        if (end != _end) {
                            end = _end
                            invalidate()
                            onTrackRangeListener?.onEndRangeChanged(this, end)
                        }
                    }
                    return true
                }

                if (isInTargetZone(start, x, y)) {
                    isStartPressed = true
                    fadeIn(currentLeftFocusRadiusPx, activeThumbFocusRadius)
                    return true
                }

                if (isInTargetZone(end, x, y)) {
                    isEndPressed = true
                    fadeIn(currentRightFocusRadiusPx, activeThumbFocusRadius)
                    return true
                }

                if (!isStartPressed && !isEndPressed) {
                    // Check for moving range
                    if ((getPositionByXCoord(x) in start until end) && movable) {
                        isRangeMoving = true
                        linePosToStart = getPositionByXCoord(x) - start
                        return true
                    }

                    val tmpX = closestValidPosition(getPositionByXCoord(x))
                    val xS = Math.abs(tmpX - start)
                    val xE = Math.abs(tmpX - end)

                    if (xS < xE && ((end - tmpX) >= getMinimalDistance()) && ((end - tmpX) <= getMaximalDistance())) {
                        if (isRange) {
                            start = tmpX
                            isStartPressed = true
                            fadeIn(currentLeftFocusRadiusPx, activeThumbFocusRadius)
                            onTrackRangeListener?.onStartRangeChanged(this, start)
                        } else {
                            return false
                        }
                    } else if (xS >= xE && ((tmpX - start) >= getMinimalDistance()) && ((tmpX - start) <= getMaximalDistance())) {
                        end = tmpX
                        isEndPressed = true
                        fadeIn(currentRightFocusRadiusPx, activeThumbFocusRadius)
                        onTrackRangeListener?.onEndRangeChanged(this, end)
                    }
                    return true
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isRangeMoving = false

                if (isStartPressed) {
                    isStartPressed = false
                    fadeOut(currentLeftFocusRadiusPx, activeThumbFocusRadius)
                }

                if (isEndPressed) {
                    isEndPressed = false
                    fadeOut(currentRightFocusRadiusPx, activeThumbFocusRadius)
                }

                onChangeRangeListener?.onRangeChanged(this, start, end)
            }
        }

        return true
    }

    private fun closestValidPosition(pos: Int): Int {
        if (showFixedLine) {
            if (pos < startFixed) {
                return startFixed
            }

            if (pos > endFixed) {
                return endFixed
            }
        } else {
            if (pos < 0) {
                return 0
            }

            if (pos >= count) {
                return count - 1
            }
        }
        return pos
    }

    private fun validatePosition(pos: Int) =
        if (showFixedLine) pos in startFixed..endFixed else pos in 0..(count - 1)

    private fun validatePositionForStart(pos: Int) =
        validatePosition(pos) && (pos <= end - getMinimalDistance() && pos >= end - getMaximalDistance())

    private fun validatePositionForEnd(pos: Int) =
        validatePosition(pos) && (pos >= start + getMinimalDistance() && pos <= start + getMaximalDistance())

    private fun getPositionByXCoord(x: Float) = ((x - innerRangePaddingLeft) / stepPx).toInt()

    private fun getMaximalDistance() = if (maxDistance > 0) maxDistance else count
    private fun getMinimalDistance() = if (minDistance > 0) minDistance else 0

    private fun isInTargetZone(pos: Int, x: Float, y: Float): Boolean {
        val xDiff = Math.abs(x - getPositionX(pos))
        val yDiff = Math.abs(y - getPositionY())
        return xDiff <= activeThumbRadius && yDiff <= activeThumbRadius
    }

    private fun getTextRect(text: String, paint: Paint): Rect {
        val rect = Rect()
        paint.getTextBounds(text, 0, text.length, rect)
        return rect
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val desiredHeight = calcDesiredHeight()
        val desiredWidth = calcDesiredWidth()

        var width: Int
        var height: Int

        width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> Math.min(desiredWidth, widthSize)
            else -> desiredWidth
        }

        height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> Math.min(desiredHeight, heightSize)
            else -> desiredHeight
        }

        if (width < 0) {
            width = 0
        }

        if (height < 0) {
            height = 0
        }

        setMeasuredDimension(width, height)
    }

    private fun calcDesiredWidth(): Int = 0

    private fun calcMaxRadius() = listOf(
        tickRadius,
        fixedTickRadius,
        activeTickRadius,
        activeThumbRadius,
        fixedThumbRadius,
        activeThumbFocusRadius
    ).max()!!

    private fun calcMaxHeight(): Float {
        val maxRadius = calcMaxRadius() * 2
        val maxLineHeight = listOf(lineThickness, activeLineThickness, fixedLineThickness).max()!!
        return Math.max(maxRadius, maxLineHeight)
    }

    private fun calcDesiredHeight(): Int {
        val h = calcMaxHeight() / 2
        val labelHeight = Math.max(
            if (showLabels && onRangeLabelsListener != null) labelMarginBottom + labelFontSize else 0f,
            h
        )
        return (h + labelHeight).toInt()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val yOffset = calcMaxHeight() / 2.0f

        posY = h - yOffset
        lineY = posY - lineThickness / 2.0f
        lineYActive = posY - activeLineThickness / 2.0f
        lineYFixed = posY - fixedLineThickness / 2.0f

        stepPx = (w - (innerRangePaddingLeft + innerRangePaddingRight)) / (count - 1)
    }


    //
    // Animations
    //

    private val fadeInAnim: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)
    private val fadeOutAnim: ValueAnimator = ValueAnimator.ofFloat(1f, 0f)

    private fun fadeIn(value: ValueWrapper<Float>, normalValue: Float) = fadeInAnim.apply {
        duration = 200
        addUpdateListener {
            value.value = (animatedValue as Float) * (normalValue)
            ViewCompat.postInvalidateOnAnimation(this@SimpleRangeView)
        }
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                value.value = normalValue
                removeAllListeners()
                removeAllUpdateListeners()
            }
        })
        start()
    }

    private fun fadeOut(value: ValueWrapper<Float>, normalValue: Float) = fadeOutAnim.apply {
        duration = 200

        addUpdateListener {
            value.value = (animatedValue as Float) * (normalValue)
            ViewCompat.postInvalidateOnAnimation(this@SimpleRangeView)
        }

        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                value.value = 0f
                removeAllListeners()
                removeAllUpdateListeners()
            }
        })

        start()
    }

    //
    // Internal classes
    //

    class ValueWrapper<T>(var value: T)

    enum class State {
        ACTIVE, ACTIVE_THUMB, FIXED, FIXED_THUMB, NORMAL
    }

    override fun onSaveInstanceState(): Parcelable {
        //begin boilerplate code that allows parent classes to save state
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        //end

        ss.labelColor = this.labelColor
        ss.activeLabelColor = this.activeLabelColor
        ss.activeThumbLabelColor = this.activeThumbLabelColor
        ss.fixedLabelColor = this.fixedLabelColor
        ss.fixedThumbLabelColor = this.fixedThumbLabelColor
        ss.lineColor = this.lineColor
        ss.activeLineColor = this.activeLineColor
        ss.fixedLineColor = this.fixedLineColor
        ss.tickColor = this.tickColor
        ss.activeTickColor = this.activeTickColor
        ss.fixedTickColor = this.fixedTickColor
        ss.activeThumbColor = this.activeThumbColor
        ss.activeFocusThumbColor = this.activeFocusThumbColor
        ss.fixedThumbColor = this.fixedThumbColor
        ss.activeFocusThumbAlpha = this.activeFocusThumbAlpha
        ss.lineThickness = this.lineThickness
        ss.activeLineThickness = this.activeLineThickness
        ss.fixedLineThickness = this.fixedLineThickness
        ss.activeThumbRadius = this.activeThumbRadius
        ss.activeThumbFocusRadius = this.activeThumbFocusRadius
        ss.fixedThumbRadius = this.fixedThumbRadius
        ss.tickRadius = this.tickRadius
        ss.activeTickRadius = this.activeTickRadius
        ss.fixedTickRadius = this.fixedTickRadius
        ss.labelMarginBottom = this.labelMarginBottom
        ss.labelFontSize = this.labelFontSize
        ss.minDistanceBetweenLabels = this.minDistanceBetweenLabels
        ss.innerRangePadding = this.innerRangePadding
        ss.innerRangePaddingLeft = this.innerRangePaddingLeft
        ss.innerRangePaddingRight = this.innerRangePaddingRight
        ss.count = this.count
        ss.startFixed = this.startFixed
        ss.endFixed = this.endFixed
        ss.start = this.start
        ss.end = this.end
        ss.minDistance = this.minDistance
        ss.maxDistance = this.maxDistance
        ss.movable = this.movable
        ss.showFixedLine = this.showFixedLine
        ss.showTicks = this.showTicks
        ss.showActiveTicks = this.showActiveTicks
        ss.showFixedTicks = this.showFixedTicks
        ss.showLabels = this.showLabels

        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        //begin boilerplate code so parent classes can restore state
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
            return
        }

        super.onRestoreInstanceState(state.superState)
        //end

        this.labelColor = state.labelColor
        this.activeLabelColor = state.activeLabelColor
        this.activeThumbLabelColor = state.activeThumbLabelColor
        this.fixedLabelColor = state.fixedLabelColor
        this.fixedThumbLabelColor = state.fixedThumbLabelColor
        this.lineColor = state.lineColor
        this.activeLineColor = state.activeLineColor
        this.fixedLineColor = state.fixedLineColor
        this.tickColor = state.tickColor
        this.activeTickColor = state.activeTickColor
        this.fixedTickColor = state.fixedTickColor
        this.activeThumbColor = state.activeThumbColor
        this.activeFocusThumbColor = state.activeFocusThumbColor
        this.fixedThumbColor = state.fixedThumbColor
        this.activeFocusThumbAlpha = state.activeFocusThumbAlpha
        this.lineThickness = state.lineThickness
        this.activeLineThickness = state.activeLineThickness
        this.fixedLineThickness = state.fixedLineThickness
        this.activeThumbRadius = state.activeThumbRadius
        this.activeThumbFocusRadius = state.activeThumbFocusRadius
        this.fixedThumbRadius = state.fixedThumbRadius
        this.tickRadius = state.tickRadius
        this.activeTickRadius = state.activeTickRadius
        this.fixedTickRadius = state.fixedTickRadius
        this.labelMarginBottom = state.labelMarginBottom
        this.labelFontSize = state.labelFontSize
        this.minDistanceBetweenLabels = state.minDistanceBetweenLabels
        this.innerRangePadding = state.innerRangePadding
        this.innerRangePaddingLeft = state.innerRangePaddingLeft
        this.innerRangePaddingRight = state.innerRangePaddingRight
        this.count = state.count
        this.startFixed = state.startFixed
        this.endFixed = state.endFixed
        this.start = state.start
        this.end = state.end
        this.minDistance = state.minDistance
        this.maxDistance = state.maxDistance
        this.movable = state.movable
        this.showFixedLine = state.showFixedLine
        this.showTicks = state.showTicks
        this.showActiveTicks = state.showActiveTicks
        this.showFixedTicks = state.showFixedTicks
        this.showLabels = state.showLabels
    }

    internal class SavedState : BaseSavedState {
        var labelColor: Int = 0
        var activeLabelColor: Int = 0
        var activeThumbLabelColor: Int = 0
        var fixedLabelColor: Int = 0
        var fixedThumbLabelColor: Int = 0
        var lineColor: Int = 0
        var activeLineColor: Int = 0
        var fixedLineColor: Int = 0
        var tickColor: Int = 0
        var activeTickColor: Int = 0
        var fixedTickColor: Int = 0
        var activeThumbColor: Int = 0
        var activeFocusThumbColor: Int = 0
        var fixedThumbColor: Int = 0
        var activeFocusThumbAlpha: Float = 0f
        var lineThickness: Float = 0f
        var activeLineThickness: Float = 0f
        var fixedLineThickness: Float = 0f
        var activeThumbRadius: Float = 0f
        var activeThumbFocusRadius: Float = 0f
        var fixedThumbRadius: Float = 0f
        var tickRadius: Float = 0f
        var activeTickRadius: Float = 0f
        var fixedTickRadius: Float = 0f
        var labelMarginBottom: Float = 0f
        var labelFontSize: Float = 0f
        var minDistanceBetweenLabels: Float = 0f
        var innerRangePadding: Float = 0f
        var innerRangePaddingLeft: Float = 0f
        var innerRangePaddingRight: Float = 0f
        var count: Int = 0
        var startFixed: Int = 0
        var endFixed: Int = 0
        var start: Int = 0
        var end: Int = 0
        var minDistance: Int = 0
        var maxDistance: Int = 0

        var movable: Boolean = false
        var showFixedLine: Boolean = false
        var showTicks: Boolean = false
        var showActiveTicks: Boolean = false
        var showFixedTicks: Boolean = false
        var showLabels: Boolean = false

        internal constructor(superState: Parcelable) : super(superState)

        @SuppressLint("NewApi")
        private constructor(input: Parcel, classLoader: ClassLoader) : super(input, classLoader) {
            this.labelColor = input.readInt()
            this.activeLabelColor = input.readInt()
            this.activeThumbLabelColor = input.readInt()
            this.fixedLabelColor = input.readInt()
            this.fixedThumbLabelColor = input.readInt()

            this.lineColor = input.readInt()
            this.activeLineColor = input.readInt()
            this.fixedLineColor = input.readInt()
            this.tickColor = input.readInt()
            this.activeTickColor = input.readInt()
            this.fixedTickColor = input.readInt()
            this.activeThumbColor = input.readInt()
            this.activeFocusThumbColor = input.readInt()
            this.fixedThumbColor = input.readInt()

            this.activeFocusThumbAlpha = input.readFloat()

            this.lineThickness = input.readFloat()
            this.activeLineThickness = input.readFloat()
            this.fixedLineThickness = input.readFloat()
            this.activeThumbRadius = input.readFloat()
            this.activeThumbFocusRadius = input.readFloat()
            this.fixedThumbRadius = input.readFloat()
            this.tickRadius = input.readFloat()
            this.activeTickRadius = input.readFloat()
            this.fixedTickRadius = input.readFloat()
            this.labelMarginBottom = input.readFloat()
            this.labelFontSize = input.readFloat()
            this.minDistanceBetweenLabels = input.readFloat()
            this.innerRangePadding = input.readFloat()
            this.innerRangePaddingLeft = input.readFloat()
            this.innerRangePaddingRight = input.readFloat()

            this.count = input.readInt()
            this.startFixed = input.readInt()
            this.endFixed = input.readInt()
            this.start = input.readInt()
            this.end = input.readInt()
            this.minDistance = input.readInt()
            this.maxDistance = input.readInt()

            this.movable = input.readInt() == 1
            this.showFixedLine = input.readInt() == 1
            this.showTicks = input.readInt() == 1
            this.showActiveTicks = input.readInt() == 1
            this.showFixedTicks = input.readInt() == 1
            this.showLabels = input.readInt() == 1
        }

        override fun writeToParcel(output: Parcel, flags: Int) {
            super.writeToParcel(output, flags)
            output.writeInt(this.labelColor)
            output.writeInt(this.activeLabelColor)
            output.writeInt(this.activeThumbLabelColor)
            output.writeInt(this.fixedLabelColor)
            output.writeInt(this.fixedThumbLabelColor)

            output.writeInt(this.lineColor)
            output.writeInt(this.activeLineColor)
            output.writeInt(this.fixedLineColor)
            output.writeInt(this.tickColor)
            output.writeInt(this.activeTickColor)
            output.writeInt(this.fixedTickColor)
            output.writeInt(this.activeThumbColor)
            output.writeInt(this.activeFocusThumbColor)
            output.writeInt(this.fixedThumbColor)

            output.writeFloat(this.activeFocusThumbAlpha)

            output.writeFloat(this.lineThickness)
            output.writeFloat(this.activeLineThickness)
            output.writeFloat(this.fixedLineThickness)
            output.writeFloat(this.activeThumbRadius)
            output.writeFloat(this.activeThumbFocusRadius)
            output.writeFloat(this.fixedThumbRadius)
            output.writeFloat(this.tickRadius)
            output.writeFloat(this.activeTickRadius)
            output.writeFloat(this.fixedTickRadius)
            output.writeFloat(this.labelMarginBottom)
            output.writeFloat(this.labelFontSize)
            output.writeFloat(this.minDistanceBetweenLabels)
            output.writeFloat(this.innerRangePadding)
            output.writeFloat(this.innerRangePaddingLeft)
            output.writeFloat(this.innerRangePaddingRight)

            output.writeInt(this.count)
            output.writeInt(this.startFixed)
            output.writeInt(this.endFixed)
            output.writeInt(this.start)
            output.writeInt(this.end)
            output.writeInt(this.minDistance)
            output.writeInt(this.maxDistance)

            output.writeInt(if (this.movable) 1 else 0)
            output.writeInt(if (this.showFixedLine) 1 else 0)
            output.writeInt(if (this.showTicks) 1 else 0)
            output.writeInt(if (this.showActiveTicks) 1 else 0)
            output.writeInt(if (this.showFixedTicks) 1 else 0)
            output.writeInt(if (this.showLabels) 1 else 0)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.ClassLoaderCreator<SavedState> =
                object : Parcelable.ClassLoaderCreator<SavedState> {
                    override fun createFromParcel(source: Parcel, loader: ClassLoader): SavedState =
                        SavedState(source, loader)

                    override fun createFromParcel(input: Parcel): SavedState? = null
                    override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
                }
        }
    }

    //
    // Delegated properties
    //

    private fun <T> updatePaintsAndRedraw(initialValue: T): ReadWriteProperty<SimpleRangeView, T> {
        return Delegates.observable(initialValue) { _, _, _ ->
            initPaints()
            invalidate()
        }
    }

    private fun <T> redraw(
        initialValue: T,
        f: (v: T) -> T = { it }
    ): ReadWriteProperty<SimpleRangeView, T> {
        return convertable(initialValue, f) { _, _, _ ->
            invalidate()
        }
    }

    private fun <T> updateView(initialValue: T): ReadWriteProperty<SimpleRangeView, T> {
        return Delegates.observable(initialValue) { _, _, _ ->
            initPaints()
            requestLayout()
        }
    }

    //
    // Callbacks
    //

    interface OnChangeRangeListener {
        fun onRangeChanged(rangeView: SimpleRangeView, start: Int, end: Int)
    }

    interface OnTrackRangeListener {
        fun onStartRangeChanged(rangeView: SimpleRangeView, leftPinIndex: Int)
        fun onEndRangeChanged(rangeView: SimpleRangeView, rightPinIndex: Int)
    }

    interface OnRangeLabelsListener {
        fun getLabelTextForPosition(rangeView: SimpleRangeView, pos: Int, state: State): String?
    }

    //
    // Builder
    //

    class Builder(private val context: Context) {
        private var labelColor: Int? = null
        private var activeLabelColor: Int? = null
        private var activeThumbLabelColor: Int? = null
        private var fixedLabelColor: Int? = null
        private var fixedThumbLabelColor: Int? = null
        private var lineColor: Int? = null
        private var activeLineColor: Int? = null
        private var fixedLineColor: Int? = null
        private var tickColor: Int? = null
        private var activeTickColor: Int? = null
        private var fixedTickColor: Int? = null
        private var activeThumbColor: Int? = null
        private var activeFocusThumbColor: Int? = null
        private var fixedThumbColor: Int? = null
        private var activeFocusThumbAlpha: Float? = null

        private var lineThickness: Float? = null
        private var activeLineThickness: Float? = null
        private var fixedLineThickness: Float? = null
        private var tickRadius: Float? = null
        private var activeThumbFocusRadius: Float? = null
        private var activeThumbRadius: Float? = null
        private var activeTickRadius: Float? = null
        private var fixedThumbRadius: Float? = null
        private var fixedTickRadius: Float? = null
        private var labelFontSize: Float? = null
        private var labelMarginBottom: Float? = null
        private var minDistanceBetweenLabels: Float? = null

        private var innerRangePaddingLeft: Float? = null
        private var innerRangePaddingRight: Float? = null
        private var innerRangePadding: Float? = null

        private var count: Int? = null
        private var start: Int? = null
        private var end: Int? = null
        private var minDistance: Int? = null
        private var maxDistance: Int? = null

        private var startFixed: Int? = null
        private var endFixed: Int? = null

        private var movable: Boolean? = null
        private var showFixedLine: Boolean? = null
        private var showTicks: Boolean? = null
        private var showActiveTicks: Boolean? = null
        private var showFixedTicks: Boolean? = null
        private var showLabels: Boolean? = null

        private var onChangeRangeListener: OnChangeRangeListener? = null
        private var onRangeLabelsListener: OnRangeLabelsListener? = null
        private var onTrackRangeListener: OnTrackRangeListener? = null

        fun labelColor(color: Int): Builder {
            labelColor = color
            return this
        }

        fun activeLabelColor(color: Int): Builder {
            activeLabelColor = color
            return this
        }

        fun activeThumbLabelColor(color: Int): Builder {
            activeThumbLabelColor = color
            return this
        }

        fun fixedLabelColor(color: Int): Builder {
            fixedLabelColor = color
            return this
        }

        fun fixedThumbLabelColor(color: Int): Builder {
            fixedThumbLabelColor = color
            return this
        }

        fun lineColor(color: Int): Builder {
            lineColor = color
            return this
        }

        fun activeLineColor(color: Int): Builder {
            activeLineColor = color
            return this
        }

        fun fixedLineColor(color: Int): Builder {
            fixedLineColor = color
            return this
        }

        fun tickColor(color: Int): Builder {
            tickColor = color
            return this
        }

        fun activeTickColor(color: Int): Builder {
            activeTickColor = color
            return this
        }

        fun fixedTickColor(color: Int): Builder {
            fixedTickColor = color
            return this
        }

        fun activeThumbColor(color: Int): Builder {
            activeThumbColor = color
            return this
        }

        fun activeFocusThumbColor(color: Int): Builder {
            activeFocusThumbColor = color
            return this
        }

        fun fixedThumbColor(color: Int): Builder {
            fixedThumbColor = color
            return this
        }

        fun activeFocusThumbAlpha(alpha: Float): Builder {
            activeFocusThumbAlpha = alpha
            return this
        }

        fun lineThickness(px: Float): Builder {
            lineThickness = px
            return this
        }

        fun activeLineThickness(px: Float): Builder {
            activeLineThickness = px
            return this
        }

        fun fixedLineThickness(px: Float): Builder {
            fixedLineThickness = px
            return this
        }

        fun activeThumbFocusRadius(px: Float): Builder {
            activeThumbFocusRadius = px
            return this
        }

        fun activeThumbRadius(px: Float): Builder {
            activeThumbRadius = px
            return this
        }

        fun fixedThumbRadius(px: Float): Builder {
            fixedThumbRadius = px
            return this
        }

        fun tickRadius(px: Float): Builder {
            tickRadius = px
            return this
        }

        fun activeTickRadius(px: Float): Builder {
            activeTickRadius = px
            return this
        }

        fun fixedTickRadius(px: Float): Builder {
            fixedTickRadius = px
            return this
        }

        fun innerRangePadding(px: Float): Builder {
            innerRangePadding = px
            return this
        }

        fun innerRangePaddingLeft(px: Float): Builder {
            innerRangePaddingLeft = px
            return this
        }

        fun innerRangePaddingRight(px: Float): Builder {
            innerRangePaddingRight = px
            return this
        }

        fun labelMarginBottom(px: Float): Builder {
            labelMarginBottom = px
            return this
        }

        fun minDistanceBetweenLabels(px: Float): Builder {
            minDistanceBetweenLabels = px
            return this
        }

        fun labelFontSize(px: Float): Builder {
            labelFontSize = px
            return this
        }

        fun count(value: Int): Builder {
            count = value
            return this
        }

        fun start(value: Int): Builder {
            start = value
            return this
        }

        fun end(value: Int): Builder {
            end = value
            return this
        }

        fun startFixed(value: Int): Builder {
            startFixed = value
            return this
        }

        fun endFixed(value: Int): Builder {
            endFixed = value
            return this
        }

        fun minDistance(value: Int): Builder {
            minDistance = value
            return this
        }

        fun maxDistance(value: Int): Builder {
            maxDistance = value
            return this
        }

        fun showFixedLine(value: Boolean): Builder {
            showFixedLine = value
            return this
        }

        fun movable(value: Boolean): Builder {
            movable = value
            return this
        }

        fun showTicks(value: Boolean): Builder {
            showTicks = value
            return this
        }

        fun showActiveTicks(value: Boolean): Builder {
            showActiveTicks = value
            return this
        }

        fun showFixedTicks(value: Boolean): Builder {
            showFixedTicks = value
            return this
        }

        fun showLabels(value: Boolean): Builder {
            showLabels = value
            return this
        }

        fun onChangeRangeListener(listener: OnChangeRangeListener): Builder {
            onChangeRangeListener = listener
            return this
        }

        fun onRangeLabelsListener(listener: OnRangeLabelsListener): Builder {
            onRangeLabelsListener = listener
            return this
        }

        fun onTrackRangeListener(listener: OnTrackRangeListener): Builder {
            onTrackRangeListener = listener
            return this
        }

        fun build(): SimpleRangeView {
            val rangeView = SimpleRangeView(context)

            rangeView.count = count ?: rangeView.count
            rangeView.start = start ?: rangeView.start
            rangeView.end = end ?: rangeView.end
            rangeView.startFixed = startFixed ?: rangeView.startFixed
            rangeView.endFixed = endFixed ?: rangeView.endFixed
            rangeView.showFixedLine = showFixedLine ?: rangeView.showFixedLine
            rangeView.minDistance = minDistance ?: rangeView.minDistance
            rangeView.maxDistance = maxDistance ?: rangeView.minDistance
            rangeView.movable = movable ?: rangeView.movable
            rangeView.showLabels = showLabels ?: rangeView.showLabels
            rangeView.showTicks = showTicks ?: rangeView.showTicks
            rangeView.showFixedTicks = showFixedTicks ?: rangeView.showFixedTicks
            rangeView.showActiveTicks = showActiveTicks ?: rangeView.showActiveTicks

            rangeView.labelColor = labelColor ?: rangeView.labelColor
            rangeView.activeLabelColor = activeLabelColor ?: rangeView.activeLabelColor
            rangeView.activeThumbLabelColor = activeThumbLabelColor ?:
                    rangeView.activeThumbLabelColor
            rangeView.fixedLabelColor = fixedLabelColor ?: rangeView.fixedLabelColor
            rangeView.fixedThumbLabelColor = fixedThumbLabelColor ?: rangeView.fixedThumbLabelColor
            rangeView.lineColor = lineColor ?: rangeView.lineColor
            rangeView.activeLineColor = activeLineColor ?: rangeView.activeLineColor
            rangeView.fixedLineColor = fixedLineColor ?: rangeView.fixedLineColor
            rangeView.tickColor = tickColor ?: rangeView.tickColor
            rangeView.activeTickColor = activeTickColor ?: rangeView.activeTickColor
            rangeView.fixedTickColor = fixedTickColor ?: rangeView.fixedTickColor
            rangeView.activeThumbColor = activeThumbColor ?: rangeView.activeThumbColor
            rangeView.activeFocusThumbColor = activeFocusThumbColor ?:
                    rangeView.activeFocusThumbColor
            rangeView.fixedThumbColor = fixedThumbColor ?: rangeView.fixedThumbColor
            rangeView.activeFocusThumbAlpha = activeFocusThumbAlpha ?:
                    rangeView.activeFocusThumbAlpha

            rangeView.lineThickness = lineThickness ?: rangeView.lineThickness
            rangeView.activeLineThickness = activeLineThickness ?: rangeView.activeLineThickness
            rangeView.fixedLineThickness = fixedLineThickness ?: rangeView.fixedLineThickness
            rangeView.tickRadius = tickRadius ?: rangeView.tickRadius
            rangeView.activeThumbFocusRadius = activeThumbFocusRadius ?:
                    rangeView.activeThumbFocusRadius
            rangeView.activeThumbRadius = activeThumbRadius ?: rangeView.activeThumbRadius
            rangeView.activeTickRadius = activeTickRadius ?: rangeView.activeTickRadius
            rangeView.fixedThumbRadius = fixedThumbRadius ?: rangeView.fixedThumbRadius
            rangeView.fixedTickRadius = fixedTickRadius ?: rangeView.fixedTickRadius
            rangeView.labelFontSize = labelFontSize ?: rangeView.labelFontSize
            rangeView.labelMarginBottom = labelMarginBottom ?: rangeView.labelMarginBottom
            rangeView.minDistanceBetweenLabels = minDistanceBetweenLabels ?:
                    rangeView.minDistanceBetweenLabels

            rangeView.innerRangePaddingLeft = innerRangePaddingLeft ?:
                    rangeView.innerRangePaddingLeft
            rangeView.innerRangePaddingRight = innerRangePaddingRight ?:
                    rangeView.innerRangePaddingRight
            rangeView.innerRangePadding = innerRangePadding ?: rangeView.innerRangePadding

            rangeView.onRangeLabelsListener = onRangeLabelsListener
            rangeView.onChangeRangeListener = onChangeRangeListener
            rangeView.onTrackRangeListener = onTrackRangeListener

            return rangeView
        }
    }

    //
    // Default values
    //

    private companion object DefaultValues {
        // Colors
        val DEFAULT_LABEL_COLOR = Color.parseColor("#C5C5C5")
        val DEFAULT_ACTIVE_LABEL_COLOR = Color.parseColor("#0C6CE1")
        val DEFAULT_ACTIVE_THUMB_LABEL_COLOR = Color.parseColor("#0F7BFF")
        val DEFAULT_FIXED_LABEL_COLOR = Color.parseColor("#C5C5C5")
        val DEFAULT_FIXED_THUMB_LABEL_COLOR = Color.parseColor("#C5C5C5")

        val DEFAULT_LINE_COLOR = Color.parseColor("#F7F7F7")
        val DEFAULT_ACTIVE_LINE_COLOR = Color.parseColor("#0C6CE1")
        val DEFAULT_FIXED_LINE_COLOR = Color.parseColor("#E3E3E3")
        val DEFAULT_TICK_COLOR = Color.parseColor("#C5C5C5")
        val DEFAULT_ACTIVE_TICK_COLOR = Color.parseColor("#FFFFFF")
        val DEFAULT_FIXED_TICK_COLOR = Color.parseColor("#C5C5C5")
        val DEFAULT_ACTIVE_THUMB_COLOR = Color.parseColor("#0F7BFF")
        val DEFAULT_ACTIVE_FOCUS_THUMB_COLOR = DEFAULT_ACTIVE_THUMB_COLOR
        val DEFAULT_FIXED_THUMB_COLOR = Color.parseColor("#E3E3E3")

        val DEFAULT_ACTIVE_FOCUS_THUMB_ALPHA = 1f

        val DEFAULT_LINE_THICKNESS = 4f
        val DEFAULT_ACTIVE_LINE_THICKNESS = 6f
        val DEFAULT_FIXED_LINE_THICKNESS = 6f

        val DEFAULT_ACTIVE_THUMB_FOCUS_RADIUS = 14f
        val DEFAULT_ACTIVE_THUMB_RADIUS = 10f
        val DEFAULT_FIXED_THUMB_RADIUS = 10f

        val DEFAULT_TICK_RADIUS = 1f
        val DEFAULT_ACTIVE_TICK_RADIUS = 1f
        val DEFAULT_FIXED_TICK_RADIUS = 1f

        val DEFAULT_INNER_RANGE_PADDING = 16f
        val DEFAULT_INNER_RANGE_PADDING_LEFT = DEFAULT_INNER_RANGE_PADDING
        val DEFAULT_INNER_RANGE_PADDING_RIGHT = DEFAULT_INNER_RANGE_PADDING

        val DEFAULT_COUNT = 10
        val DEFAULT_START = 0
        val DEFAULT_END = DEFAULT_COUNT - 1
        val DEFAULT_MINIMAL_DISTANCE = 1
        val DEFAULT_MAXIMAL_DISTANCE = 0

        val DEFAULT_START_FIXED = 0
        val DEFAULT_END_FIXED = 0

        val DEFAULT_SHOW_FIXED_LINE = false
        val DEFAULT_MOVABLE = false
        val DEFAULT_SHOW_TICKS = true
        val DEFAULT_SHOW_ACTIVE_TICKS = true
        val DEFAULT_SHOW_FIXED_TICKS = true
        val DEFAULT_SHOW_LABELS = true
        val DEFAULT_IS_RANGE = true

        val DEFAULT_LABEL_MARGIN_BOTTOM = 16f
        val DEFAULT_MINIMAL_DISTANCE_BETWEEN_LABELS = 20f // TODO

        val DEFAULT_LABEL_FONT_SIZE = 12f
    }
}