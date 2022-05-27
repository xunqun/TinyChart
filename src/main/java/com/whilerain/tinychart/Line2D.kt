package com.whilerain.tinychart

import android.graphics.*
import android.text.TextPaint

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
            canvas.drawOval(RectF(it.first - 5, it.second - 5, it.first + 5, it.second + 5), paint)
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
        textPaint: TextPaint,
        index: Int = 0
    ) {
        val scaleX = Math.abs(chartBoundary.width() / displayBoundary.width())


        val scaleY = Math.abs(chartBoundary.height() / displayBoundary.height())
        val x = (point.first - displayBoundary.left) * scaleX + chartBoundary.left
        var y = (point.second - displayBoundary.top) * scaleY + chartBoundary.top
        y = chartBoundary.height() - y
        canvas.drawOval(RectF(x - 10, y - 10, x + 10, y + 10), paint)
        canvas.drawLine(chartBoundary.left.toFloat(), y, chartBoundary.right.toFloat(), y, paint)
        val text = String.format("%.2f", point.second)
        val width = (textPaint.measureText(text) + 30) * (index + 1)
        canvas.drawText(
            text,
            if (x < chartBoundary.width() / 2) chartBoundary.right - width else chartBoundary.left.toFloat() + width,
            if (y - chartBoundary.height() / 2 < chartBoundary.top) y + 30 else y,
            textPaint
        )
    }

    fun findValueOfProgress(target: Float): Pair<Float, Float> {
        try {
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
        } catch (e: IndexOutOfBoundsException) {
            return Pair(0f, 0f)
        }
    }

    fun getDataBound(): RectF? {
        val result = RectF()
        if (raws.isNotEmpty()) {
            result.left = raws[0].first
            result.right = raws[0].first
            result.top = raws[0].second
            result.bottom = raws[0].second
        } else {
            return null
        }

        for (raw in raws) {
            if (raw.first < result.left) result.left = raw.first
            if (raw.first > result.right) result.right = raw.first
            if (raw.second < result.bottom) result.bottom = raw.second
            if (raw.second > result.top) result.top = raw.second
        }
        return result
    }
}