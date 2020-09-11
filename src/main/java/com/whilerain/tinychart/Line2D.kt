package com.whilerain.tinychart

import android.graphics.*
import android.util.Log

class Line2D(val raws: List<Pair<Float, Float>>) {

    fun drawPath(
        canvas: Canvas,
        displayBoundary: RectF,
        chartBoundary: Rect,
        line: List<Pair<Float, Float>>,
        paint: Paint
    ) {
        var path: Path? = null
        line.map { r ->
            val scaleX =  Math.abs(chartBoundary.width() / displayBoundary.width())
            val scaleY = Math.abs(chartBoundary.height()/ displayBoundary.height())
            var x = (r.first - displayBoundary.left) * scaleX + chartBoundary.left
            var y = (r.second - displayBoundary.top) * scaleY + chartBoundary.top
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
}