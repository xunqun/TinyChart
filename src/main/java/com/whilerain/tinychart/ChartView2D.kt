package com.whilerain.tinychart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.whilerain.tinychart.utils.UiUtil

class ChartView2D @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * The color of the chart frame
     */
    private var mainColor = Color.GRAY
        set(value) {
            field = value
            framePaint.color = value
        }

    private var lineColor = Color.GRAY
        set(value) {
            field = value
            linePaint.color = value
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
        strokeWidth = UiUtil.dpToPx(2).toFloat()
        style = Paint.Style.STROKE
        color = mainColor
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = UiUtil.dpToPx(1).toFloat()
        style = Paint.Style.STROKE
        color = lineColor
    }

    /**
     * Raw data
     */
    private var raws: List<Pair<Float, Float>>? = null
    private var colors: List<Int> = listOf(Color.BLACK)

    // The exact data boundary
    private var dataBoundary: RectF = RectF(0f, 0f, 0f, 0f)

    // The chart display boundary
    private var displayBoundary: RectF = RectF(0f, 0f, 10f, 10f)

    /**
     * View data
     */
    private val chartBoundary: Rect = Rect(0, 0, 0, 0)

    init {
        if (attrs != null) {
            context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.TinyChart,
                0, 0
            ).apply {
                mainColor = getColor(R.styleable.TinyChart_mainColor, Color.LTGRAY)
                lineColor = getColor(R.styleable.TinyChart_lineChartColor, Color.BLACK)
                strokeWidth = getDimensionPixelSize(
                    R.styleable.TinyChart_lineStrokeWidth,
                    UiUtil.dpToPx(3)

                ).toFloat()
            }
        }
    }

    /**
     * Provides data list contains (x, y) as Pair
     */
    fun loadData(raws: List<Pair<Float, Float>>) {
        if (raws.isNotEmpty()) {
            dataBoundary.left = raws[0].first
            dataBoundary.right = raws[0].first
            dataBoundary.top = raws[0].second
            dataBoundary.bottom = raws[0].second
            raws.forEach {
                if (it.first < dataBoundary.left) dataBoundary.left = it.first
                if (it.first > dataBoundary.right) dataBoundary.right = it.first
                if (it.second < dataBoundary.top) dataBoundary.top = it.second
                if (it.second > dataBoundary.bottom) dataBoundary.bottom = it.second
            }
            displayBoundary = RectF(
                dataBoundary.left,
                dataBoundary.top,
                dataBoundary.right + dataBoundary.width() * 0.1f,
                dataBoundary.bottom + dataBoundary.height() * 0.1f
            )
            this.raws = raws
        } else {
            this.raws = null
        }
    }

    fun setColors(colors: List<Int>) {
        this.colors = colors
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        chartBoundary.left = left + paddingLeft
        chartBoundary.right = right - paddingRight
        chartBoundary.top = top + paddingTop
        chartBoundary.bottom = bottom - paddingBottom
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            drawFrame(canvas)
        }
        super.onDraw(canvas)
    }

    private fun drawFrame(canvas: Canvas) {
        if (chartBoundary.width() > 0) {
            canvas.drawLine(
                chartBoundary.left.toFloat(),
                chartBoundary.bottom.toFloat(),
                chartBoundary.right.toFloat(),
                chartBoundary.bottom.toFloat(),
                framePaint
            )
            canvas.drawLine(
                chartBoundary.left.toFloat(),
                chartBoundary.bottom.toFloat(),
                chartBoundary.left.toFloat(),
                chartBoundary.top.toFloat(),
                framePaint
            )
        }
    }
}