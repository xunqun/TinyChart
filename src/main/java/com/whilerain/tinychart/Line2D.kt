package com.whilerain.tinychart

import android.graphics.*
import android.util.Log

class Line2D(val raws: List<Pair<Float, Float>>) {
    val TAG  = javaClass.simpleName
    fun drawPath(
        canvas: Canvas,
        displayBoundary: RectF,
        chartBoundary: Rect,
        line: List<Pair<Float, Float>>,
        percent: Float,
        paint: Paint
    ) {
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
        paint: Paint
    ) {
        val scaleX = Math.abs(chartBoundary.width() / displayBoundary.width())
        val scaleY = Math.abs(chartBoundary.height() / displayBoundary.height())
        val x = (point.first - displayBoundary.left) * scaleX + chartBoundary.left
        var y = (point.second - displayBoundary.top) * scaleY + chartBoundary.top
        y = chartBoundary.height() - y
        canvas.drawOval(RectF(x - 10, y - 10, x + 10, y + 10), paint)
        canvas.drawLine(chartBoundary.left.toFloat(), y, chartBoundary.right.toFloat(), y, paint)

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