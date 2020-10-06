package com.whilerain.tinychart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.whilerain.tinychart.ChartView2D
import com.whilerain.tinychart.MultiLineChartView
import com.whilerain.tinychart.utils.UiUtil

open class ProgressedChart2D @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ChartView2D(context) {

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
        color = Color.GRAY
        isAntiAlias = false
        strokeWidth = UiUtil.dpToPx(1).toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawProgressLine(canvas)
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