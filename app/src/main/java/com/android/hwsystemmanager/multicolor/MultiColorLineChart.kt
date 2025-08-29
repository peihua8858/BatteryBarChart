package com.android.hwsystemmanager.multicolor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.text.TextPaint
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.View
import androidx.core.view.ViewCompat
import com.android.hwsystemmanager.BatteryStackBarData1
import com.android.hwsystemmanager.LevelAndCharge
import com.android.hwsystemmanager.MainApplication
import com.android.hwsystemmanager.R
import com.android.hwsystemmanager.utils.Logcat
import com.android.hwsystemmanager.utils.TimeUtil
import com.android.hwsystemmanager.utils.createDashedPaint
import com.android.hwsystemmanager.utils.dp2px
import com.android.hwsystemmanager.utils.getDimension
import com.android.hwsystemmanager.utils.getDimensionPixelSize
import com.android.hwsystemmanager.utils.isLandscape
import com.android.hwsystemmanager.utils.isLayoutRtl
import com.android.hwsystemmanager.utils.isPie
import com.android.hwsystemmanager.utils.measureTextSize
import com.android.hwsystemmanager.utils.parseInt
import com.fz.common.utils.dLog
import java.text.NumberFormat
import java.util.Calendar
import kotlin.math.abs

class MultiColorLineChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {
    val batteryDataList = mutableListOf<LevelAndCharge>()
    val stackBarDataList = mutableListOf<StackBarPointData>()
    private val mPercentCoorList = mutableListOf<PercentCoordinate>()
    private val barChartTouchHelper by lazy { BarChartTouchHelper(this) }
    private var mIsHalfHour = 0
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    private var mEndTime: Long = 0
    private var mStartTime: Long = 0
    private val mLineWidth = getDimensionPixelSize(R.dimen.battery_history_chart_linewidth)
    private val mBottomTextPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = Color.DKGRAY
        this.textSize = getDimension(R.dimen.battery_history_chart_dateText_size)
        this.textAlign = Paint.Align.CENTER
    }
    private val mPrecentTextPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = Color.DKGRAY
        this.textSize = getDimension(R.dimen.battery_history_chart_dateText_size)
        this.textAlign = Paint.Align.CENTER
    }
    private val mBottomTextWidth: Int
    private var mVerticalGap: Float = 0f
    private val mLeftVerticalLineWidth = dp2px(if (isLandscape) 3f else 2f)

    // Initialize axis paint
    private val axisPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = Color.DKGRAY
        this.strokeWidth = 3f
        this.style = Paint.Style.STROKE
    }


    // Initialize grid paint
    private val gridPaint: Paint = createDashedPaint(
        getDimension(R.dimen.battery_history_chart_y_line_size_card),
        R.color.stroke_y_line_color_card
    )


    private val maxValue = 100f
    private val minValue = 0f
    private var pathRenderer: MultiColorPathRenderer = MultiColorPathRenderer()
    private val bottomPadding = getDimensionPixelSize(R.dimen.battery_history_chart_bottom_padding)
    private val dp48 = dp2px(48)
    private val chartHeight = getDimensionPixelSize(R.dimen.battery_chart_height)
    private val precentTextWidth: Int
    private val chartAboveTextSize =
        getDimensionPixelSize(R.dimen.battery_history_chart_aboveTimeText_size)
    private val chartPrecentTextMargin: Float = getDimension(R.dimen.battery_maigin_text_percent)
    private val barBubbleTopMargin = getDimension(R.dimen.margin_bar_top_bubble)
    private var mBarWidth: Float = 0f
    var mSelectIndex: Int = -1

    init {
        mBottomTextPaint.measureTextSize("现在").apply {
            mBottomTextWidth = this.width()
        }
        val precentFormat = NumberFormat.getPercentInstance().format(100 / 100.0)
        mPrecentTextPaint.measureTextSize(precentFormat).apply {
            precentTextWidth = this.width()
        }

        // 定义颜色及其位置（0~1）
//        int[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
        val colors = intArrayOf(Color.GREEN, Color.GRAY, Color.GRAY, Color.GRAY)
        // 创建渲染器
        val points: MutableList<PointF> = ArrayList()
        points.add(PointF(100f, 200f))
        points.add(PointF(200f, 300f))
        points.add(PointF(300f, 100f))
        points.add(PointF(400f, 400f))
        points.add(PointF(500f, 200f))
        pathRenderer = MultiColorPathRenderer()
        pathRenderer.setData(points, colors)
        pathRenderer.setStrokeWidth(5f) // 设置线宽
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
        var i9 = (this.chartHeight + this.dp48 + this.bottomPadding + dp2px(40))
        if (is24HourFormat()) {
            i9 += (this.chartAboveTextSize * 1.5).toInt()
        }
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), i9)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h - bottomPadding - dp48
        mVerticalGap = mHeight / 2f
        processData()
        //右侧百分比坐标
        val i14 = this.mHeight - this.mVerticalGap
        val i15 = if (isLayoutRtl) 0f else w.toFloat()
        mPercentCoorList.add(
            PercentCoordinate(
                mBottomTextPaint,
                gridPaint, 0f, precentTextWidth,
                i15,
                this.mVerticalGap,
                i14,
                100
            )
        )
        mPercentCoorList.add(
            PercentCoordinate(
                mBottomTextPaint,
                gridPaint, 0f, precentTextWidth,
                i15,
                this.mVerticalGap,
                i14,
                50
            )
        )
        mPercentCoorList.add(
            PercentCoordinate(
                mBottomTextPaint,
                gridPaint, 0f, precentTextWidth,
                i15,
                this.mVerticalGap,
                i14,
                0
            )
        )
    }

    fun m7041j() {
        Logcat.d("BatteryBarChart", "bar isLand $isLandscape")
        val f10 =
            ((this.mWidth - mLeftVerticalLineWidth/*this.f9906l*/) - (this.precentTextWidth + chartPrecentTextMargin)) / 48
        this.mBarWidth = f10
//        this.f9911q = this.f9903i
//        this.f9893A = 0.6666667f * f10
//        this.f9894B = f10 * 0.33333334f
    }

    private fun processData() {
        var z12: Boolean
        if (batteryDataList.size < 48) {
            Logcat.d("BatteryBarChart", "bar size is still not filled")
            return
        }
        stackBarDataList.clear()
        var i8 = -1
        var z16 = false
        var z17 = true
        for ((index, item) in batteryDataList.withIndex()) {
            val f10 = this.mLeftVerticalLineWidth//2dp 或3dp
            val f11 = this.mBarWidth
            val f12 = (index * f11) + f10
            val f13 = this.barBubbleTopMargin
            val f14 = this.chartHeight - f13
            val stackBarData = StackBarPointData(f12, f13, f11, f14, item)
            stackBarData.index = index
            val nextCharge = batteryDataList[index + 1]
            val isCharge = item.charge == "true"
            val isNextCharge = index < 47 && nextCharge.charge == "true"
            val z13 = item.level <= 81
            val z14 = index < 47 && nextCharge.level <= 81
            val z11 = !(!isLandscape && this.mBarWidth < dp2px(11.0f))
            z12 = (isNextCharge || z11) && isCharge
            if (z12 && z17) {
                if (i8 == -1) {
                    i8 = index
                }
                Logcat.d("BatteryBarChart", " area indexHorizontalSufficient = $i8")
                if (z13 && z14) {
                    Logcat.d("BatteryBarChart", "this area should draw i = $index")
                    z17 = false
                    z16 = true
                }
            }
            stackBarDataList.add(stackBarData)
        }


        var z15 = true
        if (!notSelected()) {
//            m7035b()
        }
        if (!z16) {
            val arrayList = this.stackBarDataList
            if (i8 < 0 || i8 > arrayList.size - 1) {
                z15 = false
            }
            if (z15) {
                Logcat.d("BatteryBarChart", "normal is not draw indexHorizontalSuffcient is $i8")
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        drawGrid(canvas)
//        drawAxes(canvas)
        drawHorLineAndPrecent(canvas)
        //绘制曲线
        pathRenderer.draw(canvas)
        val x = dp2px(4f)
        drawBottomLabels(canvas, x, mWidth)
    }


    private fun drawHorLineAndPrecent(canvas: Canvas) {
        var prevY = paddingTop.toFloat()
        mPercentCoorList.forEach {
            prevY = it.draw(canvas, prevY, mLineWidth)
        }
    }

    private fun drawBottomLabels(canvas: Canvas, x: Float, chartWidth: Int) {
        val paddingLeft = paddingLeft
        val paddingBottom = paddingBottom
        // Draw chart title
        val textPaint = this.mBottomTextPaint
        val hours = createHours()
        val gad = (chartWidth / 8f).toInt()
        for ((index, item) in hours.withIndex()) {
            canvas.drawText(
                item,
                x + index * gad,
                (this.mHeight + dp48).toFloat(),
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
        if (dataSize <= 48 && 1 <= size) {
            var index = 1
            while (true) {
                batteryDataList.add(LevelAndCharge(0, "false", (index * HALF_HOUR) + endTime))
                if (index == size) {
                    break
                } else {
                    index++
                }
            }
        }
        this.mEndTime = getEndTime()
        if (batteryDataList.isNotEmpty()) {
            val firstItem = batteryDataList.first()
            val firstTime = firstItem.time
            val context = context
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = firstTime
            val formatDateTime = DateUtils.formatDateTime(context, calendar.timeInMillis, 1)
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
    }

    //右侧百分比坐标数据
    class PercentCoordinate(
        private val paint: TextPaint,
        private val linePaint: Paint,
        private val chartX: Float,
        private val maxWidth: Int,
        i4: Float,
        i8: Float,
        i9: Float,
        value: Int,
    ) {
        private val x: Float
        private val y: Float
        private val percent: String
        private val textHeight: Int

        init {
            val format = NumberFormat.getPercentInstance().format(value / 100.0)
            this.percent = format
            val rect = paint.measureTextSize(format)
            val height = rect.height()
            this.textHeight = height

            this.x = if (isLayoutRtl) i4 else i4 - rect.width().toFloat()
            this.y = (height / 2) + ((i8 + i9) - ((value * i9) / 100))
        }

        fun draw(canvas: Canvas, prevY: Float, linewidth: Int): Float {
            if (abs(y - prevY) >= textHeight + linewidth) {
                canvas.drawText(percent, x, y, paint)
                val lineY = y - (textHeight / 2f)
                val lineStopX = x - maxWidth
                canvas.drawLine(chartX, lineY, lineStopX, lineY, linePaint)
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
) {
    var index = -1
    var charge: String = ""
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
    }

    fun drawBar(canvas: Canvas) {
        // Draw bar
    }
}