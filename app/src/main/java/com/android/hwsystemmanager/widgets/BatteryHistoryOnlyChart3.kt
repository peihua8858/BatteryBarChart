package com.android.hwsystemmanager.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.text.TextPaint
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.View
import com.android.hwsystemmanager.BatteryHistoryChartPaintFactory
import com.android.hwsystemmanager.BatteryStatisticsHelper
import com.android.hwsystemmanager.LevelAndCharge
import com.android.hwsystemmanager.MainApplication
import com.android.hwsystemmanager.R
import com.android.hwsystemmanager.utils.AttributeParseUtils
import com.android.hwsystemmanager.utils.Logcat
import com.android.hwsystemmanager.utils.dp2px
import com.android.hwsystemmanager.utils.isLandscape
import com.android.hwsystemmanager.utils.isLayoutRtl
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

class BatteryHistoryOnlyChart3 @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attributeSet, defStyleAttr) {

    // 常量定义
    companion object {
        private const val ONE_HOUR_MS = 3600000L
        private const val ONE_DAY_MS = 86400000L
        private const val MIN_TIME_DIFF_FOR_12H_FORMAT = 82800000L // 23小时

        fun formatTime(timestamp: Long): String {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = timestamp
            }
            return DateUtils.formatDateTime(
                MainApplication.context,
                calendar.timeInMillis,
                DateUtils.FORMAT_SHOW_TIME
            ).also {
                Logcat.d("BatteryHistoryOnlyChart", "timestamp:$timestamp, formattedTime:$it")
            }
        }
    }

    // 尺寸和边距
    private val lineWidth: Int = resources.getDimensionPixelOffset(R.dimen.battery_history_chart_linewidth)
    private val bottomPadding: Int = resources.getDimensionPixelOffset(R.dimen.battery_history_chart_bottom_padding)
    private val chartHeight: Float = resources.getDimension(R.dimen.battery_chart_height)
    private val timeTextSize: Int = resources.getDimensionPixelSize(R.dimen.battery_history_chart_aboveTimeText_size)
    private val belowTimeTextSize: Int = resources.getDimensionPixelSize(R.dimen.battery_history_chart_belowTimeText_size)
    private val dateTextSize: Int = resources.getDimensionPixelSize(R.dimen.battery_history_chart_dateText_size)
    private val topMargin: Int = resources.getDimension(R.dimen.margin_bar_top_bubble).toInt()

    // 时间相关
    var endTime: Long = 0
        private set
    var startTime: Long = 0
        private set

    // 布局参数
    private var leftPadding: Int = 0
    private var rightMargin: Int = 0
    private var chartTop: Int = 0
    private var chartBottom: Int = 0
    private var contentLeft: Int = 0
    private var contentRight: Int = 0
    private var rightEdge: Int = 0
    private var leftEdge: Int = 0
    private var chartWidth: Int = 0
    private var startPosition: Int = 0
    private var timeLabelWidth: Float = 0f
    private var timeLabelMargin: Float = 0f
    private var timeLabelInterval: Float = 0f
    private var percentTextWidth: Float = 0f

    // 状态标志
    private var is12HourFormat: Boolean = false

    // 绘制工具
    private val bottomLinePaint: Paint
    private val gridLinePaint: Paint
    private val leftLinePaint: Paint
    private val rightLinePaint: Paint
    private val dateTextPaint: TextPaint
//    private val percentTextPaint: TextPaint
    private val timeTextPaint: TextPaint
    private val belowTimeTextPaint: TextPaint
    private val secondaryTimeTextPaint: TextPaint

    // 数据集合
    private val dateLabels = ArrayList<DateLabel>()
    private val timeLabels = ArrayList<TimeLabel>()
    private val percentLabels = ArrayList<PercentLabel>()
    private var currentTimeLabel: TimeLabel? = null

    // 数据类
    data class DateLabel(val paint: TextPaint, val position: Float, val calendar: Calendar) {
        val text: String
        val width: Int
        val height: Int

        init {
            text = DateFormat.format(
                DateFormat.getBestDateTimePattern(Locale.getDefault(), "Md"),
                calendar
            ).toString()

            val bounds = Rect()
            paint.getTextBounds(text, 0, text.length, bounds)
            height = bounds.height()
            width = bounds.width()
        }
    }

    data class PercentLabel(val paint: TextPaint, val x: Int, val y: Int, val text: String, val height: Int)

    data class TimeLabel(val paint: TextPaint, val x: Int, val y: Int, val text: String) {
        val width: Int
        val height: Int

        init {
            val bounds = Rect()
            paint.getTextBounds(text, 0, text.length, bounds)
            height = bounds.height()
            width = bounds.width()
        }
    }

    init {
        // 初始化绘制工具
        val isCardMode = true // 根据实际需求调整

        val bottomLineSize = resources.getDimensionPixelSize(R.dimen.battery_history_chart_bottomline_size)
        val cardBottomLineSize = resources.getDimensionPixelSize(R.dimen.battery_history_chart_y_line_size_card)
        val xLineSize = resources.getDimensionPixelSize(R.dimen.battery_history_chart_x_line_size_card)
        val yLineSize = resources.getDimensionPixelSize(R.dimen.battery_history_chart_y_line_size_card)

        gridLinePaint = BatteryHistoryChartPaintFactory.m11666b/*createGridLinePaint*/(
            if (isCardMode) xLineSize else bottomLineSize
        )

        bottomLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = context.getColor(
                if (isCardMode) R.color.stroke_bottom_line_color_card
                else R.color.stroke_bottom_line_color
        )
            style = Paint.Style.STROKE
            strokeWidth = if (isCardMode) yLineSize.toFloat() else bottomLineSize.toFloat()
    }

        leftLinePaint = BatteryHistoryChartPaintFactory.m11667c/*createLinePaint*/(
            if (isCardMode) cardBottomLineSize else bottomLineSize
        )

        rightLinePaint = BatteryHistoryChartPaintFactory.m11667c/*createLinePaint*/(
            if (isCardMode) cardBottomLineSize else bottomLineSize
        )

        dateTextPaint = BatteryHistoryChartPaintFactory.m11665a/*createTextPaint*/(dateTextSize)
        timeTextPaint = BatteryHistoryChartPaintFactory.m11665a(timeTextSize)
        belowTimeTextPaint = BatteryHistoryChartPaintFactory.m11665a(belowTimeTextSize)

        secondaryTimeTextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            color = AttributeParseUtils.m14218a(android.R.attr.textColorSecondary, false)
            isAntiAlias = true
            textSize = dateTextSize.toFloat()
        }

        // 初始化时间
        endTime = BatteryStatisticsHelper.m934d/*getCurrentTime*/()
        startTime = endTime - ONE_DAY_MS // 默认显示一天的数据
    }

    fun addContentDescription(text: String?) {
        val currentDescription = contentDescription?.toString() ?: ""
        setContentDescription("$currentDescription$text;")
    }

    fun drawPercentLabel(canvas: Canvas, label: PercentLabel) {
        if (abs(percentLabels[1].y - percentLabels[0].y) >= label.height + lineWidth) {
            canvas.drawText(label.text, label.x.toFloat(), label.y.toFloat(), secondaryTimeTextPaint)
            addContentDescription(label.text)
        }
    }

    fun drawTimeLabel(canvas: Canvas, label: TimeLabel, lineSpacing: Float, x: Float) {
        val textParts = splitTimeText(label.text)
        var primaryPaint = timeTextPaint
        var secondaryPaint = belowTimeTextPaint

        if (is12HourFormat) {
            primaryPaint = belowTimeTextPaint
            secondaryPaint = timeTextPaint
        }

        canvas.drawText(textParts[0], x, label.y.toFloat(), primaryPaint)
        addContentDescription(label.text)

        canvas.drawText(textParts[1], x, label.y + lineSpacing, secondaryPaint)
        addContentDescription(label.text)
    }

    fun shouldUse12HourFormat(): Boolean {
        val timeDiff = endTime - startTime
        return timeDiff >= MIN_TIME_DIFF_FOR_12H_FORMAT && !DateFormat.is24HourFormat(context)
    }

    fun addDateLabel(calendar: Calendar, position: Int) {
        val adjustedPosition = if (isLayoutRtl) {
            position - (((calendar.timeInMillis - startTime) * chartWidth) / ONE_DAY_MS).toInt()
        } else {
            position + (((calendar.timeInMillis - startTime) * chartWidth) / ONE_DAY_MS).toInt()
        }

        dateLabels.add(DateLabel(dateTextPaint, adjustedPosition.toFloat(), calendar))
    }

    fun measureTextWidth(text: String): Int {
        return if (shouldUse12HourFormat()) {
            val textParts = splitTimeText(text)
            maxOf(
                belowTimeTextPaint.measureText(textParts[0]),
                belowTimeTextPaint.measureText(textParts[1])
            ).toInt()
        } else {
            dateTextPaint.measureText(text).toInt()
        }
    }

    fun splitTimeText(text: String): List<String> {
        val result = mutableListOf<String>()

        if (text[0].isDigit()) {
            is12HourFormat = true
            val lastDigitIndex = text.indexOfLast { it.isDigit() }

            try {
                result.add(text.substring(0, lastDigitIndex + 1))
                result.add(text.substring(lastDigitIndex + 2)) // 跳过空格或分隔符
            } catch (e: IndexOutOfBoundsException) {
                result.add("")
                result.add("")
                Logcat.d("BatteryHistoryOnlyChart", "Index out of string bounds.")
            }
        } else {
            val firstDigitIndex = text.indexOfFirst { it.isDigit() }
            if (firstDigitIndex >= 0) {
                result.add(text.substring(0, firstDigitIndex))
                result.add(text.substring(firstDigitIndex))
            } else {
                result.add(text)
                result.add("")
            }
        }

        return result
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val startX = if (isLayoutRtl) {
            contentRight + timeLabelMargin
        } else {
            contentLeft - timeLabelMargin
        }

        val endX = if (isLayoutRtl) {
            contentLeft - timeLabelMargin
        } else {
            contentRight + timeLabelMargin
        }

        // 绘制网格线
        drawGridLines(canvas, startX, endX)

        // 绘制百分比标签
        percentLabels.forEach { drawPercentLabel(canvas, it) }

        // 绘制当前时间标签
        drawCurrentTimeLabel(canvas)

        // 绘制日期标签
        drawDateLabels(canvas)

        // 绘制时间标签
        drawTimeLabels(canvas)
        }

    private fun drawGridLines(canvas: Canvas, startX: Float, endX: Float) {
        val chartTopY = chartTop.toFloat()
        val chartBottomY = chartBottom.toFloat()
        val middleY = (chartTop + chartBottom) / 2f
        val quarter = (chartBottom - chartTop) / 4f

        // 绘制水平线
        canvas.drawLine(startX, chartBottomY, endX, chartBottomY, bottomLinePaint)
        canvas.drawLine(startX, chartTopY + quarter, endX, chartTopY + quarter, gridLinePaint)
        canvas.drawLine(startX, middleY, endX, middleY, gridLinePaint)
        canvas.drawLine(startX, middleY + quarter, endX, middleY + quarter, gridLinePaint)
        canvas.drawLine(startX, chartTopY, endX, chartTopY, gridLinePaint)

        // 绘制垂直线
        canvas.drawLine(startX, chartTopY + dp2px(4f), startX, chartBottomY, leftLinePaint)
        canvas.drawLine(endX, chartTopY + dp2px(4f), endX, chartBottomY, rightLinePaint)
        }

    private fun drawCurrentTimeLabel(canvas: Canvas) {
        if (endTime <= startTime) {
            Logcat.d("BatteryHistoryOnlyChart", "drawNowLabel is error, currTime is below with chart start time")
            return
        }

        val timePosition = (((endTime - startTime - ONE_HOUR_MS) * chartWidth) / ONE_DAY_MS).toInt()
        val xPosition = if (isLayoutRtl) {
            startPosition - timePosition
            } else {
            startPosition + timePosition
            }

        val timeText = formatTime(endTime)
        val textWidth = measureTextWidth(timeText)

        var adjustedX = if (isLayoutRtl) {
            xPosition - (timeLabelWidth.toInt() + textWidth)
            } else {
            xPosition + timeLabelWidth.toInt()
            }

        // 边界检查
        if (!isLayoutRtl && adjustedX > rightEdge) {
            adjustedX = rightEdge
        } else if (isLayoutRtl && adjustedX < leftEdge) {
            adjustedX = leftEdge - textWidth + dp2px(12)
            }

        val yPosition = (bottomPadding / 2) + chartTop

        if (shouldUse12HourFormat()) {
                Logcat.d("BatteryHistoryOnlyChart", "Enter into double line branch.")
            currentTimeLabel = TimeLabel(dateTextPaint, adjustedX, yPosition, timeText)
            currentTimeLabel?.let {
                drawTimeLabel(canvas, it, (timeTextSize * 1.5f), it.x.toFloat())
        }
        } else {
            currentTimeLabel = TimeLabel(dateTextPaint, adjustedX, yPosition, timeText)
            currentTimeLabel?.let {
                canvas.drawText(it.text, it.x.toFloat(), it.y.toFloat(), dateTextPaint)
                addContentDescription(it.text)
                }
                }
                }

    private fun drawDateLabels(canvas: Canvas) {
        if (dateLabels.isEmpty()) return

        dateLabels.forEach { label ->
            var xPosition = label.position

            // 检查是否需要调整位置以避免重叠
            currentTimeLabel?.let { currentLabel ->
                val minMargin = resources.getDimensionPixelSize(R.dimen.bar_min_margin) + lineWidth

                val isOverlapping = if (isLayoutRtl) {
                    xPosition >= currentLabel.width + currentLabel.x + minMargin
                } else {
                    xPosition + label.width + minMargin <= currentLabel.x
                }

                if (!isOverlapping) {
                    // 调整位置以避免重叠
                    canvas.drawText(
                        label.text,
                        xPosition,
                        (label.height / 2f) + (bottomPadding / 2f) + chartTop,
                        dateTextPaint
                    )
                    addContentDescription(label.text)

                    // 绘制连接线
                    val lineX = if (isLayoutRtl) {
                        xPosition + timeLabelMargin
                    } else {
                        xPosition - timeLabelMargin
                    }

                    canvas.drawLine(
                        lineX, chartTop + dp2px(4f),
                        lineX, chartBottom.toFloat(),
                        leftLinePaint
                    )
                }
            } ?: run {
                // 没有当前时间标签，直接绘制
                canvas.drawText(
                    label.text,
                    xPosition,
                    (label.height / 2f) + (bottomPadding / 2f) + chartTop,
                    dateTextPaint
                    )
                addContentDescription(label.text)
                }
            }
        }

    private fun drawTimeLabels(canvas: Canvas) {
        if (endTime - startTime < MIN_TIME_DIFF_FOR_12H_FORMAT) return

            Logcat.d("BatteryHistoryOnlyChart", "show timeLabel.")

        timeLabels.clear()
        val startTimeText = formatTime(startTime)
        val textWidth = measureTextWidth(startTimeText)

        val xPosition = if (isLayoutRtl) {
            startPosition - textWidth
            } else {
            startPosition
            }

        val yPosition = (bottomPadding / 2) + chartTop
        timeLabels.add(TimeLabel(dateTextPaint, xPosition, yPosition, startTimeText))

        // 检查日期标签和时间标签是否重叠
        if (dateLabels.isNotEmpty() && timeLabels.isNotEmpty()) {
            val timeLabel = timeLabels[0]
            val dateLabel = dateLabels[0]

            val minMargin = resources.getDimensionPixelSize(R.dimen.bar_min_margin) + lineWidth

            val isOverlapping = if (isLayoutRtl) {
                timeLabel.x - dateLabel.position < minMargin + timeLabel.width
                            } else {
                dateLabel.position - timeLabel.x < minMargin + dateLabel.width
                            }

            if (!isOverlapping) {
                if (shouldUse12HourFormat()) {
                    drawTimeLabel(canvas, timeLabel, (timeTextSize * 1.5f), timeLabel.x.toFloat())
                    } else {
                    canvas.drawText(timeLabel.text, timeLabel.x.toFloat(), timeLabel.y.toFloat(), dateTextPaint)
                    addContentDescription(timeLabel.text)
                    }
                }
            } else {
            Logcat.d("BatteryHistoryOnlyChart", "error mTimeLabels or mDateLabels is error.")
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        var height = (topMargin + bottomPadding + chartHeight).toInt()
        if (shouldUse12HourFormat()) {
            height += (timeTextSize * 1.5).toInt()
        }

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        dateLabels.clear()
        timeLabels.clear()
        percentLabels.clear()

        // 计算布局参数
        leftPadding = dp2px(if (isLandscape) 3 else 2)
        rightMargin = if (isLayoutRtl) {
            leftPadding
                        } else {
            resources.getDimension(R.dimen.battery_history_chart_right_margin).toInt()
        }

        chartTop = topMargin
        chartBottom = height - bottomPadding
        if (shouldUse12HourFormat()) {
            chartBottom -= (timeTextSize * 1.5).toInt()
                        }

        contentLeft = leftPadding
        contentRight = width - rightMargin

        // 计算文本宽度用于布局
        val percentText = NumberFormat.getPercentInstance().format(100 / 100.0)
//        percentTextWidth = BatteryHistoryChartPaintFactory.measureTextWidth(
//            percentText,
//            BatteryHistoryChartPaintFactory.createTextPaint(dateTextSize)
//        )
        percentTextWidth = BatteryHistoryChartPaintFactory.m11668d(
            percentText,
            BatteryHistoryChartPaintFactory.m11665a(dateTextSize)
        )
        leftEdge = contentLeft
        rightEdge = (contentRight - (resources.getDimension(R.dimen.battery_maigin_text_percent) + percentTextWidth)).toInt()
        chartWidth = rightEdge - leftEdge
        startPosition = if (isLayoutRtl) contentRight else contentLeft

        // 计算时间标签间隔
        timeLabelInterval = (chartWidth - (resources.getDimension(R.dimen.battery_maigin_text_percent) + percentTextWidth)) / 24f
        timeLabelWidth = timeLabelInterval * 0.6666667f
        timeLabelMargin = (timeLabelInterval * 0.33333334f) / 4f

        // 添加日期标签
        if (startTime in 1..<endTime) {
            val startCalendar = Calendar.getInstance().apply {
                timeInMillis = startTime
                set(Calendar.MILLISECOND, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MINUTE, 0)
            }

            val endCalendar = Calendar.getInstance().apply {
                timeInMillis = endTime
                set(Calendar.MILLISECOND, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MINUTE, 0)
            }

            if (startCalendar[Calendar.DAY_OF_YEAR] != endCalendar[Calendar.DAY_OF_YEAR] ||
                startCalendar[Calendar.YEAR] != endCalendar[Calendar.YEAR]) {

                startCalendar[Calendar.HOUR_OF_DAY] = 0
                var calendarTime = startCalendar.timeInMillis

                if (calendarTime < startTime) {
                    startCalendar.add(Calendar.DAY_OF_YEAR, 1)
                    calendarTime = startCalendar.timeInMillis
                }

                val endTimeMs = endCalendar.timeInMillis
                if (calendarTime < endTimeMs) {
                    addDateLabel(startCalendar, startPosition)
                }

                startCalendar.add(Calendar.DAY_OF_YEAR, 1)
                if (startCalendar.timeInMillis < endTimeMs) {
                    addDateLabel(startCalendar, startPosition)
                }
            }
        }

        // 添加百分比标签
        val chartHeightPx = chartBottom - chartTop
        val xPosition = if (isLayoutRtl) contentLeft else contentRight

        percentLabels.add(PercentLabel(
            secondaryTimeTextPaint,
            xPosition,
            chartTop + (chartHeightPx * 0.5).toInt(),
            NumberFormat.getPercentInstance().format(50 / 100.0),
            secondaryTimeTextPaint.textSize.toInt()
        ))

        percentLabels.add(PercentLabel(
            secondaryTimeTextPaint,
            xPosition,
            chartTop + (chartHeightPx * 0.25).toInt(),
            NumberFormat.getPercentInstance().format(75 / 100.0),
            secondaryTimeTextPaint.textSize.toInt()
        ))
    }

    fun setData(data: ArrayList<LevelAndCharge>) {
        endTime = BatteryStatisticsHelper.m934d()//getCurrentTime()
        Logcat.d("BatteryHistoryOnlyChart", "endTime = $endTime")

        startTime = if (data.isNotEmpty()) {
            data[0].time ?: endTime
                        } else {
            endTime
    }

        // 计算开始时间（默认显示24小时数据）
        val dataDuration = if (data.isNotEmpty()) {
            data.size * ONE_HOUR_MS
        } else {
            ONE_DAY_MS
    }

        startTime = endTime - dataDuration
        Logcat.d("BatteryHistoryOnlyChart", "startTime = $startTime, endTime = $endTime, duration = $dataDuration")
        }
    }