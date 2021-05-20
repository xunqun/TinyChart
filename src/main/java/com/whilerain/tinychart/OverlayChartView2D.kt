package com.whilerain.tinychart

import android.content.Context
import android.graphics.Canvas
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import com.whilerain.tinychart.utils.UiUtil

open class OverlayChartView2D @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
): ChartView2D(context, attrs, defStyleAttr) {


    /**
     * Raw data
     */
    protected var extraLines1: ArrayList<Line2D> = ArrayList()

    /**
     *  The exact data boundary
     */
    var extraDataBoundary1: RectF = RectF(0f, 0f, 0f, 0f)

    /**
     * The chart display boundary
     */
    var extraDisplayBoundary1: RectF = RectF(0f, 0f, 10f, 10f)

    /**
     * Paint for extra lines
     */
    private val extraLinePaint1 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = UiUtil.dpToPx(1).toFloat()
        style = Paint.Style.STROKE
        pathEffect = CornerPathEffect(UiUtil.dpToPx(16).toFloat())
    }

    /**
     * Provides data list contains (x, y) as Pair
     */
    fun addExtraData1(lines: ArrayList<Line2D>) {
        if (lines.isNotEmpty() && lines[0].raws.isNotEmpty()) {
            extraDataBoundary1.left = lines[0].raws[0].first
            extraDataBoundary1.right = lines[0].raws[0].first
            extraDataBoundary1.top = lines[0].raws[0].second
            extraDataBoundary1.bottom = lines[0].raws[0].second
            lines.forEach { line ->
                line.raws.forEach {
                    if (it.first < extraDataBoundary1.left) extraDataBoundary1.left = it.first
                    if (it.first > extraDataBoundary1.right) extraDataBoundary1.right = it.first
                    if (it.second < extraDataBoundary1.top) extraDataBoundary1.top = it.second
                    if (it.second > extraDataBoundary1.bottom) extraDataBoundary1.bottom = it.second
                }
            }
            extraDisplayBoundary1 = RectF(
                dataBoundary.left,
                extraDataBoundary1.top,
                dataBoundary.right,
                extraDataBoundary1.bottom + (extraDataBoundary1.height() * 0.1f)
            )
            this.extraLines1 = lines
            invalidate()
        }
    }

    private fun drawExtraLines1(canvas: Canvas) {

        for (i in extraLines1.indices) {
            extraLinePaint1.color = lineColors[i % lineColors.size]
            if(drawAsDot){
                extraLines1[i].drawDot(
                    canvas,
                    displayBoundary,
                    chartBoundary,
                    extraLines1[i].raws,
                    percent,
                    extraLinePaint1
                )
            }else {
                extraLines1[i].drawPath(
                    canvas,
                    displayBoundary,
                    chartBoundary,
                    extraLines1[i].raws,
                    percent,
                    extraLinePaint1
                )
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawExtraLines1(it)
        }
    }
}