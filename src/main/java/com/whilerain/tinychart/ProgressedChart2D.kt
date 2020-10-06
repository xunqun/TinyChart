package com.whilerain.tinychart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.constraintlayout.widget.ConstraintLayout
import com.whilerain.tinychart.ChartView2D
import com.whilerain.tinychart.MultiLineChartView
import com.whilerain.tinychart.utils.UiUtil

open class ProgressedChart2D @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ChartView2D(context, attrs, defStyleAttr) {

    /**
     * The current progress to show on chart
     */
    var progress = 0f
        set(value) {
            field = value
            invalidate()
        }


    /**
     * Paint to draw current progress line
     */
    private val linePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        isAntiAlias = false
        strokeWidth = UiUtil.dpToPx(1).toFloat()
    }

    private val markPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.WHITE
        isAntiAlias = false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            val x = it.getX()
            val y = it.getY()
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    var p = (x - chartBoundary.left) / chartBoundary.width()
                    if (p < 0) p = 0f
                    progress = p
                    return true
                }
                else -> {

                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawProgressLine(it)
            drawMark(it)
        }

    }

    private fun drawMark(canvas: Canvas) {
        for (i in lines.indices) {
            val point = lines[i].findValueOfProgress(progress)
            markPaint.color = lineColors[i % lineColors.size]
            lines[i].drawMark(canvas, displayBoundary, chartBoundary, point, markPaint)
        }
    }

    private fun drawProgressLine(canvas: Canvas?) {
        val x = chartBoundary.width() * progress + chartBoundary.left
        canvas?.drawLine(
            x,
            chartBoundary.top.toFloat(),
            x,
            chartBoundary.bottom.toFloat(),
            linePaint
        )
    }
}