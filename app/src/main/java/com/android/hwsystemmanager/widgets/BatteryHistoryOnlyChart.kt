package com.android.hwsystemmanager.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.os.Build
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
import com.android.hwsystemmanager.utils.Logcat
import com.android.hwsystemmanager.utils.createDashedPaint
import com.android.hwsystemmanager.utils.createPaint
import com.android.hwsystemmanager.utils.createTextPaint
import com.android.hwsystemmanager.utils.dp2px
import com.android.hwsystemmanager.utils.getDimension
import com.android.hwsystemmanager.utils.getDimensionPixelOffset
import com.android.hwsystemmanager.utils.getDimensionPixelSize
import com.android.hwsystemmanager.utils.isLandscape
import com.android.hwsystemmanager.utils.isLayoutRtl
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

@SuppressLint("ResourceType")
class BatteryHistoryOnlyChart @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attributeSet, defStyleAttr) {
    var f9921A: Int = 0
    var f9922B: Int = 0
    var f9923C: Int = 0
    var f9924D: Int = 0
    var f9925E: Float = 0f
    var f9926F: Float = 0f
    var f9927G: Float = 0f
    var f9928H: Float = 0f
    val barBubbleTopMargin: Int

    //f9930a
    private var mEndTime: Long = 0

    //f9931b
    private var mStartTime: Long = 0
    private val chartLineWidth: Int = getDimensionPixelOffset(R.dimen.battery_history_chart_linewidth)

    var f9933d: Int = 0
    var f9934e: Int = 0
    private val chartBottomPadding: Int = getDimensionPixelOffset(R.dimen.battery_history_chart_bottom_padding)
    private val chartHeight: Float = getDimension(R.dimen.battery_chart_height)
    val aboveTimeTextSize: Float

    //横向底部线条画笔
    val f9938i: Paint

    //横向线条画笔
    val f9939j: Paint

    //纵向虚线条左侧画笔
    val f9940k: Paint

    //纵向虚线条中间（凌晨时间点）画笔
    val f9941l: Paint

    //纵向虚线条右侧画笔
    val f9942m: Paint

    //底部文字画笔
    val f9943n: TextPaint
    val f9944o: TextPaint
    val f9945p: TextPaint
    val f9946q: TextPaint
    val f9947r: ArrayList<DateTimeLabel> = ArrayList()
    val f9948s: ArrayList<TimeLabel> = ArrayList()
    val f9949t: ArrayList<PercentageLabel> = ArrayList()
    var f9950u: TimeLabel? = null
    var f9951v: Int = 0
    var f9952w: Int = 0
    var f9953x: Int = 0
    var f9954y: Int = 0
    var f9955z: Boolean = false
    private val blowTextTopMargin: Int = dp2px(48)

    //C2673a
    class DateTimeLabel(paint: TextPaint, positionX: Float, calendar: Calendar) {
        val posX: Float
        val dateStr: String
        val width: Int
        val height: Int

        init {
            val obj = DateFormat.format(
                DateFormat.getBestDateTimePattern(Locale.getDefault(), "Md"),
                calendar
            ).toString()
            this.dateStr = obj
            val rect = Rect()
            paint.getTextBounds(obj, 0, obj.length, rect)
            this.height = rect.height()
            this.width = rect.width()
            this.posX = positionX
        }
    }


    //C2674b
    class PercentageLabel(paint: TextPaint, xPosition: Int, yBase: Int, yAdjustment: Int, percentage: Int) {
        val xCoordinate: Int
        val yCoordinate: Int
        val formattedPercentage: String
        val textHeight: Int

        init {
            val format = NumberFormat.getPercentInstance().format(percentage / 100.0)
            this.formattedPercentage = format
            val rect = Rect()
            paint.getTextBounds(format, 0, format.length, rect)
            val height = rect.height()
            this.textHeight = height
            this.xCoordinate = if (isLayoutRtl) xPosition else xPosition - rect.width()
            this.yCoordinate = (height / 2) + ((yBase + yAdjustment) - ((percentage * yAdjustment) / 100))
        }
    }


    //C2675c
    class TimeLabel(paint: TextPaint, val timePosition: Int, i8: Int, val label: String) {
        val labelHeight: Int
        val labelWidth: Int

        init {
            val rect = Rect()
            paint.getTextBounds(label, 0, label.length, rect)
            val height = rect.height()
            this.labelWidth = rect.width()
            this.labelHeight = (height / 2) + i8
        }
    }


    init {
        this.aboveTimeTextSize = getDimension(R.dimen.battery_history_chart_aboveTimeText_size)
        val belowTimeTextSize = getDimension(R.dimen.battery_history_chart_belowTimeText_size)
        this.barBubbleTopMargin = getDimensionPixelSize(R.dimen.margin_bar_top_bubble)
        val bottomLineSize = getDimension(R.dimen.battery_history_chart_bottomline_size)
        val xLineSize = getDimension(R.dimen.battery_history_chart_x_line_size_card)
        val yLineSize = getDimension(R.dimen.battery_history_chart_y_line_size_card)
        val z10 = Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1
        val intValue = (if (z10) yLineSize else bottomLineSize)
        val intValue2 = if (z10) xLineSize else bottomLineSize
        val dateTextSize = getDimension(R.dimen.battery_history_chart_dateText_size)
        this.f9939j = createDashedPaint(intValue2, R.color.stroke_line_color_card)
        this.f9938i = createPaint(intValue, R.color.stroke_bottom_line_color_card)
        this.f9940k = createDashedPaint(intValue, R.color.stroke_y_line_color_card)
        this.f9941l = createDashedPaint(intValue, R.color.stroke_y_line_color_card)
        this.f9942m = createDashedPaint(intValue, R.color.stroke_y_line_color_card)
        this.f9945p = createTextPaint(aboveTimeTextSize)
        this.f9946q = createTextPaint(belowTimeTextSize)
        this.f9943n = createTextPaint(dateTextSize)
        this.f9944o = createTextPaint(dateTextSize)
    }


    //m7044a
    fun updateContentDescription(str: String?) {
        val currentContentDescription: String?
        val contentDescription = contentDescription
        currentContentDescription = contentDescription?.toString()
        setContentDescription("$currentContentDescription$str;")
    }


    //m7045b
    fun drawBatteryHistoryText(canvas: Canvas, data: PercentageLabel) {
        val arrayList = this.f9949t
        if (abs(arrayList[1].yCoordinate - arrayList[0].yCoordinate) >= arrayList[1].textHeight + this.chartLineWidth) {
            val x = data.xCoordinate.toFloat()
            val y = data.yCoordinate.toFloat()
            val textPaint = this.f9944o
            val str = data.formattedPercentage
            canvas.drawText(str, x, y, textPaint)
            updateContentDescription(str)
        }
    }


    //m7046c
    fun drawSplitTextOnCanvas(canvas: Canvas, c2675c: TimeLabel, f10: Float, f11: Float) {
        val str = c2675c.label
        val splitStrings = splitStringByDigits(str)
        var textPaint = this.f9945p
        var textPaint2 = this.f9946q
        if (this.f9955z) {
            textPaint2 = textPaint
            textPaint = textPaint2
        }
        val str2 = splitStrings[0]
        val f12 = c2675c.labelHeight.toFloat()
        canvas.drawText(str2, f11, f12, textPaint)
        updateContentDescription(str)
        canvas.drawText(splitStrings[1], f11, f12 + f10, textPaint2)
        updateContentDescription(str)
    }


    private fun shouldUseAmPmNotation(): Boolean {
        val durationInMilliseconds = this.mEndTime - this.mStartTime
        return durationInMilliseconds >= 82800000 && !DateFormat.is24HourFormat(context)
    }

    //m7048f
    fun addTimePointToCalendar(calendar: Calendar, initialPosition: Int) {
        val adjustedPosition: Int
        val timeInMillis =
            (((calendar.timeInMillis - this.mStartTime) * this.f9924D) / 86400000).toInt()
        adjustedPosition = if (isLayoutRtl) {
            initialPosition - timeInMillis
        } else {
            initialPosition + timeInMillis
        }
        f9947r.add(DateTimeLabel(this.f9943n, adjustedPosition.toFloat(), calendar))
    }

    //m7049g
    fun calculateMaxTextWidth(str: String): Int {
        if (shouldUseAmPmNotation()) {
            val parts = splitStringByDigits(str)
            val textPaint = this.f9946q
            var maxMeasureText = textPaint.measureText(parts[1])
            val measureText2 = textPaint.measureText(parts[0])
            if (maxMeasureText < measureText2) {
                maxMeasureText = measureText2
            }
            return maxMeasureText.toInt()
        }
        return f9943n.measureText(str).toInt()
    }

    //m7050h
    fun splitStringByDigits(str: String): ArrayList<String> {
        val result: ArrayList<String> = ArrayList()
        val length = str.length
        if (Character.isDigit(str[0])) {
            this.f9955z = true
            var index = 0
            for (i in 0..<length) {
                if (Character.isDigit(str[i])) {
                    index = i
                }
            }
            val nextIndex = index + 1
            try {
                val firstPart = str.substring(0, nextIndex)
                result.add(firstPart)
                val secondPart = str.substring(nextIndex + 1, length)
                result.add(secondPart)
            } catch (e: IndexOutOfBoundsException) {
                result.add("")
                result.add("")
                Logcat.d("BatteryHistoryOnlyChart", "Index out of string bounds.")
            }
            return result
        }
        var digitPosition = 0
        while (true) {
            if (digitPosition < length) {
                if (Character.isDigit(str[digitPosition])) {
                    break
                }
                digitPosition++
            } else {
                digitPosition = 0
                break
            }
        }
        val prefix = str.substring(0, digitPosition)
        result.add(prefix)
        val suffix = str.substring(digitPosition, length)
        result.add(suffix)
        return result
    }

    public override fun onDraw(canvas: Canvas) {
        val z10: Boolean
        val arrayList: ArrayList<DateTimeLabel>?
        var i4: Int
        var i8: Float
        val i9: Int
        val i10: Int
        val f12: Float
        val f13: Float
        var i11: Int
        var f14: Float
        var f15: Float
        var z12: Boolean
        var z13: Boolean
        var f16: Float
        val i12: Int
        var i13: Int
        val z14: Boolean
        val z15: Boolean
        super.onDraw(canvas)
        val f10 = if (isLayoutRtl) {
            f9954y + this.f9926F
        } else {
            f9953x - this.f9926F
        }
        val f11 = if (isLayoutRtl) {
            f9921A + this.f9926F
        } else {
            f9921A - this.f9926F
        }
        val f18 = f11
        val i14 = this.f9952w
        val i15 = this.f9951v
        val f19 = ((i14 + i15) / 2).toFloat()
        val f20 = ((i15 - i14) / 4).toFloat()
        val m10476a: Int = dp2px(4)
        val f21 = f9952w.toFloat()
        canvas.drawLine(f10, f21, f18, f21, this.f9938i)
        val f22 = this.f9952w + f20
        val paint = this.f9939j
        canvas.drawLine(f10, f22, f18, f22, paint)
        canvas.drawLine(f10, f19, f18, f19, paint)
        val f23 = f19 + f20
        canvas.drawLine(f10, f23, f18, f23, paint)
        val f24 = f9951v.toFloat()
        canvas.drawLine(f10, f24, f18, f24, paint)
        canvas.drawLine(
            f10, (this.f9952w + m10476a).toFloat(), f10,
            f9951v.toFloat(), this.f9940k
        )
        canvas.drawLine(
            f18, (this.f9952w + m10476a).toFloat(), f18,
            f9951v.toFloat(), this.f9942m
        )
        val arrayList2 = this.f9949t
        if (arrayList2.size == 2) {
            val c2674b = arrayList2[0]
            drawBatteryHistoryText(canvas, c2674b)
            val c2674b2 = arrayList2[1]
            drawBatteryHistoryText(canvas, c2674b2)
        }
//        val i16 = this.f9951v
//        val i17 = this.f9952w
//        val paint2 = Paint(Paint.ANTI_ALIAS_FLAG)
//        paint2.isAntiAlias = true
//        paint2.style = Paint.Style.FILL
//        paint2.setShader(
//            LinearGradient(
//                0.0f, i16.toFloat(), 0.0f, i17.toFloat(), context.getColor(
//                    R.color.hsm_widget_canvas_degree_line_alpha50
//                ), context.getColor(
//                    R.color.hsm_widget_canvas_degree_line_alpha10
//                ), Shader.TileMode.CLAMP
//            )
//        )
        val textPaint = this.f9943n
        var i18 = this.aboveTimeTextSize
        val i19 = this.chartBottomPadding
        if (this.mEndTime > this.mStartTime) {
            val i20 = ((((this.mEndTime - this.mStartTime) - 3600000) * this.f9924D) / 86400000).toInt()
            i12 = if (isLayoutRtl) {
                f9923C - i20
            } else {
                f9923C + i20
            }
            val m7043d = m7043d(this.mEndTime)
            val m7049g = calculateMaxTextWidth(m7043d)
            i13 = if (isLayoutRtl) {
                (i12 - (f9927G.toInt())) - m7049g
            } else {
                i12 + (f9927G.toInt())
            }
            val i21 = this.f9921A
            z14 = i13 > i21
            z15 = i13 < i21
            if (!isLayoutRtl && z14) {
                i13 = this.f9921A
            }
            if (isLayoutRtl && z15) {
                i13 = (this.f9921A - m7049g) + dp2px(12)//C5820e.m13959i()
            }
            if (shouldUseAmPmNotation()) {
                Logcat.d("BatteryHistoryOnlyChart", "Enter into double line branch.")
                val c2675c = TimeLabel(textPaint, i13, (i19 / 2) + this.f9952w, m7043d)
                this.f9950u = c2675c
                drawSplitTextOnCanvas(canvas, c2675c, (i18 * 1.5).toFloat(), c2675c.timePosition.toFloat())
            } else {
                val c2675c2 = TimeLabel(textPaint, i13, (i19 / 2) + this.f9952w, m7043d)
                val f25 = c2675c2.timePosition.toFloat()
                val f26 = c2675c2.labelHeight.toFloat()
                val str3 = c2675c2.label
                canvas.drawText(str3, f25, f26, textPaint)
                updateContentDescription(str3)
                this.f9950u = c2675c2
            }
        } else {
            Logcat.d(
                "BatteryHistoryOnlyChart",
                "drawNowLabel is error, currTime is below with chart start time"
            )
        }
        val arrayList3 = this.f9947r
        z10 = arrayList3.isNotEmpty()
        arrayList = if (z10) {
            arrayList3
        } else {
            null
        }
        val i22 = this.chartLineWidth
        if (arrayList == null || arrayList3.size - 1 < 0) {
            i4 = i22
            i8 = i18
        } else {
            val c2673a = arrayList3[0]
            val c2673a2 = c2673a
            var f27 = f9922B.toFloat()
            val f28 = c2673a2.posX
            if (f28 > f27) {
                f27 = f28
            }
            val str5 = c2673a2.dateStr
            val measureText = textPaint.measureText(str5)
            val c2675c3 = this.f9950u
            if (c2675c3 != null) {
                f14 = if (isLayoutRtl) {
                    f27 - this.f9925E
                } else {
                    f27
                }
                f15 = if (isLayoutRtl) {
                    (f27 - measureText) - this.f9925E
                } else {
                    f27
                }
                val dimensionPixelSize =
                    resources.getDimensionPixelSize(R.dimen.bar_min_margin) + i22
                val i24 = c2675c3.labelWidth
                val i25 = c2675c3.timePosition
                val i26 = i22
                z12 = if (f27 >= i24 + i25 + dimensionPixelSize) {
                    true
                } else {
                    false
                }
                z13 = if (f27 + c2673a2.width + dimensionPixelSize <= i25) {
                    true
                } else {
                    false
                }
                if (!z12 && !z13) {
                    i8 = i18
                    i4 = i26
                } else {
                    canvas.drawText(
                        str5,
                        f15,
                        ((c2673a2.height / 2f) + (i19 / 2f) + this.f9952w),
                        textPaint
                    )
                    updateContentDescription(str5)
                    f16 = if (isLayoutRtl) {
                        f14 + this.f9926F
                    } else {
                        f14 - this.f9926F
                    }
                    val f29 = f16
                    i4 = i26
                    i8 = i18
                    canvas.drawLine(
                        f29, this.f9952w + dp2px(4.0f), f29,
                        f9951v.toFloat(), this.f9941l
                    )
                }
            } else {
                i4 = i22
                i8 = i18
            }
//            i18 = i8
//            i22 = i4
        }
        val z11 = this.mEndTime - this.mStartTime >= 82800000
        if (z11) {
            Logcat.d("BatteryHistoryOnlyChart", "show timeLabel.")
            val arrayList4 = this.f9948s
            arrayList4.clear()
            val m7043d2 = m7043d(this.mStartTime)
            val m7049g2 = calculateMaxTextWidth(m7043d2)
            i9 = if (isLayoutRtl) {
                f9923C - m7049g2
            } else {
                f9923C
            }
            arrayList4.add(TimeLabel(textPaint, i9, (i19 / 2) + this.f9952w, m7043d2))
            if (arrayList3.isEmpty() && arrayList4.isNotEmpty()) {
                var size2 = arrayList4.size - 1
                if (size2 < 0) {
                    return
                }
                while (true) {
                    val i27 = size2 - 1
                    if (size2 == 0) {
                        val c2675c4 = arrayList4[size2]
                        val c2675c5 = c2675c4
                        val c2675c6 = this.f9950u
                        if (c2675c6 != null) {
                            val m9620e: Boolean = isLayoutRtl
                            val i28 = c2675c5.timePosition
                            val i29 = c2675c6.timePosition
                            i11 = if (m9620e) {
                                i28 - i29
                            } else {
                                i29 - i28
                            }
                            val f30 = i28.toFloat()
                            if (i11 >= c2675c5.labelWidth + i4 + resources.getDimensionPixelSize(R.dimen.bar_min_margin)) {
                                drawSplitTextOnCanvas(canvas, c2675c5, (i8 * 1.5).toFloat(), f30)
                            }
                        }
                    }
                    if (i27 >= 0) {
                        size2 = i27
                    } else {
                        return
                    }
                }
            } else {
                if (arrayList4.isNotEmpty() && arrayList3.isNotEmpty()) {
                    val c2675c7 = arrayList4[0]
                    val c2675c9 = this.f9950u
                    if (c2675c9 != null) {
                        val m9620e2: Boolean = isLayoutRtl
                        val i30 = c2675c7.timePosition
                        val i31 = c2675c9.timePosition
                        i10 = if (m9620e2) {
                            i30 - i31
                        } else {
                            i31 - i30
                        }
                        if (isLayoutRtl) {
                            f12 = i30.toFloat()
                            f13 = arrayList3[0].posX
                        } else {
                            f12 = arrayList3[0].posX
                            f13 = i30.toFloat()
                        }
                        val f31 = f12 - f13
                        val dimensionPixelSize2 =
                            c2675c7.labelWidth + i4 + resources.getDimensionPixelSize(
                                R.dimen.bar_min_margin
                            )
                        val f32 = c2675c7.timePosition.toFloat()
                        if (i10 >= dimensionPixelSize2 && f31 >= dimensionPixelSize2) {
                            drawSplitTextOnCanvas(canvas, c2675c7, (i8 * 1.5).toFloat(), f32)
                            return
                        }
                        return
                    }
                    return
                }
                Logcat.d("BatteryHistoryOnlyChart", "error mTimeLabels or mDateLabels is error.")
            }
        }
    }

    // android.view.View
    public override fun onMeasure(i4: Int, i8: Int) {
        super.onMeasure(i4, i8)
        var height = (this.barBubbleTopMargin + this.chartBottomPadding + this.chartHeight).toInt()
        if (shouldUseAmPmNotation()) {
            height += (this.aboveTimeTextSize * 1.5).toInt()
        }
        setMeasuredDimension(MeasureSpec.getSize(i4), height)
    }

    // android.view.View
    public override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        f9947r.clear()
        f9948s.clear()
        val arrayList = this.f9949t
        arrayList.clear()
        val d10 = 100 / 100.0
        val format = NumberFormat.getPercentInstance().format(d10)
        this.f9928H = BatteryHistoryChartPaintFactory.m11668d(
            format, BatteryHistoryChartPaintFactory.m11665a(
                resources.getDimensionPixelSize(R.dimen.battery_history_chart_dateText_size)
            )
        )
        var m10476a: Int = dp2px(if (isLandscape) 3 else 2)
        val z10 = true
        this.f9933d =
            if (z10) m10476a else resources.getDimension(R.dimen.battery_history_chart_left_padding)
                .toInt()
        if (!z10) {
            m10476a = resources.getDimension(R.dimen.battery_history_chart_right_margin).toInt()
        }
        this.f9934e = m10476a
        val dimension = resources.getDimension(R.dimen.battery_maigin_text_percent) + this.f9928H
        this.f9951v = this.barBubbleTopMargin
        this.f9952w = height - this.chartBottomPadding
        if (shouldUseAmPmNotation()) {
            this.f9952w -= (this.aboveTimeTextSize * 1.5).toInt()
        }
        val i11 = this.f9933d
        this.f9953x = i11
        val i12 = width - this.f9934e
        this.f9954y = i12
        val i13 = (i12 - dimension).toInt()
        this.f9921A = i13
        this.f9922B = i11
        this.f9924D = i13 - i11
        this.f9923C = if (isLayoutRtl) this.f9954y else this.f9953x
        val format2 = NumberFormat.getPercentInstance().format(d10)
        val dimension2 =
            ((this.f9954y - this.f9953x) - (resources.getDimension(R.dimen.battery_maigin_text_percent) + BatteryHistoryChartPaintFactory.m11668d(
                format2, BatteryHistoryChartPaintFactory.m11665a(
                    resources.getDimensionPixelSize(R.dimen.battery_history_chart_dateText_size)
                )
            ))) / 24
        this.f9927G = dimension2
        this.f9925E = 0.6666667f * dimension2
        this.f9926F = (dimension2 * 0.33333334f) / 4
        val j10 = this.mEndTime
        val j11 = this.mStartTime
        if (j11 in 1..<j10) {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = mStartTime
            calendar[14] = 0
            calendar[13] = 0
            calendar[12] = 0
            if (calendar.timeInMillis < this.mStartTime) {
                calendar[11] = calendar[11] + 1
            }
            val calendar2 = Calendar.getInstance()
            calendar2.timeInMillis = mEndTime
            calendar2[14] = 0
            calendar2[13] = 0
            calendar2[12] = 0
            if (calendar[6] != calendar2[6] || calendar[1] != calendar2[1]) {
                calendar[11] = 0
                var timeInMillis = calendar.timeInMillis
                if (timeInMillis < this.mStartTime) {
                    calendar[6] = calendar[6] + 1
                    timeInMillis = calendar.timeInMillis
                }
                val timeInMillis2 = calendar2.timeInMillis
                if (timeInMillis < timeInMillis2) {
                    addTimePointToCalendar(calendar, this.f9923C)
                }
                calendar[6] = calendar[6] + 1
                if (calendar.timeInMillis < timeInMillis2) {
                    addTimePointToCalendar(calendar, this.f9923C)
                }
            }
        }
        val i14 = this.f9952w - this.f9951v
        val i15 = if (isLayoutRtl) this.f9953x else this.f9954y
        if (isLayoutRtl) {
            this.f9921A = (this.f9953x + dimension).toInt()
        }
        val textPaint = this.f9944o
        arrayList.add(PercentageLabel(textPaint, i15, this.f9951v, i14, 100))
        arrayList.add(PercentageLabel(textPaint, i15, this.f9951v, i14, 50))
    }

    fun setData(value2: ArrayList<LevelAndCharge>) {
        val j10: Long
        val j11: Long
        val j12: Long
//        ResourcesWrap.m30n()
        this.mEndTime = BatteryStatisticsHelper.m934d()
        Logcat.d("BatteryHistoryOnlyChart", "endTime = " + this.mEndTime)
        if (value2.isNotEmpty()) {
            val firstItem = value2.getOrNull(0)
            j10 = firstItem?.time ?: this.mEndTime
            j12 = 1800000
        } else {
            j10 = this.mEndTime
            j11 = value2.size.toLong()
            j12 = j11 * 1800000L
        }
        this.mStartTime = j10 - j12
        Logcat.d("BatteryHistoryOnlyChart", "f9931b = " + this.mStartTime + ",j10 = " + j10 + ",j12 = " + j12)
    }

    companion object {
        fun m7043d(j10: Long): String {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = j10
            val formatDateTime =
                DateUtils.formatDateTime(MainApplication.context, calendar.timeInMillis, DateUtils.FORMAT_SHOW_TIME)
            Logcat.d("BatteryHistoryOnlyChart", "j10:$j10,startTimeString =$formatDateTime")
            return formatDateTime
        }
    }
}
