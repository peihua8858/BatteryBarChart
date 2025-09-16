package com.android.hwsystemmanager.multicolor

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.Shader
import android.text.BidiFormatter
import android.text.TextPaint
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat
import com.android.hwsystemmanager.BatteryStatisticsHelper
import com.android.hwsystemmanager.LevelAndCharge
import com.android.hwsystemmanager.MainApplication
import com.android.hwsystemmanager.R
import com.android.hwsystemmanager.SelectedItem
import com.android.hwsystemmanager.multicolor.MultiColorPathRenderer.PointFColor
import com.android.hwsystemmanager.utils.Logcat
import com.android.hwsystemmanager.utils.ScreenReaderUtils
import com.android.hwsystemmanager.utils.TimeUtil
import com.android.hwsystemmanager.utils.argb
import com.android.hwsystemmanager.utils.createDashedPaint
import com.android.hwsystemmanager.utils.createTextPaint
import com.android.hwsystemmanager.utils.dLog
import com.android.hwsystemmanager.utils.dp2px
import com.android.hwsystemmanager.utils.isLandscape
import com.android.hwsystemmanager.utils.isLayoutRtl
import com.android.hwsystemmanager.utils.isPie
import com.android.hwsystemmanager.utils.measureTextSize
import com.android.hwsystemmanager.utils.parseInt
import java.text.NumberFormat
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class MultiColorLineChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    var f9893A: Float = 0f
    var f9894B: Float = 0f
    val batteryDataList = mutableListOf<LevelAndCharge>()
    val stackBarDataList = mutableListOf<StackBarPointData>()
    private val mPercentCoorList = mutableListOf<PercentCoordinate>()
    private val barChartTouchHelper by lazy { BarChartTouchHelper(this) }
    var mIsHalfHour = 0
    var lastIndex: Int = 0
    private var mWidth: Float = 0f
    private var mHeight: Float = 0f
    private var mEndTime: Long = 0
    private var mStartTime: Long = 0
    var dataLength: Int = 0
    var mSelectPointF: PointF = PointF()
        private set
    private val onTouchListener = TouchListenerImpl(this)

    /**
     * 柱状图X轴坐标
     */
    private var chartX: Float = 0f

    /**
     * 柱状图X轴坐标
     */
    private var chartStopX: Float = 0f
    private var chartY: Float = 0f
    private var chartStopY: Float = 0f
    private val mLineWidth: Float
    private val mBottomTextPaint: TextPaint
    private val mPrecentTextPaint: TextPaint
    private val mBottomTextWidth: Int
    private val mBottomTextHeight: Int
    var mVerticalGap: Float = 0f
        private set
    val mLeftVerticalLineWidth: Float
    private val xOffset: Float = if (isLandscape) dp2px(3f) else dp2px(2f)


    private val mHorizontalLinePaint: Paint
    private val mVerticalLinePaint: Paint


    private var pathRenderer: MultiColorPathRenderer = MultiColorPathRenderer()
    private val chartBottomPadding: Float
    private val chartHeight: Float
    private val precentTextWidth: Int
    private val precentTextHeight: Int
    private val precentTextMargin: Float

    private val chartAboveTextSize: Float

    @ColorInt
    private val chartAboveTextColor: Int

    val barBubbleEnable: Boolean
    val barBubbleBottomMargin: Float
    val barBubbleLeftMargin: Float
    val barBubbleRightMargin: Float
    val barBubbleTextSize: Float

    @ColorInt
    val barBubbleTextColor: Int

    @ColorInt
    val barBubbleBackground: Int
    val barBubbleCornerRadius: Float

    var mBarWidth: Float = 0f
        private set
    var mSelectIndex: Int = -1

    @ColorInt
    val chartLineChargeColor: Int

    @ColorInt
    val chartLineLowBatteryColor: Int

    @ColorInt
    val chartLineNoChargeColor: Int

    @ColorInt
    val chartNoSelectedBgColor: Int

    @ColorInt
    val chartNoSelectedBottomBgColor: Int
    private val chartVerticalLineVisible: Boolean

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MultiColorLineChart)
        chartHeight = typedArray.getDimension(R.styleable.MultiColorLineChart_chartHeight, 0f)
        mLineWidth = typedArray.getDimension(R.styleable.MultiColorLineChart_chartLineWidth, dp2px(1f))

        chartLineChargeColor = typedArray.getColor(R.styleable.MultiColorLineChart_chartLineChargeColor, Color.BLACK)
        chartLineLowBatteryColor = typedArray.getColor(R.styleable.MultiColorLineChart_chartLineLowBatteryColor, Color.BLACK)
        chartLineNoChargeColor = typedArray.getColor(R.styleable.MultiColorLineChart_chartLineNoChargeColor, Color.BLACK)
        chartAboveTextSize = typedArray.getDimension(R.styleable.MultiColorLineChart_chartAboveTextSize, dp2px(11f))
        chartAboveTextColor = typedArray.getColor(R.styleable.MultiColorLineChart_chartAboveTextColor, Color.BLACK)

        mVerticalGap = typedArray.getDimension(R.styleable.MultiColorLineChart_chartLineMargin, 0f)
        chartBottomPadding = typedArray.getDimension(R.styleable.MultiColorLineChart_chartBottomPadding, 0f)
        val chartHorizontalLineColor = typedArray.getColor(R.styleable.MultiColorLineChart_chartHorizontalLineColor, Color.GRAY)
        val chartHorizontalLineWidth = typedArray.getDimension(R.styleable.MultiColorLineChart_chartHorizontalLineWidth, 0f)
        chartVerticalLineVisible = typedArray.getBoolean(R.styleable.MultiColorLineChart_chartVerticalLineVisible, true)
        val chartVerticalLineColor = typedArray.getColor(R.styleable.MultiColorLineChart_chartVerticalLineColor, Color.GRAY)
        val chartVerticalLineWidth = typedArray.getDimension(R.styleable.MultiColorLineChart_chartVerticalLineWidth, 0f)
        mLeftVerticalLineWidth = if (chartVerticalLineVisible) chartVerticalLineWidth else 0f

        val rightTextColor = typedArray.getColor(R.styleable.MultiColorLineChart_chartRightLabelTextColor, Color.BLACK)
        val rightTextSize = typedArray.getDimension(R.styleable.MultiColorLineChart_chartRightLabelTextSize, dp2px(11f))
        precentTextMargin = typedArray.getDimension(R.styleable.MultiColorLineChart_chartRightLabelTextMargin, 0f)

        chartNoSelectedBgColor = typedArray.getColor(R.styleable.MultiColorLineChart_chartNoSelectedBgColor, Color.WHITE)
        chartNoSelectedBottomBgColor = typedArray.getColor(R.styleable.MultiColorLineChart_chartNoSelectedBottomBgColor, Color.WHITE)


        barBubbleEnable = typedArray.getBoolean(R.styleable.MultiColorLineChart_barBubbleEnable, true)
        barBubbleBottomMargin = typedArray.getDimension(R.styleable.MultiColorLineChart_barBubbleBottomMargin, 0f)
        barBubbleLeftMargin = typedArray.getDimension(R.styleable.MultiColorLineChart_barBubbleLeftMargin, 0f)
        barBubbleRightMargin = typedArray.getDimension(R.styleable.MultiColorLineChart_barBubbleRightMargin, 0f)
        barBubbleTextSize = typedArray.getDimension(R.styleable.MultiColorLineChart_barBubbleTextSize, dp2px(11f))
        barBubbleTextColor = typedArray.getColor(R.styleable.MultiColorLineChart_barBubbleTextColor, Color.WHITE)
        barBubbleBackground = typedArray.getColor(R.styleable.MultiColorLineChart_barBubbleBackground, Color.BLACK)
        barBubbleCornerRadius = typedArray.getDimension(R.styleable.MultiColorLineChart_barBubbleCornerRadius, 0f)

        typedArray.recycle()
        mBottomTextPaint = createTextPaint(chartAboveTextColor, chartAboveTextSize)
        mPrecentTextPaint = createTextPaint(rightTextColor, rightTextSize)
        mHorizontalLinePaint = createDashedPaint(chartHorizontalLineColor, chartHorizontalLineWidth)
        mVerticalLinePaint = createDashedPaint(chartVerticalLineColor, chartVerticalLineWidth)
        onTouchListener.isEnable = barBubbleEnable
        setOnTouchListener(onTouchListener)
        mBottomTextPaint.measureTextSize("现在").apply {
            mBottomTextWidth = this.width()
            mBottomTextHeight = this.height()
        }
        val precentFormat = NumberFormat.getPercentInstance().format(100 / 100.0)
        mPrecentTextPaint.measureTextSize(precentFormat).apply {
            precentTextWidth = this.width()
            precentTextHeight = this.height()
        }

        pathRenderer = MultiColorPathRenderer()
        pathRenderer.setStrokeWidth(mLineWidth) // 设置线宽
    }

    val endIndex: Int
        get() {
            val startIndex = startIndex
            if (this.mIsHalfHour != 1 || (startIndex != 0 && startIndex != 47)) {
                return min(dataLength - 1, (startIndex + 2) - 1)
            }
            return startIndex
        }

    val selectedTime: Long
        get() {
            val m934d: Long
            val levelAndCharge = batteryDataList.getOrNull(startIndex)
            m934d = levelAndCharge?.time ?: BatteryStatisticsHelper.m934d()
            return m934d - 1800000
        }

    val selectedTimeSpand: Long
        get() {
            if (checkBatteryLevelValidity(startIndex, endIndex) != 0) {
                return HALF_HOUR
            }
            return ONE_HOUR
        }

    //m7037d
    fun checkBatteryLevelValidity(startIndex: Int, endIndex: Int): Int {
        if (startIndex < 0) {
            return 0
        }
        if ((batteryDataList.isNotEmpty()) && startIndex > dataLength - 1) {
            return 0
        }
        var isValid = true
        val isSingleItem = startIndex == dataLength - 1
        if (batteryDataList[startIndex].level < 0 || batteryDataList[endIndex].level != 0) {
            isValid = false
        }
        if ((!isSingleItem || !isValid) && startIndex != endIndex) {
            return 0
        }
        return -1
    }

    val startIndex: Int
        get() {
            if (this.mSelectIndex != 0 && !notSelected()) {
                return min(dataLength - 1, (this.mSelectIndex * 2) - this.mIsHalfHour)
            }
            return 0
        }

    fun setSelectIndex(index: Int) {
        this.mSelectIndex = index
    }

    fun notSelected(): Boolean {
        return this.mSelectIndex == -1
    }

    private fun is24HourFormat(): Boolean {
        val z10 = this.mEndTime - this.mStartTime >= 82800000
        return z10 && !DateFormat.is24HourFormat(context)
    }

    public override fun dispatchHoverEvent(event: MotionEvent): Boolean {
        val z10: Boolean
        val barChartTouchHelper = this.barChartTouchHelper
        z10 = barChartTouchHelper.dispatchHoverEvent(event)
        return !(!z10 && !super.dispatchHoverEvent(event))
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val z10: Boolean
        val barChartTouchHelper = this.barChartTouchHelper
        z10 = barChartTouchHelper.dispatchKeyEvent(event)
        return !(!z10 && !super.dispatchKeyEvent(event))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var height = (paddingBottom + paddingTop + chartBottomPadding + mVerticalGap + chartHeight + mBottomTextHeight).roundToInt()
        if (is24HourFormat()) {
            height += (this.chartAboveTextSize * 1.5).toInt()
        }
        Logcat.d(
            TAG, "onMeasure height:$height, mHeight:$mHeight,chartHeight:$chartHeight, mBottomTextHeight:$mBottomTextHeight," +
                    "\nchartBottomPadding:$chartBottomPadding,paddingTop:$paddingTop,paddingBottom:$paddingBottom"
        )
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height)
    }

    var chartWidth: Float = 0f
    private val timeLabels = mutableListOf<TimeLabel>()
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat()
        mHeight = h.toFloat()
        chartX = (paddingStart + mLeftVerticalLineWidth)
        chartStopX = (mWidth - paddingEnd - precentTextWidth - precentTextMargin - mLeftVerticalLineWidth)
        chartY = mVerticalGap + paddingTop.toFloat()
        chartStopY = mHeight - paddingBottom - mBottomTextHeight - chartBottomPadding
        chartWidth = (chartStopX - chartX)
        calculateBarDimensions()
        processData()
        val size = dataLength
        if (size != 0 && stackBarDataList.isNotEmpty()) {
            for (i11 in 0..<size) {
                if (batteryDataList[i11].level != 0 && i11 != 0) {
                    this.lastIndex = i11
                }
            }
            val i12 = size - 1
            if (batteryDataList[i12].level != 0) {
                this.lastIndex = i12
            }
            val formatDateTime = DateUtils.formatDateTime(
                context,
                batteryDataList[0].time, 17
            )
            val formatDateTime2 = DateUtils.formatDateTime(
                context, batteryDataList[lastIndex].time, 17
            )
            val m9243i = context.getString(R.string.power_battery_choose_info)
            val format2 = String.format(
                m9243i, formatDateTime, formatDateTime2,
                stackBarDataList[0].precentLevel, stackBarDataList[lastIndex].precentLevel
            )
            contentDescription = format2
        }
        //右侧百分比坐标
        val y = chartStopY - chartY
        val x = if (isLayoutRtl) mLeftVerticalLineWidth else mWidth - mLeftVerticalLineWidth
        mPercentCoorList.add(PercentCoordinate(this, x, this.mVerticalGap, y, 100))
//        mPercentCoorList.add(PercentCoordinate(this, i15, this.mVerticalGap, i14, 75))
        mPercentCoorList.add(PercentCoordinate(this, x, this.mVerticalGap, y, 50))
//        mPercentCoorList.add(PercentCoordinate(this, i15, this.mVerticalGap, i14, 25))
        mPercentCoorList.add(PercentCoordinate(this, x, this.mVerticalGap, y, 0))
        val timeLabels = createHours2(chartX, chartStopY + chartBottomPadding)
        this.timeLabels.clear()
        this.timeLabels.addAll(timeLabels)
    }

    fun calculateBarDimensions() {
        Logcat.d(TAG, "bar isLand $isLandscape")
        val f10 = ((this.chartStopX - chartX)) / 48f
        this.mBarWidth = f10
//        this.f9911q = this.f9903i
        this.f9893A = 0.6666667f * f10
        this.f9894B = f10 * 0.33333334f
    }

    private fun processData() {
        var z12 = false
        stackBarDataList.clear()
        var i8 = -1
        var z16 = false
        var z17 = true
        var z14 = false
        val xOffset = chartX + xOffset//2dp 或3dp
        val y = chartY
        val barHeight = this.chartStopY - y
        for ((index, item) in batteryDataList.withIndex()) {
            val x = (index * this.mBarWidth) + xOffset
            Logcat.d(
                TAG,
                "processData>>>>chartStopY:$chartStopY，mBarWidth:$mBarWidth,index:$index,chartX:$chartX,chartStopX:$chartStopX,mLeftVerticalLineWidth:$mLeftVerticalLineWidth,[ x:$x,y:$y]"
            )
            val stackBarData =
                StackBarPointData(x, y, this.mBarWidth, barHeight, item)
                    .setChargeColor(chartLineChargeColor)
                    .setNoChargeColor(chartLineNoChargeColor)
                    .setLowBatteryColor(chartLineLowBatteryColor)
                    .setNoSelectedBgColor(chartNoSelectedBgColor)
                    .setNoSelectedBottomBgColor(chartNoSelectedBottomBgColor)
                    .setChartLineWidth(mLineWidth)
            stackBarData.index = index
            val nextCharge = batteryDataList.getOrNull(index + 1)
            Logcat.d(TAG, " area indexHorizontalSufficient = $i8")
            if (nextCharge != null) {
                val isCharge = item.charge == "true"
                val isNextCharge = index < 47 && nextCharge.charge == "true"
                z14 = index < 47 && nextCharge.level <= 81
                val z11 = !(!isLandscape && this.mBarWidth < dp2px(11.0f))
                z12 = (isNextCharge || z11) && isCharge
            }
            val z13 = item.level <= 81
            if (z12 && z17) {
                if (i8 == -1) {
                    i8 = index
                }
                Logcat.d(TAG, " area indexHorizontalSufficient = $i8")
                if (z13 && z14) {
                    Logcat.d(TAG, "this area should draw index = $index")
                    z17 = false
                    z16 = true
                }
            }
            stackBarDataList.add(stackBarData)
        }
        var z15 = true
        processPointF2()
        if (!notSelected()) {
            onTouchListener.updateBarStates()
        }
        if (!z16) {
            val arrayList = this.stackBarDataList
            if (i8 < 0 || i8 > arrayList.size - 1) {
                z15 = false
            }
            if (z15) {
                Logcat.d(TAG, "normal is not draw indexHorizontalSuffcient is $i8")
            }
        }
    }


    private fun processPointF2() {
        val pointFColors = mutableListOf<PointFColor>()
        for ((index, item) in stackBarDataList.withIndex()) {
            val preBar = stackBarDataList.getOrNull(index - 1) ?: stackBarDataList[0]
            val preLevel = preBar.levelAndCharge.level
            item.calculatePointF(preLevel, pointFColors)
        }
        pathRenderer.setStrokeWidth(mLineWidth)
        pathRenderer.setData(pointFColors)
        dLog { "processPointF2>>pointFColors:${pointFColors.size},lastItem:${pointFColors.last()},chartStopX:$chartStopX" }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (chartVerticalLineVisible) {
            canvas.drawLine(chartX, chartY, chartX, chartStopY, mVerticalLinePaint)
            canvas.drawLine(chartStopX, chartY, chartStopX, chartStopY, mVerticalLinePaint)
        }
        drawHorLineAndPrecent(canvas)
        //绘制曲线
        processPointF2()
        pathRenderer.draw(canvas)
        stackBarDataList.forEach {
            it.drawBar(canvas)
        }
        if (barBubbleEnable) {
            drawBarBubble(canvas)
        }
        timeLabels.forEach {
            it.drawTime(canvas)
        }
    }

    private fun drawBarBubble(canvas: Canvas) {
        val mBarLists = stackBarDataList
        var bubbleDrawn = false
        for ((index, item) in mBarLists.withIndex()) {
//            if (bubbleDrawn) {
//                break
//            }
            val nextIndex = index + 1
            // 修复：添加条件判断，确保只在需要时绘制气泡
            if (!item.showBubble || notSelected()/* || bubbleDrawn*/) {
                continue
            }
            val barDataState = item.state
            if (barDataState == 0) {
                continue
            }
            val lastIndex = mBarLists.size - 2
            var levelAndCharge = item.levelAndCharge

            if (index < lastIndex) {
                val nextItem = mBarLists[nextIndex]
                if (levelAndCharge.level < nextItem.levelAndCharge.level) {
                    levelAndCharge = nextItem.levelAndCharge
                }
            }
            val y2 = item.y
            val startX = item.x
            val startY =
                (((chartStopY - mVerticalGap) * ((100 - levelAndCharge.level).toFloat())) / (100f)) + y2
            Logcat.d(
                "BubbleView", "BubbleView>>barDataState:$barDataState,startX:$startX,startY:$startY,y2:$y2" +
                        ",chartHeight:${chartHeight},mVerticalGap:${mVerticalGap}," +
                        "levelAndCharge.level:${levelAndCharge.level}"
            )
            val bubbleStartX = when (barDataState) {
                -1 -> {
                    //起始、结束位置是半个小时时
                    startX + f9893A / 2f
                }

                1 -> {
                    startX + f9893A + f9894B / 2f
                }

                2 -> {
                    // prevLevel < curLevel
                    startX -f9894B / 2f
                }

                else -> {
                  startX
                }
            }
            Logcat.d(
                "BubbleView",
                "barDataState:$barDataState,startX:$startX,f9893A:${f9893A},f9894B:${f9894B},startY:$startY,mLineWidth:$mLineWidth"
            )
            // 向上移动气泡，避免箭头覆盖曲线
            val bubbleStartY = startY-mLineWidth/2f
            Logcat.d(
                "BubbleView",
                "bubbleStartX:$bubbleStartX,bubbleStartY:$bubbleStartY,startY:$startY,chartWidth:$chartWidth"
            )
            val point = SelectedItem(
                bubbleStartX,
                bubbleStartY,
                selectedTime,
                mWidth
            ).apply {
                this.state = checkBatteryLevelValidity(startIndex, endIndex)
            }
            val bubbleView = BubbleView(context, point)
            if (!ScreenReaderUtils.checkScreenReaderStatus()) {
                bubbleView.drawBubble(canvas)
                // 标记已绘制气泡
                bubbleDrawn = true
            }
        }
    }


    private fun drawHorLineAndPrecent(canvas: Canvas) {
        var prevY = paddingTop.toFloat()
        mPercentCoorList.forEach {
            prevY = it.draw(canvas, prevY, mLineWidth)
        }
    }

    private fun createHours2(x: Float, y: Float): ArrayList<TimeLabel> {
        val arrayList = ArrayList<TimeLabel>()
        val j10 = this.mEndTime
        val j11 = this.mStartTime
        if (j11 in 1..<j10) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = mStartTime
            calendar[Calendar.MILLISECOND] = 0
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MINUTE] = 0
            if (calendar.timeInMillis < this.mStartTime) {
                calendar[Calendar.HOUR_OF_DAY] = calendar[Calendar.HOUR_OF_DAY] + 1
            }
            val calendar2 = Calendar.getInstance()
            calendar2.timeInMillis = mEndTime
            calendar2[Calendar.MILLISECOND] = 0
            calendar2[Calendar.SECOND] = 0
            calendar2[Calendar.MINUTE] = 0
            if (calendar[Calendar.DAY_OF_YEAR] != calendar2[Calendar.DAY_OF_YEAR] || calendar[Calendar.YEAR] != calendar2[Calendar.YEAR]) {
                calendar[Calendar.HOUR_OF_DAY] = 0
                calendar[Calendar.DAY_OF_YEAR] = calendar[Calendar.DAY_OF_YEAR] + 1
            }
            Logcat.d(">>>time:${TimeUtil.formatDateTime(calendar.timeInMillis)}")
            Logcat.d(">>>time:${TimeUtil.formatDateTime(calendar.timeInMillis)}")
            val timeMillis = TimeUtil.getDateTimeMillis(System.currentTimeMillis(), 12)
            Logcat.d("timeMillis>>>time0:${TimeUtil.formatDateTime(timeMillis)}")

            val nowTimeMillis = TimeUtil.getDateTimeMillis(System.currentTimeMillis())
            Logcat.d("nowTimeMillis>>>time0:${TimeUtil.formatDateTime(nowTimeMillis)}")
            val nowAfterHalfHourTimeMillis = TimeUtil.getDateTimeMillis(System.currentTimeMillis()+ HALF_HOUR)
            Logcat.d("nowAfterHalfHourTimeMillis>>>time0:${TimeUtil.formatDateTime(nowAfterHalfHourTimeMillis)}")


            val earlyMorningTime = calendar.timeInMillis

            val step = ONE_HOUR * 3L
            val calendar4 = Calendar.getInstance()
            val gad = ((chartStopX - chartX) / 8f).toInt()

            for ((index, item) in (mStartTime..mEndTime step step).withIndex()) {
                calendar4.clear()
                calendar4.timeInMillis = item
                calendar4[Calendar.MILLISECOND] = 0
                calendar4[Calendar.SECOND] = 0
                calendar4[Calendar.MINUTE] = 0
                Logcat.d("calendar4>>>time1:${TimeUtil.formatDateTime(calendar4.timeInMillis)}")
                var startX = x + index * gad
                val timeText =
                    when (calendar4.timeInMillis) {
                        nowTimeMillis, nowAfterHalfHourTimeMillis -> "现在"
                        earlyMorningTime -> "凌晨"
                        timeMillis -> "12点"
                        else -> TimeUtil.formatHourTime(item)
                    }
                startX += if (index == 8) -(mBottomTextWidth / 2).toFloat() else 0f
                arrayList.add(TimeLabel(mBottomTextPaint, startX, y + mBottomTextHeight / 3f, timeText))
            }
        }
        return arrayList
    }

    private fun createHours(): ArrayList<String> {
        val arrayList = ArrayList<String>()
        val j10 = this.mEndTime
        val j11 = this.mStartTime
        if (j11 in 1..<j10) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = mStartTime
            calendar[Calendar.MILLISECOND] = 0
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MINUTE] = 0
            if (calendar.timeInMillis < this.mStartTime) {
                calendar[Calendar.HOUR_OF_DAY] = calendar[Calendar.HOUR_OF_DAY] + 1
            }
            val calendar2 = Calendar.getInstance()
            calendar2.timeInMillis = mEndTime
            calendar2[Calendar.MILLISECOND] = 0
            calendar2[Calendar.SECOND] = 0
            calendar2[Calendar.MINUTE] = 0
            if (calendar[Calendar.DAY_OF_YEAR] != calendar2[Calendar.DAY_OF_YEAR] || calendar[Calendar.YEAR] != calendar2[Calendar.YEAR]) {
                calendar[Calendar.HOUR_OF_DAY] = 0
                calendar[Calendar.DAY_OF_YEAR] = calendar[Calendar.DAY_OF_YEAR] + 1
            }
            Logcat.d(">>>time:${TimeUtil.formatDateTime(calendar.timeInMillis)}")
            val earlyMorningTime = TimeUtil.getDateTimeMillis(System.currentTimeMillis(),0)
            val time12 = TimeUtil.getDateTimeMillis(System.currentTimeMillis(), 12)
            Logcat.d("calendar3>>>time0:${TimeUtil.formatDateTime(time12)}")

            val nowTimeMillis = TimeUtil.getDateTimeMillis(System.currentTimeMillis())
            Logcat.d("newCalendar>>>time0:${TimeUtil.formatDateTime(nowTimeMillis)}")
            val nowAfterHalfHourTimeMillis = TimeUtil.getDateTimeMillis(System.currentTimeMillis()+ HALF_HOUR)
            Logcat.d("newCalendar1>>>time0:${TimeUtil.formatDateTime(nowAfterHalfHourTimeMillis)}")
            val step = ONE_HOUR * 3L
            val calendar4 = Calendar.getInstance()
            for (i in mStartTime..mEndTime step step) {
//                val curCharge = Calendar.getInstance()
//                calendar.timeInMillis = i

                calendar4.clear()
                calendar4.timeInMillis = i
                calendar4[Calendar.MILLISECOND] = 0
                calendar4[Calendar.SECOND] = 0
                calendar4[Calendar.MINUTE] = 0
                Logcat.d("calendar4>>>time1:${TimeUtil.formatDateTime(calendar4.timeInMillis)}")
                if (calendar4.timeInMillis == nowTimeMillis || calendar4.timeInMillis == nowAfterHalfHourTimeMillis) {
                    arrayList.add("现在")
                }else if (calendar4.timeInMillis == nowTimeMillis || calendar4.timeInMillis == nowAfterHalfHourTimeMillis) {
                    arrayList.add("凌晨")
                } else if (calendar4.timeInMillis == time12) {
                    arrayList.add("12点")
                } else
                    arrayList.add(TimeUtil.formatHourTime(i))
            }
        }
//        val calendar = Calendar.getInstance()
        return arrayList
    }

    // Method to set custom data
    fun setData(data: List<LevelAndCharge>) {
        this.batteryDataList.clear()
        this.batteryDataList.addAll(data)
        dataLength = data.size
        this.mIsHalfHour = 1
//        val currentTimeMillis = System.currentTimeMillis()
//        val endTime =
//            if (dataLength <= 0) getHalfTime(currentTimeMillis) + (currentTimeMillis - HALF_HOUR) else data.last().time
//        val size: Int = 48 - dataLength
//        if (dataSize <= 48 && 1 <= size) {
//            var index = 1
//            while (true) {
//                batteryDataList.add(LevelAndCharge(0, "false", (index * HALF_HOUR) + endTime))
//                if (index == size) {
//                    break
//                } else {
//                    index++
//                }
//            }
//        }
        this.mEndTime = getEndTime()
        if (batteryDataList.isNotEmpty()) {
            val firstItem = batteryDataList.first()
            val firstTime = firstItem.time
            val context = context
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = firstTime
            val formatDateTime =
                DateUtils.formatDateTime(context, calendar.timeInMillis, DateUtils.FORMAT_SHOW_TIME)
            val startIndex = formatDateTime.indexOf(':') + 1
            val substring = formatDateTime.substring(startIndex, startIndex + 2)
            val res = substring.toInt()
            if (res != 0) {
                this.mIsHalfHour = 0
            }
            this.mStartTime = firstItem.time - 1800000L
        } else {
            this.mStartTime = this.mEndTime - dataLength * 1800000L
        }
        ViewCompat.setAccessibilityDelegate(this, barChartTouchHelper)
        dLog { "endTime = " + this.mEndTime }
        requestLayout()
    }

    val clickPointDescription: String
        get() {
            var selectedTime = selectedTime
            val str = stackBarDataList[startIndex].precentLevel
            val str2 = stackBarDataList[endIndex].precentLevel
            val m7037d = checkBatteryLevelValidity(startIndex, endIndex)
            var j10 = ONE_HOUR + selectedTime
            val calendar = Calendar.getInstance()
            if (m7037d != -1) {
                if (m7037d == 1) {
                    selectedTime += HALF_HOUR
                }
            } else {
                j10 -= HALF_HOUR
            }
            calendar.timeInMillis = selectedTime
            val formatDateTime = DateUtils.formatDateTime(
                context,
                calendar.timeInMillis,
                1
            )
            val str3 = DateUtils.formatDateTime(
                context,
                calendar.timeInMillis,
                16
            ) + formatDateTime
            calendar.timeInMillis = j10
            val formatDateTime2 = DateUtils.formatDateTime(
                context,
                calendar.timeInMillis,
                1
            )
            val m9243i =
                context.getString(R.string.power_battery_choose_info)
            val text = String.format(m9243i, str3, formatDateTime2, str, str2)
            return BidiFormatter.getInstance().unicodeWrap(text)
        }

    companion object {
        const val TAG = "MultiColorLineChart"

        /**
         * 1分钟
         */
        const val ONE_MINUTE: Long = 60 * 1000

        /**
         * 30分钟
         */
        const val HALF_HOUR: Long = ONE_MINUTE * 30

        /**
         * 1小时
         */
        const val ONE_HOUR: Long = HALF_HOUR * 2

        /**
         * 24小时
         */
        const val HOUR_24: Long = 24 * ONE_HOUR

        /**
         * 23小时
         */
        const val HOUR_23: Long = 23 * ONE_HOUR

        fun getEndTime(): Long {
            val currentTimeMillis = System.currentTimeMillis()
            Logcat.d(TAG, "currentTimeMillis:$currentTimeMillis")
            if (parseMinute(currentTimeMillis) >= 30) {
                return getHalfTime(currentTimeMillis) + currentTimeMillis
            }
            return (currentTimeMillis - HALF_HOUR) + getHalfTime(currentTimeMillis)
        }

        fun getHalfTime(timeMillis: Long): Long {
            Logcat.d(TAG, "getHalfTime:$timeMillis")
            val minute = parseMinute(timeMillis)
            if (minute != 0 && minute != 30) {
                val halfTime = ((30 - (minute % 30)) * ONE_MINUTE)
                Logcat.d(TAG, "getHalfTime changed: $halfTime")
                return halfTime
            }
            Logcat.d(TAG, "time is not changed: $timeMillis")
            return HALF_HOUR
        }

        private fun parseMinute(timeMillis: Long): Int {
            val context = MainApplication.context
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeMillis
            val formatDateTime =
                DateUtils.formatDateTime(context, calendar.timeInMillis, DateUtils.FORMAT_SHOW_TIME)
            val startIndex = formatDateTime.indexOf(':') + 1
            val substring = formatDateTime.substring(startIndex, startIndex + 2)
            return substring.parseInt(0)
        }

        /**
         * 检查指定索引的电池数据与前一个电池数据的充电值是否不同
         *
         * @param index 当前电池数据的索引
         * @param barData 包含所有电池数据的列表
         * @return 如果当前电池数据的充电值与前一个电池数据的充电值不同，则返回 true，否则返回 false。
         */
        @JvmStatic
        fun isDifferentFromPreviousCharge(index: Int, barData: List<StackBarPointData>): Boolean {
            return barData[index].levelAndCharge.charge != barData[index - 1].levelAndCharge.charge
        }

        /**
         * 判断后一个柱状图是否和当前[index]柱状图 charge 值是否相等
         */
        @JvmStatic
        fun isDifferentFromNextCharge(index: Int, barData: List<StackBarPointData>): Boolean {
            return barData[index].levelAndCharge.charge != barData[index + 1].levelAndCharge.charge
        }
    }

    class TimeLabel(private val paint: TextPaint, val timePosition: Float, private val y: Float, val label: String) {
        val labelHeight: Float
        val labelWidth: Float

        init {
            val rect = Rect()
            paint.getTextBounds(label, 0, label.length, rect)
            val height = rect.height()
            this.labelWidth = rect.width().toFloat()
            this.labelHeight = (height / 2f) + y
        }

        fun drawTime(canvas: Canvas) {
            dLog { "TimeLabel>>drawTime:$label,timePosition:$timePosition,y:$y,labelHeight:$labelHeight" }
            canvas.drawText(label, timePosition, labelHeight, paint)
        }
    }

    //右侧百分比坐标数据
    class PercentCoordinate(
        private val hostView: MultiColorLineChart,
        i4: Float,
        gad: Float,
        height: Float,
        value: Int,
    ) {
        private val x: Float
        private val y: Float
        private val percent: String
        private val textPaint = hostView.mPrecentTextPaint
        private val textWidth = hostView.precentTextWidth
        private val textHeight = hostView.precentTextHeight

        init {
            val format = NumberFormat.getPercentInstance().format(value / 100.0)
            this.percent = format
            this.x = if (isLayoutRtl) i4 else i4 - textWidth.toFloat()
            this.y = (textHeight / 2f) + ((gad + height) - ((value * height) / 100))
        }

        fun draw(canvas: Canvas, prevY: Float, linewidth: Float): Float {
            if (abs(y - prevY) >= textHeight + linewidth) {
                canvas.drawText(percent, x, y, textPaint)
                val lineY = y - (textHeight / 2f)
                val lineStopX =
                    hostView.mWidth - hostView.mLeftVerticalLineWidth - textWidth - hostView.precentTextMargin
                canvas.drawLine(hostView.chartX, lineY, lineStopX, lineY, hostView.mHorizontalLinePaint)
            }
            return y
        }
    }

    interface OnSlideListener {
        fun onSlide(isDoNotIntercept: Boolean)
    }

    interface OnCallBack {
        fun onCallback(selectedTime: Long, selectedTimeSpand: Long, isReset: Boolean)
    }

    var onSlideListener: OnSlideListener? = null
        private set

    fun setOnSlideListener(onSlideListener: OnSlideListener) {
        this.onSlideListener = onSlideListener
    }

    var onCallBack: OnCallBack? = null
        private set

    fun setOnCallBack(onCallBack: OnCallBack) {
        this.onCallBack = onCallBack
    }
}

data class Point(val startX: Float, val startY: Float, val stopX: Float, val stopY: Float)
