package com.whilerain.tinychart

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_instant_value.*
import kotlinx.android.synthetic.main.view_label_progress_overlay_chart_2d.view.*

class LabelProgressOverlayChart2D @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    enum class Type {
        line, dot
    }


    private var view: ConstraintLayout =
        LayoutInflater.from(context)
            .inflate(R.layout.view_label_progress_overlay_chart_2d, this) as ConstraintLayout

    private val valueAdapter = InstantValueAdapter()

    val dataObs = Observer<ArrayList<Pair<Float, Float>>> {
        valueAdapter.setData(it)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        vProgressOverlayChart.obsMarkedPoint().observeForever(dataObs)
    }

    override fun onDetachedFromWindow() {
        vProgressOverlayChart.obsMarkedPoint().removeObserver(dataObs)
        super.onDetachedFromWindow()
    }

    var drawType = Type.line
        set(value) {
            field = value
            vProgressOverlayChart.drawAsDot = value == Type.dot
        }

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

    var yname_r: String = ""
        set(value) {
            field = value
            vYname_r.text = value
        }

    var xformat = "%.0f"
    var yformat = "%.0f"
    var yformat_r = "%.1f"


    fun setData(data: ArrayList<Line2D>, top: Float? = null, bottom: Float? = null) {
        vProgressOverlayChart.addData(data, top, bottom)
        updateFrame()
        vProgressOverlayChart.animate(1000)
    }

    fun setExtraData(data: ArrayList<Line2D>, top: Float? = null, bottom: Float? = null) {
        vProgressOverlayChart.addExtraData1(data, top, bottom)
        updateExtraFrame()
        vProgressOverlayChart.animate(1000)
    }

    private fun updateExtraFrame() {
        val bound = vProgressOverlayChart.extraDisplayBoundary1

        vY0_r.apply {
            text = String.format(yformat_r, bound.top)
//            setTextColor(vProgressOverlayChart.extraLineColors[0])
        }

        vY1_r.apply {
            text = String.format(yformat_r, bound.top + bound.height() * 0.25)
//            setTextColor(vProgressOverlayChart.extraLineColors[0])
        }
        vY2_r.apply {
            text = String.format(yformat_r, bound.top + bound.height() * 0.5)
//            setTextColor(vProgressOverlayChart.extraLineColors[0])
        }
        vY3_r.apply {
            text = String.format(yformat_r, bound.top + bound.height() * 0.75)
//            setTextColor(vProgressOverlayChart.extraLineColors[0])
        }
        vY4_r.apply {
            text = String.format(yformat_r, bound.bottom)
//            setTextColor(vProgressOverlayChart.extraLineColors[0])
        }

    }

    fun setColor(colors: List<Int>) {
        vProgressOverlayChart.lineColors = colors
        updateFrame()
        updateExtraFrame()
    }

    fun animate(t: Long) {
        vProgressOverlayChart.animate(t)
    }

    fun show() {
        vProgressOverlayChart.show()
    }

    fun updateFrame() {
        val bound = vProgressOverlayChart.displayBoundary
        vX0.text = String.format(xformat, bound.left)
        vX1.text = String.format(xformat, bound.left + bound.width() * 0.25)
        vX2.text = String.format(xformat, bound.left + bound.width() * 0.5)
        vX3.text = String.format(xformat, bound.left + bound.width() * 0.75)
        vX4.text = String.format(xformat, bound.right)

        vY0.apply {
            text = String.format(yformat, bound.top)
//            setTextColor(vProgressOverlayChart.lineColors[0])
        }
        vY1.apply {
            text = String.format(yformat, bound.top + bound.height() * 0.25)
//            setTextColor(vProgressOverlayChart.lineColors[0])
        }
        vY2.apply {
            text = String.format(yformat, bound.top + bound.height() * 0.5)
//            setTextColor(vProgressOverlayChart.lineColors[0])
        }
        vY3.apply {
            text = String.format(yformat, bound.top + bound.height() * 0.75)
//            setTextColor(vProgressOverlayChart.lineColors[0])
        }

        vY4.apply {
            text = String.format(yformat, bound.bottom)
//            setTextColor(vProgressOverlayChart.lineColors[0])
        }
    }

    inner class InstantValueAdapter : RecyclerView.Adapter<InstantVh>() {
        var list = arrayListOf<Pair<Float, Float>>()

        fun setData(l: ArrayList<Pair<Float, Float>>) {
            list = l
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstantVh {
            val v = LayoutInflater.from(context).inflate(R.layout.view_instant_value, null)
            return InstantVh(v)
        }

        override fun onBindViewHolder(holder: InstantVh, position: Int) {
            holder.onBind(position, list[position])
        }

        override fun getItemCount(): Int {
            return list.size
        }
    }

    inner class InstantVh(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun onBind(index: Int, point: Pair<Float, Float>) {
            text.text = String.format("%.3f", point.second)
            text.setTextColor(vProgressOverlayChart.lineColors[index % vProgressOverlayChart.lineColors.size])
        }
    }
}