package com.whilerain.tinychart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _markedPoint = MutableLiveData<ArrayList<Pair<Float, Float>>>()

    /**
     * Paint to draw current progress line
     */
    private val levelPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        isAntiAlias = false
        strokeWidth = 1f
        pathEffect = DashPathEffect(floatArrayOf(5f, 5f), 0f)
    }

    private val textPaint = Paint().apply {
        textSize = UiUtil.dpToPx(14).toFloat()
    }

    private val markPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.WHITE
        isAntiAlias = false
    }

    private val blockPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.WHITE
        isAntiAlias = false
    }

    fun obsMarkedPoint(): LiveData<ArrayList<Pair<Float, Float>>> = _markedPoint

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
            drawLevels(it)
            drawProgressLine(it)
            drawMark(it)
        }

    }

    private fun drawLevels(c: Canvas) {
        for (i in 0..3) {
            val y = chartBoundary.top + chartBoundary.height() * 0.25f * i
            c.drawLine(
                chartBoundary.left.toFloat(),
                y,
                chartBoundary.right.toFloat(),
                y,
                levelPaint
            )
        }
    }

    private fun drawMark(canvas: Canvas) {
        val markedPair = arrayListOf<Pair<Float, Float>>()
        for (i in lines.indices) {
            val target = displayBoundary.left + displayBoundary.width() * progress
            val point = lines[i].findValueOfProgress(target)
            markPaint.color = lineColors[i % lineColors.size]
            lines[i].drawMark(canvas, displayBoundary, chartBoundary, point, markPaint)
            markedPair.add(point)
        }

        if (lines.size == 2) {
            val delta = String.format("%.2f", markedPair[0].second - markedPair[1].second)
            val text =
                if (markedPair[0].second - markedPair[1].second >= 0f) "+$delta" else "$delta"
            var x = chartBoundary.left + chartBoundary.width() * progress + UiUtil.dpToPx(8)

            var y =
                if (markedPair[0].second >= markedPair[1].second) (markedPair[0].second - displayBoundary.top) / displayBoundary.height() * chartBoundary.height()
                else (markedPair[1].second - displayBoundary.top) / displayBoundary.height() * chartBoundary.height()
            y = chartBoundary.height() - y - UiUtil.dpToPx(8)
            y = if (y - UiUtil.dpToPx(20) < chartBoundary.top) UiUtil.dpToPx(20).toFloat() else y
            val textWidth = textPaint.measureText(text) + UiUtil.dpToPx(8)
            x =
                if (x + textWidth > chartBoundary.right) chartBoundary.right.toFloat() - textWidth else x
            canvas.drawRoundRect(
                RectF(x, y - UiUtil.dpToPx(20), x + textWidth, y),
                10f,
                10f,
                blockPaint
            )
            canvas.drawText(text, x + UiUtil.dpToPx(4), y - UiUtil.dpToPx(4).toFloat(), textPaint)
            _markedPoint.value = markedPair
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