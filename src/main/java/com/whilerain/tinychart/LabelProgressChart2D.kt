package com.whilerain.tinychart

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.view_label_progress_chart_2d.view.*

class LabelProgressChart2D @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var view: ConstraintLayout =
        LayoutInflater.from(context)
            .inflate(R.layout.view_label_progress_chart_2d, this) as ConstraintLayout

    var xname: String = "TIME"
        set(value) {
            field = value
            vXname.text = value
        }

    var yname: String = "SPEED"
        set(value) {
            field = value
            vYname.text = value
        }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)
    }

    private fun chart() = view.findViewById<ProgressedChart2D>(R.id.vProgressChart)

    fun setData(data: ArrayList<Line2D>){
        chart().addData(data)
        updateFrame()
        chart().animate(1000)
    }

    fun updateFrame(){
        val bound = chart().displayBoundary
        vX0.text = String.format("%.0f", bound.left)
        vX1.text = String.format("%.0f", bound.left + bound.width() * 0.25)
        vX2.text = String.format("%.0f", bound.left + bound.width() * 0.5)
        vX3.text = String.format("%.0f", bound.left + bound.width() * 0.75)
        vX4.text = String.format("%.0f", bound.right)

        vY0.text = String.format("%.0f", bound.top)
        vY1.text = String.format("%.0f", bound.top + bound.height() * 0.25)
        vY2.text = String.format("%.0f", bound.top + bound.height() * 0.5)
        vY3.text = String.format("%.0f", bound.top + bound.height() * 0.75)
        vY4.text = String.format("%.0f", bound.bottom)
    }

}