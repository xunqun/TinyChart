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

    enum class Type{
        line, dot
    }


    private var view: ConstraintLayout =
        LayoutInflater.from(context)
            .inflate(R.layout.view_label_progress_overlay_chart_2d, this) as ConstraintLayout

    private val valueAdapter = InstantValueAdapter()

    init {


//        vInstantValueList.apply {
//            adapter = valueAdapter
//            layoutManager = LinearLayoutManager(context)
//        }
    }

    val dataObs = Observer<ArrayList<Pair<Float, Float>>> {
        valueAdapter.setData(it)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        vProgressChart.obsMarkedPoint().observeForever(dataObs)
    }

    override fun onDetachedFromWindow() {
        vProgressChart.obsMarkedPoint().removeObserver(dataObs)
        super.onDetachedFromWindow()
    }

    var drawType = Type.line
        set(value) {
            field = value
            vProgressChart.drawAsDot = value == Type.dot
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

    var xformat = "%.0f"
    var yformat = "%.0f"

    private fun chart() = view.findViewById<ProgressedOverlayChart2D>(R.id.vProgressChart)

    fun setData(data: ArrayList<Line2D>) {
        chart().addData(data)
        updateFrame()
        chart().animate(1000)
    }

    fun setExtraData(data: ArrayList<Line2D>){
        chart().addExtraData1(data)
        updateFrame()
        chart().animate(1000)
    }

    fun setColor(colors: List<Int>){
        vProgressChart.lineColors = colors
    }

    fun animate(t: Long){
        chart().animate(t)
    }

    fun show(){
        chart().show()
    }

    fun updateFrame() {
        val bound = chart().displayBoundary
        vX0.text = String.format(xformat, bound.left)
        vX1.text = String.format(xformat, bound.left + bound.width() * 0.25)
        vX2.text = String.format(xformat, bound.left + bound.width() * 0.5)
        vX3.text = String.format(xformat, bound.left + bound.width() * 0.75)
        vX4.text = String.format(xformat, bound.right)

        vY0.text = String.format(yformat, bound.top)
        vY1.text = String.format(yformat, bound.top + bound.height() * 0.25)
        vY2.text = String.format(yformat, bound.top + bound.height() * 0.5)
        vY3.text = String.format(yformat, bound.top + bound.height() * 0.75)
        vY4.text = String.format(yformat, bound.bottom)
    }

    inner class InstantValueAdapter : RecyclerView.Adapter<InstantVh>() {
        var list = arrayListOf<Pair<Float, Float>>()

        fun setData(l: ArrayList<Pair<Float, Float>>){
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
            text.setTextColor(chart().lineColors[index % chart().lineColors.size])
        }
    }
}