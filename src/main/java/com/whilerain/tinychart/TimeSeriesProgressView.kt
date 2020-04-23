package com.whilerain.tinychartimport android.content.Contextimport android.graphics.Canvasimport android.graphics.Colorimport android.graphics.Paintimport android.text.TextPaintimport android.util.AttributeSetimport com.whilerain.tinychart.utils.UiUtilopen class TimeSeriesProgressView @JvmOverloads constructor(    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ChartView(context, attrs, defStyleAttr) {    /**     * The max num of data show in thie chart view, this avoids too much data show in a chart     */    private val MAX_DATA_SIZE: Int = 100    /**     * Keep the originData for dynamically split show data into small pieces     */    private lateinit var originData: List<Float>    /**     * The animation percent float value between 0 to 1     */    private var percent: Float = 0f    /**     * The indicator position after separates original data into pieces     */    private var indicatorPositionPercent = 0f    /**     * The value of current progress in the list     */    private var curValue = 0f    var progressColor: Int = Color.LTGRAY        set(value) {            field = value            paint.color = value        }    private val paint = Paint().apply {        style = Paint.Style.STROKE        strokeWidth = UiUtil.dpToPx(1).toFloat()    }    private val textPaint = TextPaint().apply {        textSize = UiUtil.dpToPx(12).toFloat()        setBackgroundResource(R.drawable.shape_background_dark)    }    override fun loadLineData(data: List<Float>) {        if(data.isEmpty()) {            originData = listOf()            super.loadLineData(originData)        }else{            originData = data            super.loadLineData(getRangedData())        }    }    private fun getRangedData(): List<Float> {        when {            originData.size > MAX_DATA_SIZE -> {                val index = ((originData.size - 1) * percent).toInt()                var min = Math.min(index - (MAX_DATA_SIZE / 2), Math.max(0, (originData.size - MAX_DATA_SIZE)))                min = if (min < 0) 0 else min                var max = min + (MAX_DATA_SIZE)                max = if (max > originData.size - 1) originData.size - 1 else max                indicatorPositionPercent = (index - min).toFloat() / MAX_DATA_SIZE                curValue = originData[index]                return originData.subList(min, max)            }            else -> {                return originData            }        }    }    init {        if (attrs != null) {            context.theme.obtainStyledAttributes(                attrs,                R.styleable.TimeSeriesProgressView,                0, 0            ).apply {                progressColor =                    getColor(R.styleable.TimeSeriesProgressView_progressColor, Color.LTGRAY)                val strokeWidth = getDimension(                    R.styleable.TimeSeriesProgressView_lineWidth,                    UiUtil.dpToPx(1).toFloat()                )                paint.strokeWidth = strokeWidth            }        }    }    fun setup(tag: String, color: Int) {        this.tag = tag        lineColor = color    }    /**     * Update the progress value     * @param p which is between 0 to 1     */    fun onProgressUpdate(p: Float) {        if (p > 1) throw IllegalArgumentException("p should be from 0 to 1")        percent = p        super.loadLineData(getRangedData())        invalidate()    }    override fun onDraw(canvas: Canvas) {        drawProgressLine(canvas)        super.onDraw(canvas)    }    private fun drawProgressLine(canvas: Canvas) {        textPaint.color= lineColor        val x = (chartRect.width() * indicatorPositionPercent) + chartRect.left        val textShift = textPaint.measureText("00")        canvas.drawLine(x, chartRect.top, x, chartRect.bottom, paint)        canvas.drawText("$curValue", x, textShift, textPaint)    }}