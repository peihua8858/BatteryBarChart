package com.android.hwsystemmanager.multicolor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Shader
import android.text.BidiFormatter
import android.text.TextPaint
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.view.ViewCompat
import com.android.hwsystemmanager.BatteryStatisticsHelper
import com.android.hwsystemmanager.LevelAndCharge
import com.android.hwsystemmanager.MainApplication
import com.android.hwsystemmanager.R
import com.android.hwsystemmanager.multicolor.MultiColorPathRenderer.PointFColor
import com.android.hwsystemmanager.utils.Logcat
import com.android.hwsystemmanager.utils.TimeUtil
import com.android.hwsystemmanager.utils.createDashedPaint
import com.android.hwsystemmanager.utils.dLog
import com.android.hwsystemmanager.utils.dp2px
import com.android.hwsystemmanager.utils.getDimension
import com.android.hwsystemmanager.utils.getDimensionPixelSize
import com.android.hwsystemmanager.utils.isLandscape
import com.android.hwsystemmanager.utils.isLayoutRtl
import com.android.hwsystemmanager.utils.isPie
import com.android.hwsystemmanager.utils.measureTextSize
import com.android.hwsystemmanager.utils.parseInt
import java.text.NumberFormat
import java.util.Calendar
import kotlin.math.abs

class MultiColorLineChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    private val batteryDataList = mutableListOf<LevelAndCharge>()
    val stackBarDataList = mutableListOf<StackBarPointData>()
    private val mPercentCoorList = mutableListOf<PercentCoordinate>()
    private val barChartTouchHelper by lazy { BarChartTouchHelper(this) }
    var mIsHalfHour = 0
    var lastIndex: Int = 0
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var mEndTime: Long = 0
    private var mStartTime: Long = 0

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
    private val mLineWidth = getDimensionPixelSize(R.dimen.battery_history_chart_linewidth)
    private val mBottomTextPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = Color.DKGRAY
        this.textSize = getDimension(R.dimen.battery_history_chart_dateText_size)
        this.textAlign = Paint.Align.CENTER
    }
    private val mPrecentTextPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = Color.DKGRAY
        this.textSize = getDimension(R.dimen.battery_history_chart_dateText_size)
//        this.textAlign = Paint.Align.CENTER
    }
    private val mBottomTextWidth: Int
    private var mVerticalGap: Float = 0f
    private val mLeftVerticalLineWidth = dp2px(if (isLandscape) 3f else 2f)


    // Initialize grid paint
    private val hDashedLinePaint: Paint = createDashedPaint(
        getDimension(R.dimen.battery_history_chart_y_line_size_card),
        R.color.charge_rect_color
    )


    private var pathRenderer: MultiColorPathRenderer = MultiColorPathRenderer()
    private val bottomPadding = getDimensionPixelSize(R.dimen.battery_history_chart_bottom_padding)
    private val chartHeight = getDimensionPixelSize(R.dimen.battery_chart_height)
    private val precentTextWidth: Int
    private val precentTextHeight: Int
    private val precentTextMargin: Int = getDimensionPixelSize(R.dimen.battery_maigin_text_percent)
    private val chartAboveTextSize =
        getDimensionPixelSize(R.dimen.battery_history_chart_aboveTimeText_size)
    private val barBubbleTopMargin = getDimension(R.dimen.margin_bar_top_bubble)
    private var mBarWidth: Float = 0f
    var mSelectIndex: Int = -1

    init {
        mBottomTextPaint.measureTextSize("12点").apply {
            mBottomTextWidth = this.width()
        }
        val precentFormat = NumberFormat.getPercentInstance().format(100 / 100.0)
        mPrecentTextPaint.measureTextSize(precentFormat).apply {
            precentTextWidth = this.width()
            precentTextHeight = this.height()
        }

        pathRenderer = MultiColorPathRenderer()
        pathRenderer.setStrokeWidth(dp2px(1f)) // 设置线宽
    }

    val endIndex: Int
        get() {
            val startIndex = startIndex
            if (this.mIsHalfHour != 1 || (startIndex != 0 && startIndex != 47)) {
                return (startIndex + 2) - 1
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
            if (m7037d(startIndex, endIndex) != 0) {
                return HALF_HOUR
            }
            return ONE_HOUR
        }

    fun m7037d(startIndex: Int, endIndex: Int): Int {
        if (startIndex < 0) {
            return 0
        }
        var z11 = true
        if ((batteryDataList.isNotEmpty()) && startIndex > batteryDataList.size - 1) {
            return 0
        }
        val isOneItem = startIndex == batteryDataList.size - 1
        if (batteryDataList[startIndex].level < 0 || batteryDataList[endIndex].level != 0) {
            z11 = false
        }
        if ((!isOneItem || !z11) && startIndex != endIndex) {
            return 0
        }
        return -1
    }

    val startIndex: Int
        get() {
            if (this.mSelectIndex != 0 && !notSelected()) {
                return (this.mSelectIndex * 2) - this.mIsHalfHour
            }
            return 0
        }

    private fun setSelectIndex(index: Int) {
        this.mSelectIndex = index
    }

    fun notSelected(): Boolean {
        return this.mSelectIndex == -1
    }

    private fun is24HourFormat(): Boolean {
        val z10 = this.mEndTime - this.mStartTime >= 82800000
        return z10 && !DateFormat.is24HourFormat(context)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var height =
            (this.chartHeight + this.bottomPadding + barBubbleTopMargin + paddingTop + paddingBottom).toInt()
        if (is24HourFormat()) {
            height += (this.chartAboveTextSize * 1.5).toInt()
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w - paddingStart - paddingEnd
        mHeight = h - paddingTop - paddingBottom - bottomPadding
        mVerticalGap = mHeight / 5f
        chartX = (paddingStart + mLeftVerticalLineWidth)
        chartStopX =
            (mWidth - paddingEnd - precentTextWidth - precentTextMargin - mLeftVerticalLineWidth)
        chartY = barBubbleTopMargin
        chartStopY = (mHeight).toFloat()
        m7041j()
        processData()
        val size = batteryDataList.size
        if (size != 0 && stackBarDataList.size != 0) {
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
        val i14 = this.mHeight - mVerticalGap
        val i15 = if (isLayoutRtl) mLeftVerticalLineWidth else mWidth - mLeftVerticalLineWidth
        mPercentCoorList.add(PercentCoordinate(this, i15, this.mVerticalGap, i14, 100))
        mPercentCoorList.add(PercentCoordinate(this, i15, this.mVerticalGap, i14, 75))
        mPercentCoorList.add(PercentCoordinate(this, i15, this.mVerticalGap, i14, 50))
        mPercentCoorList.add(PercentCoordinate(this, i15, this.mVerticalGap, i14, 25))
        mPercentCoorList.add(PercentCoordinate(this, i15, this.mVerticalGap, i14, 0))
    }

    fun m7041j() {
        Logcat.d(TAG, "bar isLand $isLandscape")
        val f10 = ((this.chartStopX - chartX)) / 48f
        this.mBarWidth = f10
//        this.f9911q = this.f9903i
//        this.f9893A = 0.6666667f * f10
//        this.f9894B = f10 * 0.33333334f
    }

    private fun processData() {
        var z12 = false
        stackBarDataList.clear()
        var i8 = -1
        var z16 = false
        var z17 = true
        var z14 = false
        for ((index, item) in batteryDataList.withIndex()) {
            val f10 = chartX + this.mLeftVerticalLineWidth//2dp 或3dp
            val f11 = this.mBarWidth
            val f12 = (index * f11) + f10
            val f13 = this.mVerticalGap
            val f14 = this.mHeight.toFloat() - f13
            Logcat.d(TAG, ">>>>mHeight:$mHeight,[ x:$f12,y:$f13]")
            val preLevel =
                if (index == 0) {
                    batteryDataList[1]
                } else {
                    batteryDataList[index - 1]
                }
            val stackBarData =
                StackBarPointData(f12/*x*/, f13/*y*/, f11/*width*/, f14/*height*/, item, preLevel)
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
                    Logcat.d(TAG, "this area should draw i = $index")
                    z17 = false
                    z16 = true
                }
            }
            stackBarDataList.add(stackBarData)
        }
        var z15 = true
        processPointF()
        if (!notSelected()) {
            m7035b()
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

    private fun processPointF() {
        val mBarLists = stackBarDataList
        val f9902h = stackBarDataList.size
        val pointFColors = mutableListOf<MultiColorPathRenderer.PointFColor>()
        val pointFs = mutableListOf<PointF>()
        for ((index, item) in stackBarDataList.withIndex()) {
            val curLevelCharge = item.levelAndCharge
            val nextIndex = index + 1
            var cornerType: Int
            when {
                index == 0 -> {
                    val nextItem = stackBarDataList[nextIndex]
                    if (notSelected()) {
                        cornerType = if (f9902h <= 1) {
                            4
                        } else {
                            if (curLevelCharge.charge == nextItem.levelAndCharge.charge) {
                                1
                            } else {
                                4
                            }
                        }
                    } else {
                        cornerType = if (f9902h <= 1 || mIsHalfHour == 1) {
                            4
                        } else {
                            if (curLevelCharge.charge == nextItem.levelAndCharge.charge) {
                                1
                            } else {
                                4
                            }
                        }
                    }
                }

                index == f9902h - 1 -> {
                    if (notSelected()) {
                        val prevItem = stackBarDataList[index - 1]
                        cornerType = if (curLevelCharge.charge == prevItem.levelAndCharge.charge) {
                            3
                        } else {
                            4
                        }
                    } else {
                        //>>Lb8
                        cornerType = if (startIndex == index && selectedTimeSpand == 1800000L) {
                            4
                        } else if (endIndex == index && selectedTimeSpand == 3600000L
                            && isDifferentFromPreviousCharge(index, mBarLists)
                        ) {
                            4
                        } else if (endIndex == index - 1 && f9902h - endIndex == 2) {
                            4
                        } else {
                            3
                        }
                    }
                }

                index < mBarLists.size -> {
                    //L103>L10d
                    if (notSelected()) {
                        //未选中时
                        cornerType = if (index != mBarLists.size - 1) {
                            if (isDifferentFromPreviousCharge(index, mBarLists)
                                && isDifferentFromNextCharge(index, mBarLists)
                            ) {
                                4
                            } else if (isDifferentFromPreviousCharge(index, mBarLists)) {
                                1
                            } else if (isDifferentFromNextCharge(index, mBarLists)) {
                                3
                            } else {
                                2
                            }
                        } else {
                            3
                        }
                    } else {
                        //L13b
                        val startIndex = startIndex
                        val endIndex = endIndex
                        cornerType =
                            if (index == startIndex && isDifferentFromNextCharge(
                                    index,
                                    mBarLists
                                )
                            ) {
                                4
                            } else if (endIndex == index && isDifferentFromPreviousCharge(
                                    index,
                                    mBarLists
                                )
                            ) {
                                4
                            } else if (endIndex + 1 == index || startIndex == index) {
                                1
                            } else if (f9902h - 1 == index || startIndex - 1 == index || endIndex == index) {
                                3
                            } else {
                                2
                            }
                    }
                }

                else -> cornerType = 1
            }

            if (cornerType == 1 || cornerType == 4) {
                pointFs.clear()
            }

            if (index == 0 && f9902h > 0) {
                item.calculatePoint(
                    pointFs,
                    curLevelCharge.level,
                    cornerType, index
                )
            }

            if (index in 1..<f9902h) {
                val prevItem = mBarLists[index - 1]
                item.calculatePoint(pointFs, prevItem.levelAndCharge.level, cornerType, index)
            }

            when (cornerType) {
                1, 3, 4 -> {
                    pointFs.forEach {
                        pointFColors.add(PointFColor(it.x, it.y, item.drawLineColor))
                    }
                }
            }


        }
        dLog { "pointFColors:${pointFColors.size}" }
        pathRenderer.setData(pointFColors)
    }

    fun m7035b() {
        for ((index, item) in stackBarDataList.withIndex()) {
//            if (this.mSelectIndex == (this.mIsHalfHour + index) / 2) {
//                item.f18276h = 1
//                if (index == endIndex) {
//                    Logcat.d(TAG, "j = $index ")
//                    if (this.mIsHalfHour == 1 && (index == 0 || index == 47)) {
//                        item.f18278j = -1
//                    } else {
//                        val nextIndex = index - 1
//                        val nextBar = stackBarDataList[nextIndex]
//                        Logcat.d(
//                            TAG,
//                            item.levelAndCharge.level.toString() + " " + item.levelAndCharge.level
//                        )
//                        if (item.levelAndCharge.level != 0 && nextBar.levelAndCharge.level != 0) {
//                            if (item.levelAndCharge.level >= nextBar.levelAndCharge.level) {
//                                item.f18278j = 2
//                                nextBar.f18278j = 0
//                            } else {
//                                nextBar.f18278j = 1
//                                item.f18278j = 0
//                            }
//                        } else if (item.levelAndCharge.level == 0) {
//                            nextBar.f18278j = -1
//                        } else if (nextBar.levelAndCharge.level == 0) {
//                            item.f18278j = -1
//                        } else {
//                            Logcat.d(TAG, "invalid")
//                        }
//                    }
//                }
//                item.showBubble = true
//            } else {
//                item.f18276h = 0
//                item.showBubble = false
//            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        drawGrid(canvas)
//        drawAxes(canvas)
        canvas.drawLine(
            chartX, chartY, chartX,
            chartStopY, hDashedLinePaint
        )
        canvas.drawLine(
            chartStopX, chartY, chartStopX,
            chartStopY, hDashedLinePaint
        )
        drawHorLineAndPrecent(canvas)
        //绘制曲线
        pathRenderer.draw(canvas)
        stackBarDataList.forEach {
            it.drawBar(canvas)
        }
        drawBottomLabels(canvas, chartX, mWidth)
    }


    private fun drawHorLineAndPrecent(canvas: Canvas) {
        var prevY = paddingTop.toFloat()
        mPercentCoorList.forEach {
            prevY = it.draw(canvas, prevY, mLineWidth)
        }
    }

    private fun drawBottomLabels(canvas: Canvas, x: Float, chartWidth: Int) {
        // Draw chart title
        val textPaint = this.mBottomTextPaint
        val hours = createHours()
        val gad = ((chartStopX - chartX) / 8f).toInt()
        for ((index, item) in hours.withIndex()) {
            dLog { "x:$x,index:$index,gad:$gad,chartStopX:$chartStopX" }
            var textWidth: Int
            mBottomTextPaint.measureTextSize(item).apply {
                textWidth = this.width()
            }
            canvas.drawText(
                item,
                x + index * gad +if(index == 0) textWidth / 2 else 0,
                (this.mHeight + bottomPadding).toFloat(),
                textPaint
            )
        }
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
            Logcat.d(">>>time:${TimeUtil.formatTime(calendar.timeInMillis)}")
            val earlyMorningTime = calendar.timeInMillis
            val calendar3 = Calendar.getInstance()
            calendar3.timeInMillis = System.currentTimeMillis()
            calendar3[Calendar.MILLISECOND] = 0
            calendar3[Calendar.SECOND] = 0
            calendar3[Calendar.MINUTE] = 0
            calendar3[Calendar.HOUR_OF_DAY] = 12
            Logcat.d("calendar3>>>time0:${TimeUtil.formatTime(calendar3.timeInMillis)}")

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
                Logcat.d("calendar3>>>time1:${TimeUtil.formatTime(i)}")
                if (calendar4.timeInMillis == calendar3.timeInMillis) {
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
        val currentTimeMillis = System.currentTimeMillis()
        val dataSize = data.size
        val endTime =
            if (dataSize <= 0) getHalfTime(currentTimeMillis) + (currentTimeMillis - HALF_HOUR) else data.last().time
        this.mIsHalfHour = 1
        val size: Int = 48 - dataSize
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
            this.mStartTime = this.mEndTime - dataSize * 1800000L
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
            val m7037d = m7037d(startIndex, endIndex)
            var j10 = 3600000 + selectedTime
            val calendar = Calendar.getInstance()
            if (m7037d != -1) {
                if (m7037d == 1) {
                    selectedTime += 1800000
                }
            } else {
                j10 -= 1800000
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
        private const val TAG = "MultiColorLineChart"

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

    //右侧百分比坐标数据
    class PercentCoordinate(
        private val hostView: MultiColorLineChart,
        i4: Float,
        i8: Float,
        i9: Float,
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
            this.y = (textHeight / 2f) + ((i8 + i9) - ((value * i9) / 100))
        }

        fun draw(canvas: Canvas, prevY: Float, linewidth: Int): Float {
            if (abs(y - prevY) >= textHeight + linewidth) {
                canvas.drawText(percent, x, y, textPaint)
                val lineY = y - (textHeight / 2f)
                val lineStopX =
                    hostView.mWidth - hostView.mLeftVerticalLineWidth - textWidth - hostView.precentTextMargin
                canvas.drawLine(hostView.chartX, lineY, lineStopX, lineY, hostView.hDashedLinePaint)
            }
            return y
        }
    }
}

data class StackBarPointData(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val levelAndCharge: LevelAndCharge,
    val preCharge: LevelAndCharge,
) {
    var showBubble: Boolean = false
    var index = -1
    var charge: String = ""
    var state: Int = 1
    var barWidth: Float = 0f
    var barOffset: Float = 0f
    val precentLevel: String
    private var mBarPath: Path = Path()
    private var mDrawLineColor: Int = 0
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val noChargeLineColor50: Int
    private val noChargeLineColor10: Int
    private val chargeLineColor50: Int
    private val chargeLineColor10: Int
    private val noChargeLineNewColor: Int
    private val chargeLineNewColor: Int

    private val lowBatteryLineColor50: Int
    private val lowBatteryLineColor10: Int
    private val lowBatteryLineNewColor: Int
    private val noSelectedBgColor: Int
    private val noSelectedBottomBgColor: Int

    init {
        val level = levelAndCharge.level / 100.0
        precentLevel = NumberFormat.getPercentInstance().format(level)
        charge = levelAndCharge.charge
        barWidth = (2f * width) / 3f
        barOffset = width / 3f
        val context = MainApplication.context
        noChargeLineColor50 = context.getColor(
            if (isPie) R.color.battery_no_charge_line_alpha50_card
            else R.color.battery_no_charge_line_alpha50
        )
        noChargeLineColor10 = context.getColor(
            if (isPie) R.color.battery_no_charge_line_alpha10_card
            else R.color.battery_no_charge_line_alpha10
        )
        chargeLineColor50 = context.getColor(
            if (isPie) R.color.battery_charge_line_alpha50_card
            else R.color.battery_charge_line_alpha50
        )
        chargeLineColor10 = context.getColor(
            if (isPie) R.color.battery_charge_line_alpha10_card
            else R.color.battery_charge_line_alpha10
        )
        noChargeLineNewColor = context.getColor(
            if (isPie) R.color.battery_no_charge_line_new_card
            else R.color.battery_no_charge_line_new
        )
        chargeLineNewColor = context.getColor(
            if (isPie) R.color.battery_charge_line_new_card
            else R.color.battery_charge_line_new
        )
        lowBatteryLineColor10 = context.getColor(
            if (isPie) R.color.battery_low_battery_line_alpha10_card
            else R.color.battery_low_battery_line_alpha10
        )
        lowBatteryLineColor50 = context.getColor(
            if (isPie) R.color.battery_low_battery_line_alpha50_card
            else R.color.battery_low_battery_line_alpha50
        )
        lowBatteryLineNewColor = context.getColor(
            if (isPie) R.color.battery_low_battery_line_new_card
            else R.color.battery_low_battery_line_new
        )
        noSelectedBgColor = context.getColor(R.color.battery_not_select_bg)
        noSelectedBottomBgColor = context.getColor(R.color.battery_not_select_bg_bottom_card)
        changePainColor()
    }

    fun calculatePoint(pointFs: MutableList<PointF>, preLevel: Int, type: Int, index: Int) {
        val levelData = levelAndCharge
        val level = levelData.level
        val emptySpacePercent = 100 - level
        val emptySpaceHeight = emptySpacePercent * height / 100f
        val yStart = y + emptySpaceHeight

        val param3Percent = 100 - preLevel
        val param3Height = param3Percent * height / 100f
        val yParam3 = y + param3Height

        val halfWidth = barOffset / 2f
        val xLeft = x - halfWidth
        val xRight = x + width - halfWidth
        Logcat.d(
            "BatteryStackBarData",
            "x:$x,y:$y,width:$width,height:$height,preLevel:$preLevel,level is $level,index:$index,xLeft:$xLeft,yParam3:$yParam3,xRight:$xRight,yStart:$yStart"
        )
        when (type) {
            1 -> {
                pointFs.add(PointF(xLeft, yParam3))
                pointFs.add(PointF(xRight, yStart))
            }

            2 -> {
                pointFs.add(PointF(xRight, yStart))
                pointFs.add(PointF(xLeft, yParam3))
                pointFs.add(PointF(xRight, yStart))
            }

            3 -> {
                pointFs.add(PointF(xRight, yStart))
            }

            4 -> {
                pointFs.add(PointF(xLeft, yParam3))
                pointFs.add(PointF(xRight, yStart))
            }

            else -> {
                // Do nothing
            }
        }

        mBarPath = Path().apply {
            moveTo(xLeft, yParam3)
            lineTo(xRight, yStart)
            lineTo(xRight, y + height)
            lineTo(xLeft, y + height)
            close()
        }
    }

    val drawLineColor: Int
        get() = linePaint.color

    fun drawBar(canvas: Canvas) {
        canvas.drawPath(mBarPath, barPaint)
    }

    fun changePainColor() {
        val context = MainApplication.context

        when (state) {
            0, 2 -> {
                barPaint.shader = createLinearGradient(noSelectedBgColor, noSelectedBgColor)
                linePaint.color = noSelectedBottomBgColor
                mDrawLineColor = noSelectedBottomBgColor
            }

            1 -> {
                if (charge == "true") {
                    barPaint.shader = createLinearGradient(chargeLineColor50, chargeLineColor10)
                    linePaint.color = chargeLineNewColor
                    mDrawLineColor = chargeLineNewColor
                } else if (charge == "low") {
                    barPaint.shader =
                        createLinearGradient(lowBatteryLineColor50, lowBatteryLineColor10)
                    linePaint.color = lowBatteryLineNewColor
                    mDrawLineColor = lowBatteryLineNewColor
                } else {
                    barPaint.shader =
                        createLinearGradient(noChargeLineColor50, noChargeLineColor10)
                    linePaint.color = noChargeLineNewColor
                    mDrawLineColor = noChargeLineNewColor
                }

            }
        }

//        curvePaint.setColor(getColorResource(
//            R.color.battery_not_select_bg_bottom,
//            R.color.battery_not_select_bg_bottom_card
//        ));

//        val bgColor = resources.getColor(R.color.battery_chart_set)
//        f18283o.shader = b(bgColor, bgColor)

        // Common paint settings
//        f18283o.isAntiAlias = true
        barPaint.isAntiAlias = true
        barPaint.isAntiAlias = true
        barPaint.isDither = true

        linePaint.style = Paint.Style.STROKE
        linePaint.strokeJoin = Paint.Join.ROUND
        linePaint.strokeWidth = context.dp2px(2f)
    }

    private fun createLinearGradient(
        @ColorInt colorStart: Int,
        @ColorInt colorEnd: Int,
    ): LinearGradient {
        // 重新设计渐变策略以增强低电量情况下的可见性
        val level = levelAndCharge.level
        val levelY = (if (level < 20) 0f else 0f) + (100 - level) * height / 100f
        Logcat.d(
            "BatteryStackBarData>>>",
            "level:$level,levelY is $levelY,startY:$y, height is $height"
        )
        // 调整渐变起始位置，使其与电量曲线位置对齐
        return LinearGradient(
            x,
            levelY, // 使用电量对应的Y坐标作为起始位置
            x + barWidth,
            y + height,
            colorStart,
            colorEnd,
            Shader.TileMode.CLAMP
        )
    }

}