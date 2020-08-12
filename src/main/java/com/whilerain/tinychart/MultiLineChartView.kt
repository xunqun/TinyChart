package com.whilerain.tinychartimport android.content.Contextimport android.graphics.*import android.text.TextPaintimport android.util.AttributeSetimport android.view.Viewimport com.whilerain.tinychart.utils.UiUtilimport java.io.InvalidObjectExceptionopen class MultiLineChartView @JvmOverloads constructor(    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {    /**     * The padding of the wording beside the chart     */    private var labelPadding = 56    /**     * The bounding box of the chart     */    protected val chartRect = RectF()    /**     * Paints of drawing     */    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)    private val textPaint = TextPaint()    /**     * Colors for painting     */    var mainColor = Color.GRAY        set(value) {            field = value            bgPaint.color = value        }    var lineColor = Color.BLACK        set(value) {            field = value            linePaint.color = value        }    var labelColor = Color.WHITE        set(value) {            field = value            textPaint.color = value        }    /**     * To hide the text     */    var hideScaleText = false    var lineTextFormat: String = "%.0f"    var barTextFormat: String = "%.0f"    var strokeWidth: Float = UiUtil.dpToPx(3).toFloat()        set(value) {            linePaint.strokeWidth = value            field = value        }    /**     * The data list of line chart     */    private var lineDataSet = HashMap<Int, List<Float>>()    private var barData = listOf<Float>()    private val minMaxLineData = arrayOf(0f, 0f)    private val minMaxBarData = arrayOf(0f, 0f)    private var xTextData = listOf<String>()    init {        if (attrs != null) {            context.theme.obtainStyledAttributes(                attrs,                R.styleable.TinyChart,                0, 0            ).apply {                mainColor = getColor(R.styleable.TinyChart_mainColor, Color.LTGRAY)                labelColor = getColor(R.styleable.TinyChart_labelColor, Color.GRAY)                labelPadding = getDimensionPixelSize(R.styleable.TinyChart_labelPadding, 56)                hideScaleText = getBoolean(R.styleable.TinyChart_hideScaleText, false)                strokeWidth = getDimensionPixelSize(                    R.styleable.TinyChart_lineStrokeWidth,                    UiUtil.dpToPx(3)                ).toFloat()                lineTextFormat =                    if (hasValue(R.styleable.TinyChart_lineScaleTextFormat)) {                        getString(R.styleable.TinyChart_lineScaleTextFormat)                    } else "%.0f"                barTextFormat =                    if (hasValue(R.styleable.TinyChart_barScaleTextFormat)) {                        getString(R.styleable.TinyChart_barScaleTextFormat)                    } else "%.0f"            }        }        linePaint.strokeWidth = strokeWidth.toFloat()        linePaint.style = Paint.Style.STROKE        linePaint.color = lineColor        linePaint.strokeJoin = Paint.Join.ROUND        linePaint.strokeCap = Paint.Cap.ROUND        linePaint.pathEffect = CornerPathEffect(UiUtil.dpToPx(24).toFloat())        bgPaint.strokeWidth = UiUtil.dpToPx(1).toFloat()        bgPaint.style = Paint.Style.STROKE        bgPaint.color = mainColor        textPaint.textSize = UiUtil.dpToPx(12).toFloat()    }    fun loadXScaleData(data: List<String>) {        xTextData = data        invalidate()    }    open fun loadLineData(data: HashMap<Int, List<Float>>) {        if(data.size < 0) throw InvalidObjectException("Line data cannot have no content")        minMaxLineData[0] = data[0]!![0]        minMaxLineData[1] = data[0]!![0]        data.forEach{            for (d in it.value) {                if (d < minMaxLineData[0]) minMaxLineData[0] = d                if (d > minMaxLineData[1]) minMaxLineData[1] = d            }        }        val padding = chartDataPadding(minMaxLineData[1] - minMaxLineData[0])        minMaxLineData[0] = minMaxLineData[0] - padding        minMaxLineData[1] = minMaxLineData[1] + padding        lineDataSet = data        invalidate()    }    fun chartDataPadding(delta: Float) = if (delta != 0f) delta * 0.1f else 1f    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {        super.onLayout(changed, left, top, right, bottom)        chartRect.top = labelPadding.toFloat()        chartRect.left = labelPadding.toFloat()        chartRect.bottom = ((bottom - top) - labelPadding).toFloat()        chartRect.right = ((right - left) - labelPadding).toFloat()    }    override fun onDraw(canvas: Canvas) {        if (chartRect.height() > 0) {            drawChartBackground(canvas)            drawLineData(canvas)            drawLineScaleText(canvas, lineTextFormat)            drawXScaleText(canvas)        }        super.onDraw(canvas)    }    private fun drawXScaleText(canvas: Canvas) {        var x = 0f        val y = height - UiUtil.dpToPx(12).toFloat()        textPaint.color = mainColor        for (i in 0 until xTextData.size) {            x = getXAxis(i, xTextData.size)            canvas.drawText(xTextData[i], x, y, textPaint)        }    }    private fun drawLineScaleText(canvas: Canvas, format: String) {        if (lineDataSet.isEmpty() || hideScaleText) return        val x = 0f        var value = 0f        textPaint.color = lineColor        canvas.drawText(            String.format(format, minMaxLineData[0]),            x,            chartRect.bottom,            textPaint        )        canvas.drawText(            String.format(format, minMaxLineData[1]),            x,            chartRect.top,            textPaint        )        value = (minMaxLineData[1] - minMaxLineData[0]) * 0.5f + minMaxLineData[0]        canvas.drawText(            String.format(format, value),            x,            chartRect.height() * 0.5f + chartRect.top,            textPaint        )        value = (minMaxLineData[1] - minMaxLineData[0]) * 0.75f + minMaxLineData[0]        canvas.drawText(            String.format(format, value),            x,            chartRect.height() * 0.25f + chartRect.top,            textPaint        )        value = (minMaxLineData[1] - minMaxLineData[0]) * 0.25f + minMaxLineData[0]        canvas.drawText(            String.format(format, value),            x,            chartRect.height() * 0.75f + chartRect.top,            textPaint        )    }    private fun drawChartBackground(canvas: Canvas) {        if (chartRect.height() == 0f) return        val oneFourth = chartRect.height() * 0.25f + chartRect.top        val mid = (chartRect.top + chartRect.bottom) * 0.5f        val threeFourth = chartRect.height() * 0.75f + chartRect.top        canvas.drawLine(            chartRect.left,            chartRect.bottom,            chartRect.right,            chartRect.bottom,            bgPaint        )        canvas.drawLine(            chartRect.left,            chartRect.top,            chartRect.right,            chartRect.top,            bgPaint        )        canvas.drawLine(chartRect.left, oneFourth, chartRect.right, oneFourth, bgPaint)        canvas.drawLine(chartRect.left, mid, chartRect.right, mid, bgPaint)        canvas.drawLine(chartRect.left, threeFourth, chartRect.right, threeFourth, bgPaint)    }    private fun drawLineData(canvas: Canvas) {        lineDataSet.forEach{            if (it.value.size <= 1) return            linePaint.color = it.key            it.value.run {                if (this.size > 1) {                    var x: Float                    var y: Float                    val path = Path()                    for (index in this.indices) {                        x = getXAxis(index, this.size)                        y = getLineYAxis(this[index])                        if (index == 0) {                            path.moveTo(x, y)                        } else {                            path.lineTo(x, y)                        }                    }                    canvas.drawPath(path, linePaint)                } else if (this.size == 1) {                    minMaxLineData[0] = this[0] - 1                    minMaxLineData[1] = this[1] + 1                } else {                    minMaxLineData[0] = 0f                    minMaxLineData[1] = 0f                }            }        }    }    private fun getLineYAxis(fl: Float): Float {        return chartRect.bottom - (((fl - minMaxLineData[0]) / (minMaxLineData[1] - minMaxLineData[0])) * chartRect.height())    }    private fun getXAxis(index: Int, size: Int): Float {        val section = chartRect.width() / size        return chartRect.left + index * section + (section / 2f)    }}