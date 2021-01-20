package com.whilerain.tinychart

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import com.whilerain.tinychart.utils.UiUtil

open class ChartView2D @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * The color of the chart frame
     */
    private var mainColor = Color.LTGRAY
        set(value) {
            field = value
            framePaint.color = value
        }

    private var strokeWidth: Float = UiUtil.dpToPx(3).toFloat()
        set(value) {
            linePaint.strokeWidth = value
            field = value
        }

    var drawAsDot = false

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
        pathEffect = CornerPathEffect(UiUtil.dpToPx(16).toFloat())
    }

    /**
     * Raw data
     */
    protected var lines: ArrayList<Line2D> = ArrayList()
    var lineColors: List<Int> = listOf(Color.YELLOW, Color.CYAN, Color.BLUE)

    // The exact data boundary
    var dataBoundary: RectF = RectF(0f, 0f, 0f, 0f)

    // The chart display boundary
    var displayBoundary: RectF = RectF(0f, 0f, 10f, 10f)

    /**
     * View data
     */
    var chartBoundary: Rect = Rect(0, 0, 0, 0)


    private var percent = 0f
    private var animator: ValueAnimator? = null

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
                mainColor = getColor(R.styleable.TinyChart_mainColor, Color.WHITE)
                strokeWidth = getDimensionPixelSize(
                    R.styleable.TinyChart_lineStrokeWidth,
                    UiUtil.dpToPx(1)

                ).toFloat()
                drawAsDot = getBoolean(R.styleable.TinyChart_drawAsDot, false)
            }
        }
    }

    /**
     * Provides data list contains (x, y) as Pair
     */
    fun addData(lines: ArrayList<Line2D>) {
        if (lines.isNotEmpty() && lines[0].raws.isNotEmpty()) {
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

    fun show(){
        percent = 1f
        invalidate()
    }

    fun animate(duration: Long) {
        percent = 0f
        if (animator != null) animator!!.cancel()
        animator = ValueAnimator.ofFloat(0f, 1f).setDuration(duration)
        animator!!.addUpdateListener(ValueAnimator.AnimatorUpdateListener {
            percent = it.animatedValue as Float
            invalidate()
        })
        animator!!.start()
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
        this.lineColors = colors
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        chartBoundary.left = paddingLeft
        chartBoundary.right = right - left - paddingRight - paddingLeft
        chartBoundary.top = paddingTop
        chartBoundary.bottom = bottom - top - paddingTop - paddingBottom
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
            linePaint.color = lineColors[i % lineColors.size]
            if(drawAsDot){
                lines[i].drawDot(
                    canvas,
                    displayBoundary,
                    chartBoundary,
                    lines[i].raws,
                    percent,
                    linePaint
                )
            }else {
                lines[i].drawPath(
                    canvas,
                    displayBoundary,
                    chartBoundary,
                    lines[i].raws,
                    percent,
                    linePaint
                )
            }
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
        }
    }
}