package com.whilerain.tinychart

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import com.whilerain.tinychart.utils.UiUtil

import java.lang.Float.max

open class FixedWidthChart @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    val dataWith = 10

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
    protected var percent = 1f

    /**
     * Paints of drawing
     */
    private val framePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = UiUtil.dpToPx(1).toFloat()
        style = Paint.Style.STROKE
        color = mainColor
    }

    /**
     * Paint to draw current progress line
     */
    private val levelPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        alpha = 160
        isAntiAlias = false
        strokeWidth = 1f
        pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
    }

    private val zeroPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        isAntiAlias = false
        strokeWidth = UiUtil.dpToPx(1).toFloat()
    }

    private val textPaint = TextPaint().apply {
        textSize = UiUtil.dpToPx(12).toFloat()
        color = Color.WHITE
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = UiUtil.dpToPx(2).toFloat()

        style = Paint.Style.STROKE
        pathEffect = CornerPathEffect(UiUtil.dpToPx(5).toFloat())
    }

    var line: Line2D? = null
    var lineColors: List<Int> = listOf(Color.YELLOW, Color.CYAN, Color.BLUE)

    var dataBoundary = RectF(0f, 0f, 0f, 0f)

    // The chart display boundary
    var displayBoundary: RectF = RectF(0f, 0f, 10f, 10f)

    /**
     * View data
     */
    var viewBoundary: Rect = Rect(0, 0, 0, 0)


    fun setData(line: Line2D, top: Float? = null, bottom: Float? = null) {

        this.line = line
        if (line.raws.size <= 1) {
            invalidate()
            return
        }
        dataBoundary.left = line.raws[0].first
        dataBoundary.right = line.raws[0].first
        dataBoundary.top = line.raws[0].second
        dataBoundary.bottom = line.raws[0].second

        line.raws.forEach {
            if (it.first < dataBoundary.left) dataBoundary.left = it.first
            if (it.first > dataBoundary.right) dataBoundary.right = it.first
            if (it.second < dataBoundary.top) dataBoundary.top = it.second
            if (it.second > dataBoundary.bottom) dataBoundary.bottom = it.second
        }
        val bottomBoundSpace =
            (if (dataBoundary.height() > 0) dataBoundary.height() * 0.1f else dataBoundary.height() + 1)
        val topBoundSpace = if (dataBoundary.top == 0f) 0f else bottomBoundSpace
        val displayTop = top ?: dataBoundary.top - topBoundSpace
        val displayBottom = bottom ?: dataBoundary.bottom + bottomBoundSpace
        displayBoundary = RectF(

            dataBoundary.right - dataWith,
            displayTop,
            dataBoundary.right,
            displayBottom
        )
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            drawLevelLines(it)
            drawLines(canvas)
        }
        super.onDraw(canvas)
    }

    private fun drawLevelLines(c: Canvas) {
        for (i in 0..3) {
            val y = viewBoundary.top + viewBoundary.height() * 0.25f * i
            c.drawLine(
                viewBoundary.left.toFloat(),
                y,
                viewBoundary.right.toFloat(),
                y,
                levelPaint
            )
        }
        if (0f > dataBoundary.top && 0f < dataBoundary.bottom) {
            val zeroY =
                viewBoundary.top + viewBoundary.height() * ((dataBoundary.bottom - 0) / dataBoundary.height())
            c.drawLine(
                viewBoundary.left.toFloat(),
                zeroY,
                viewBoundary.right.toFloat(),
                zeroY,
                zeroPaint
            )
        } else {
            val zeroY = viewBoundary.top + viewBoundary.height().toFloat()
            c.drawLine(
                viewBoundary.left.toFloat(),
                zeroY,
                viewBoundary.right.toFloat(),
                zeroY,
                zeroPaint
            )
        }

        for (i in max(displayBoundary.left + 1, 0f).toInt()..(displayBoundary.right).toInt()) {

            if (i % 5 == 0) {
                val x =
                    viewBoundary.left + viewBoundary.width() * ((i - displayBoundary.left) / displayBoundary.width())
                val y = viewBoundary.bottom - 20f
                c.drawLine(
                    x,
                    viewBoundary.top.toFloat(),
                    x,
                    y,
                    levelPaint
                )
                c.drawText(i.toString(), x, y, textPaint)
            }
        }
    }

    private fun drawLines(canvas: Canvas) {
        if (line == null) return

        linePaint.color = lineColors[0]
        if (drawAsDot) {
            line!!.drawDot(
                canvas,
                displayBoundary,
                viewBoundary,
                line!!.raws,
                percent,
                linePaint
            )
        } else {
            line!!.drawPath(
                canvas,
                displayBoundary,
                viewBoundary,
                line!!.raws,
                percent,
                linePaint
            )
        }
    }

    /**
     * Clear data
     */
    fun clear() {
        line = null
        dataBoundary = RectF(0f, 0f, 0f, 0f)
        displayBoundary = RectF(0f, 0f, 10f, 10f)
        viewBoundary = Rect(0, 0, 0, 0)
    }

    /**
     * Given the color list
     */
    fun setColors(colors: List<Int>) {
        this.lineColors = colors
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        viewBoundary.left = paddingLeft
        viewBoundary.right = right - left - paddingRight - paddingLeft
        viewBoundary.top = paddingTop
        viewBoundary.bottom = bottom - top - paddingTop - paddingBottom
    }
}