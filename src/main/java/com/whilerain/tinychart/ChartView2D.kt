package com.whilerain.tinychart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import com.whilerain.tinychart.utils.UiUtil

class ChartView2D @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * The color of the chart frame
     */
    private var mainColor = Color.BLACK
        set(value) {
            field = value
            framePaint.color = value
        }

    private var strokeWidth: Float = UiUtil.dpToPx(3).toFloat()
        set(value) {
            linePaint.strokeWidth = value
            field = value
        }

    /**
     * Paints of drawing
     */
    private val framePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = UiUtil.dpToPx(1).toFloat()
        style = Paint.Style.STROKE
        color = mainColor
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = UiUtil.dpToPx(1).toFloat()
        style = Paint.Style.STROKE
    }

    /**
     * Raw data
     */
    private var lines: ArrayList<Line2D> = ArrayList()
    private var colors: List<Int> = listOf(Color.BLACK, Color.RED, Color.BLUE)

    // The exact data boundary
    private var dataBoundary: RectF = RectF(0f, 0f, 0f, 0f)

    // The chart display boundary
    private var displayBoundary: RectF = RectF(0f, 0f, 10f, 10f)

    /**
     * View data
     */
    private var chartBoundary: Rect = Rect(0, 0, 0, 0)

    /**
     * Touch listener
     */
    private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val spanDelta =
                (detector.currentSpanX - detector.previousSpanX) * (dataBoundary.width() / chartBoundary.width()) * 0.5
            Log.d("chart", "spanDelta: $spanDelta  ")
            val left = displayBoundary.left + spanDelta
            val right = displayBoundary.right - spanDelta
            if (right > left && left >= dataBoundary.left && right <= dataBoundary.right) {
                displayBoundary.left = left.toFloat()
                displayBoundary.right = right.toFloat()
            }
            invalidate()

            return true
        }
    }

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            val left =
                displayBoundary.left + distanceX * (displayBoundary.width() / chartBoundary.width())
            val right =
                displayBoundary.right + distanceX * (displayBoundary.width() / chartBoundary.width())
            if (left >= dataBoundary.left && right <= dataBoundary.right) {
                displayBoundary.left = left
                displayBoundary.right = right
                invalidate()
            }
            return true
        }
    }


    private val scaleDetector = ScaleGestureDetector(context, scaleListener)
    private val guestureDetector = GestureDetector(context, gestureListener)

    init {
        if (attrs != null) {
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.TinyChart,
                0, 0
            ).apply {
                mainColor = getColor(R.styleable.TinyChart_mainColor, Color.BLACK)
                strokeWidth = getDimensionPixelSize(
                    R.styleable.TinyChart_lineStrokeWidth,
                    UiUtil.dpToPx(1)

                ).toFloat()
            }
        }
    }

    /**
     * Provides data list contains (x, y) as Pair
     */
    fun addData(lines: ArrayList<Line2D>) {
        if (lines.isNotEmpty()) {
            dataBoundary.left = lines[0].raws[0].first
            dataBoundary.right = lines[0].raws[0].first
            dataBoundary.top = lines[0].raws[0].second
            dataBoundary.bottom = lines[0].raws[0].second
            lines.forEach { line ->
                line.raws.forEach {
                    if (it.first < dataBoundary.left) dataBoundary.left = it.first
                    if (it.first > dataBoundary.right) dataBoundary.right = it.first
                    if (it.second < dataBoundary.top) dataBoundary.top = it.second
                    if (it.second > dataBoundary.bottom) dataBoundary.bottom = it.second
                }
            }
            displayBoundary = RectF(
                dataBoundary.left,
                dataBoundary.top,
                dataBoundary.right,
                dataBoundary.bottom
            )
            this.lines = lines
            invalidate()
        }
    }

    /**
     * Clear data
     */
    fun clear() {
        lines.clear()
        dataBoundary = RectF(0f, 0f, 0f, 0f)
        displayBoundary = RectF(0f, 0f, 10f, 10f)
        chartBoundary = Rect(0, 0, 0, 0)
    }

    /**
     * Given the color list
     */
    fun setColors(colors: List<Int>) {
        this.colors = colors
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        chartBoundary.left = paddingLeft
        chartBoundary.right = right - left - paddingRight - paddingLeft
        chartBoundary.top = paddingTop
        chartBoundary.bottom = bottom - top - paddingTop - paddingBottom
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        scaleDetector.onTouchEvent(event)
        guestureDetector.onTouchEvent(event)
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            drawFrame(canvas)
            drawLines(canvas)
        }
        super.onDraw(canvas)
    }

    private fun drawLines(canvas: Canvas) {
        for (i in lines.indices) {
            linePaint.color = colors[i % colors.size]
            lines[i].drawPath(canvas, displayBoundary, chartBoundary, lines[i].raws, linePaint)
        }
    }

    private fun drawFrame(canvas: Canvas) {
        if (chartBoundary.width() > 0) {
            canvas.drawLine(
                chartBoundary.left.toFloat(),
                chartBoundary.bottom.toFloat() - 1,
                chartBoundary.right.toFloat(),
                chartBoundary.bottom.toFloat() - 1,
                framePaint
            )
            canvas.drawLine(
                chartBoundary.left.toFloat() + 1,
                chartBoundary.bottom.toFloat(),
                chartBoundary.left.toFloat() + 1,
                chartBoundary.top.toFloat(),
                framePaint
            )
        }
    }
}