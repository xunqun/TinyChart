package com.whilerain.tinychart

import android.graphics.*
import android.text.TextPaint
import android.util.Log

class Line2D(val raws: List<Pair<Float, Float>>) {
    val TAG = javaClass.simpleName

    fun drawDot(
        canvas: Canvas,
        displayBoundary: RectF,
        chartBoundary: Rect,
        line: List<Pair<Float, Float>>,
        percent: Float,
        paint: Paint
    ) {
        paint.style = Paint.Style.FILL
        val count = (line.size * percent).toInt()
        line.subList(0, count).map { r ->
            val scaleX = Math.abs(chartBoundary.width() / displayBoundary.width())
            val scaleY = Math.abs(chartBoundary.height() / displayBoundary.height())
            val x = (r.first - displayBoundary.left) * scaleX + chartBoundary.left
            val y = (r.second - displayBoundary.top) * scaleY + chartBoundary.top
            Pair(x, chartBoundary.height() - y)

        }.forEach {
            canvas.drawOval(RectF(it.first-5, it.second- 5 , it.first + 5, it.second + 5), paint)
        }
    }

    fun drawPath(
        canvas: Canvas,
        displayBoundary: RectF,
        chartBoundary: Rect,
        line: List<Pair<Float, Float>>,
        percent: Float,
        paint: Paint
    ) {
        paint.style = Paint.Style.STROKE
        var path: Path? = null
        val count = (line.size * percent).toInt()
        line.subList(0, count).map { r ->
            val scaleX = Math.abs(chartBoundary.width() / displayBoundary.width())
            val scaleY = Math.abs(chartBoundary.height() / displayBoundary.height())
            val x = (r.first - displayBoundary.left) * scaleX + chartBoundary.left
            val y = (r.second - displayBoundary.top) * scaleY + chartBoundary.top
            Pair(x, chartBoundary.height() - y)

        }.forEach {
            if (path == null) {
                path = Path()
                path!!.moveTo(it.first, it.second)
            } else {
                path!!.lineTo(it.first, it.second)
            }
        }

        if (path != null && !path!!.isEmpty) {
            canvas.drawPath(path!!, paint)
        }
    }

    fun drawMark(
        canvas: Canvas, displayBoundary: RectF,
        chartBoundary: Rect,
        point: Pair<Float, Float>,
        paint: Paint,
        textPaint: TextPaint

    ) {
        val scaleX = Math.abs(chartBoundary.width() / displayBoundary.width())
        val scaleY = Math.abs(chartBoundary.height() / displayBoundary.height())
        val x = (point.first - displayBoundary.left) * scaleX + chartBoundary.left
        var y = (point.second - displayBoundary.top) * scaleY + chartBoundary.top
        y = chartBoundary.height() - y
        canvas.drawOval(RectF(x - 10, y - 10, x + 10, y + 10), paint)
        canvas.drawLine(chartBoundary.left.toFloat(), y, chartBoundary.right.toFloat(), y, paint)
        val text = String.format("%.2f", point.second)
        val width = textPaint.measureText(text) + 10
        canvas.drawText(text, chartBoundary.right - width, y , textPaint)
    }

    fun findValueOfProgress(target: Float): Pair<Float, Float> {
        var minDelta = Float.MAX_VALUE
        var result = raws[0]
        for (pair in raws) {
            val delta = Math.abs(pair.first - target)
            if (delta < minDelta) {
                result = pair
                minDelta = delta
            }
        }
        return result
    }
}