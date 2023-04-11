package com.whilerain.tinychart

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.view_label_fixed_width_chart_2d.view.*

class LabelFixedWidthChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var view: ConstraintLayout =
        LayoutInflater.from(context)
            .inflate(R.layout.view_label_fixed_width_chart_2d, this) as ConstraintLayout

    fun chart(): FixedWidthChart = view.findViewById(R.id.vFChart)
    fun setData(data: Line2D, top: Float? = null, bottom: Float? = null) {
        vFChart.setData(data, top, bottom)
        updateFrame()
//        chart().animate(1000)
    }

    fun setColor(colors: List<Int>) {
        chart().lineColors = colors
    }

    fun updateFrame() {
        val bound = chart().displayBoundary
//        vX0.text = String.format(xformat, bound.left)
//        vX1.text = String.format(xformat, bound.left + bound.width() * 0.25)
//        vX2.text = String.format(xformat, bound.left + bound.width() * 0.5)
//        vX3.text = String.format(xformat, bound.left + bound.width() * 0.75)
//        vX4.text = String.format(xformat, bound.right)

        vY0.text = String.format(yformat, bound.top)
        vY1.text = String.format(yformat, bound.top + bound.height() * 0.25)
        vY2.text = String.format(yformat, bound.top + bound.height() * 0.5)
        vY3.text = String.format(yformat, bound.top + bound.height() * 0.75)
        vY4.text = String.format(yformat, bound.bottom)
    }

    var xname: String = "TIME"
        set(value) {
            field = value
            vXname.text = value
        }

    var yname: String = "WHP"
        set(value) {
            field = value
            vYname.text = value
        }

    var xformat = "%.0f"
    var yformat = "%.0f"
}