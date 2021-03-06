package com.whilerain.tinychart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.util.AttributeSet
import com.whilerain.tinychart.utils.UiUtil
import kotlin.math.roundToInt
import kotlin.properties.Delegates

open class TimeSeriesProgressView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ChartView(context, attrs, defStyleAttr) {

    /**
     * The max num of data show in thie chart view, this avoids too much data show in a chart
     */
    private val MAX_DATA_SIZE: Int = 100

    /**
     * Keep the originData for dynamically split show data into small pieces
     */
    private lateinit var originData: List<Float>

    /**
     * The animation percent float value between 0 to 1
     */
    private var percent: Float = 0f

    /**
     * The indicator position after separates original data into pieces
     */
    private var indicatorPositionPercent = 0f

    /**
     * Instance of the callback of current value
     */
    private var valueListener: ValueListener? = null

    /**
     * The value of current progress in the list
     */
    private var curValue by Delegates.observable(0f) { property, oldValue, newValue ->
        if (oldValue == newValue) return@observable
        valueListener?.onValueChanged(newValue)
    }

    var progressColor: Int = Color.LTGRAY
        set(value) {
            field = value
            paint.color = value
        }

    private val paint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = UiUtil.dpToPx(1).toFloat()
    }

    private val textPaint = TextPaint().apply {
        textSize = UiUtil.dpToPx(14).toFloat()
    }

    /**
     * callback of current value
     */
    interface ValueListener {
        fun onValueChanged(value: Float)
    }

    fun setListener(l: ValueListener){
        valueListener = l
    }

    fun remoteListener(){
        valueListener = null
    }

    override fun loadLineData(data: List<Float>) {
        if (data.isEmpty()) {
            originData = listOf()
            super.loadLineData(originData)
        } else {
            originData = data
            super.loadLineData(getRangedData())
        }
    }

    private fun getRangedData(): List<Float> {
        var index = (originData.size * percent).roundToInt() - 1
        index = if (index < 0) 0 else index
        curValue = getCurrValue(index)
        when {
            originData.size > MAX_DATA_SIZE -> {

                var min = Math.min(
                    index - ((MAX_DATA_SIZE - 1) / 2f).toInt(),
                    Math.max(0, (originData.size - 1 - MAX_DATA_SIZE))
                )
                min = if (min < 0) 0 else min
                var max = min + (MAX_DATA_SIZE - 1)
                max = if (max > originData.size - 1) originData.size - 1 else max

                indicatorPositionPercent = (index - (min - 1)).toFloat() / MAX_DATA_SIZE
                indicatorPositionPercent =
                    if (indicatorPositionPercent > 1) 1f else indicatorPositionPercent

                return originData.subList(min, max)
            }
            else -> {
                indicatorPositionPercent = percent
                return originData
            }
        }
    }

    private fun getCurrValue(index: Int): Float {
        return originData[index]
    }

    init {
        if (attrs != null) {
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.TimeSeriesProgressView,
                0, 0
            ).apply {
                progressColor =
                    getColor(R.styleable.TimeSeriesProgressView_progressColor, Color.LTGRAY)
                val strokeWidth = getDimension(
                    R.styleable.TimeSeriesProgressView_lineWidth,
                    UiUtil.dpToPx(1).toFloat()
                )
                paint.strokeWidth = strokeWidth
            }
        }
    }

    fun setup(tag: String, color: Int) {
        this.tag = tag
        lineColor = color
    }

    /**
     * Update the progress value
     * @param p which is between 0 to 1
     */
    fun onProgressUpdate(p: Float) {
        if (p > 1) throw IllegalArgumentException("p should be from 0 to 1")
        percent = p
        super.loadLineData(getRangedData())
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        drawProgressLine(canvas)
        super.onDraw(canvas)
    }

    private fun drawProgressLine(canvas: Canvas) {
        textPaint.color = lineColor
        val x = (chartRect.width() * indicatorPositionPercent) + chartRect.left
        val textShift = textPaint.measureText("00")
        canvas.drawLine(x, chartRect.top, x, chartRect.bottom, paint)
        canvas.drawText(String.format("%.1f", curValue), x, textShift, textPaint)
    }
}