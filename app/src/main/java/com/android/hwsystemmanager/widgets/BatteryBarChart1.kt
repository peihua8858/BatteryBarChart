package com.android.hwsystemmanager.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.text.BidiFormatter
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import androidx.core.view.ViewCompat
import com.android.BarChartTouchHelper1
import com.android.hwsystemmanager.BatteryStackBarData1
import com.android.hwsystemmanager.BatteryStatisticsHelper
import com.android.hwsystemmanager.LevelAndCharge
import com.android.hwsystemmanager.R
import com.android.hwsystemmanager.utils.AttributeParseUtils
import com.android.hwsystemmanager.utils.BatterHistoryUtils
import com.android.hwsystemmanager.utils.Logcat
import com.android.hwsystemmanager.utils.createPaint
import com.android.hwsystemmanager.utils.dp2px
import com.android.hwsystemmanager.utils.isLandscape
import com.android.hwsystemmanager.utils.isLayoutRtl
import com.android.hwsystemmanager.utils.measureTextSize
import java.text.NumberFormat
import java.util.Calendar
import kotlin.math.abs

class BatteryBarChart1 @JvmOverloads constructor(
    context: Context?,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attributeSet, defStyleAttr) {
    @JvmField
    var f9893A: Float = 0f
    @JvmField
    var f9894B: Float = 0f
    var mCallBack: BatterHistoryUtils.OnCallBack? = null
    var slideListener: BatterHistoryUtils.OnSlideListener? = null
    @JvmField
    var mSelectIndex: Int
    var mNumLists: ArrayList<LevelAndCharge>
    @JvmField
    var mBarLists: ArrayList<BatteryStackBarData1>
    @JvmField
    var mIsHalfHour: Int = 0
    @JvmField
    var lastIndex: Int = 0
    @JvmField
    var f9902h: Int = 0
    @JvmField
    var f9903i: Float = 0f
    @JvmField
    var f9904j: Float = 0f
    @JvmField
    val f9905k: Float = resources.getDimension(R.dimen.battery_history_chart_bottom_padding)

    @JvmField
    var f9906l: Float = 0f
    @JvmField
    var f9907m: Float = 0f
    @JvmField
    var f9908n: Float = 0f
    @JvmField
    var f9909o: Float = 0f
    @JvmField
    var f9910p: Int = 0
    @JvmField
    var f9911q: Float = 0.0f
    @JvmField
    var f9912r: Float = 0f
    val f9913s: ArrayList<Int>
    @JvmField
    var f9914t: Float = 0f
    @JvmField
    var f9915u: Int
    @JvmField
    var f9916v: Int = 0
    @JvmField
    val f9917w: PointF
    var f9918x: BarChartTouchHelper1? = null
    @JvmField
    val f9919y: Float = resources.getDimension(R.dimen.margin_bar_top_bubble)

    @JvmField
    val f9920z: Float = resources.getDimension(R.dimen.battery_chart_height)

    init {
        this.mSelectIndex = -1
        this.mNumLists = ArrayList()
        this.mBarLists = ArrayList()
        this.f9913s = ArrayList()
        this.f9915u = this.mSelectIndex
        this.f9917w = PointF()
    }


    private val mIsLayoutRtl: Boolean
        get() = isLayoutRtl


    private fun setSelectIndex(i4: Int) {
        this.mSelectIndex = i4
        val arrayList = this.f9913s
        arrayList.clear()
        arrayList.add(this.mSelectIndex)
    }


    fun m7034a() {
        var z10: Boolean
        var z11: Boolean
        var z12: Boolean
        var z13: Boolean
        var z14: Boolean
        if (mNumLists.size < 48) {
            Logcat.d("BatteryBarChart", "bar size is still not filled")
            return
        }
        mBarLists.clear()
        var z15 = true
        var i4 = 0
        var z16 = false
        var i8 = -1
        var z17 = true
        var i9 = 48
        while (i4 < i9) {
            val f10 = this.f9911q//10dp
            val f11 = this.f9912r
            val f12 = (i4 * f11) + f10
            val f13 = this.f9908n
            val f14 = this.f9909o - f13
            val levelAndCharge = mNumLists[i4]
            val batteryStackBarData = BatteryStackBarData1(f12, f13, f11, f14, levelAndCharge)
            val m10301a = mNumLists[i4].charge == "true"
            z10 = i4 < 47 && mNumLists[i4 + 1].charge == "true"
            z11 = !(!isLandscape && this.f9912r < dp2px(11.0f))
            z12 = (z10 || z11) && m10301a
            z13 = mNumLists[i4].level <= 81
            z14 = i4 < 47 && mNumLists[i4 + 1].level <= 81
            if (z12 && z17) {
                if (i8 == -1) {
                    i8 = i4
                }
                Logcat.d("BatteryBarChart", " area indexHorizontalSufficient = $i8")
                if (z13 && z14) {
                    Logcat.d("BatteryBarChart", "this area should draw i = $i4")
                    z17 = false
                    z16 = true
                }
            }
            mBarLists.add(batteryStackBarData)
            mBarLists.last().f18274f = i4
            i4++
            i9 = 48
        }
        if (!m7039f()) {
            m7035b()
        }
        if (!z16) {
            val arrayList = this.mBarLists
            if (i8 < 0 || i8 > arrayList.size - 1) {
                z15 = false
            }
            if (z15) {
                Logcat.d("BatteryBarChart", "normal is not draw indexHorizontalSuffcient is $i8")
                mBarLists[i8].javaClass
            }
        }
    }


    fun m7035b() {
        for (index in 0..47) {
            if (this.mSelectIndex == (this.mIsHalfHour + index) / 2) {
                mBarLists[index].f18276h = 1
                if (index == endIndex) {
                    Logcat.d("BatteryBarChart", "j = $index ")
                    if (this.mIsHalfHour == 1 && (index == 0 || index == 47)) {
                        mBarLists[index].f18278j = -1
                    } else {
                        val i8 = index - 1
                        Logcat.d(
                            "BatteryBarChart",
                            mBarLists[i8].f18273e.level.toString() + " " + mBarLists[index].f18273e.level
                        )
                        if (mBarLists[index].f18273e.level != 0 && mBarLists[i8].f18273e.level != 0) {
                            if (mBarLists[index].f18273e.level >= mBarLists[i8].f18273e.level) {
                                mBarLists[index].f18278j = 2
                                mBarLists[i8].f18278j = 0
                            } else {
                                mBarLists[i8].f18278j = 1
                                mBarLists[index].f18278j = 0
                            }
                        } else if (mBarLists[index].f18273e.level == 0) {
                            mBarLists[i8].f18278j = -1
                        } else if (mBarLists[i8].f18273e.level == 0) {
                            mBarLists[index].f18278j = -1
                        } else {
                            Logcat.d("BatteryBarChart", "invalid")
                        }
                    }
                }
                mBarLists[index].f18277i = true
            } else {
                mBarLists[index].f18276h = 0
                mBarLists[index].f18277i = false
            }
        }
    }


    fun m7036c(i4: Int): Int {
        var i8 = ((i4 - this.f9903i) / this.f9912r).toInt()
        if (i8 < 0 || ((mNumLists.isNotEmpty()) && i8 > mNumLists.size - 1)) {
            i8 = 0
        }
        Logcat.d("BatteryBarChart", "getSelectedBarIndex $i8")
        return i8
    }


    fun m7037d(i4: Int, i8: Int): Int {
        if (i4 < 0) {
            return 0
        }
        var z11 = true
        if ((mNumLists.isNotEmpty()) && i4 > mNumLists.size - 1) {
            return 0
        }
        val z10 = i4 == this.f9902h - 1
        if (mNumLists[i4].level < 0 || mNumLists[i8].level != 0) {
            z11 = false
        }
        if ((!z10 || !z11) && i4 != i8) {
            return 0
        }
        return -1
    }

    // android.view.View
    public override fun dispatchHoverEvent(event: MotionEvent): Boolean {
        val z10: Boolean
        val barChartTouchHelper = this.f9918x
        z10 = barChartTouchHelper?.dispatchHoverEvent(event) ?: false
        return !(!z10 && !super.dispatchHoverEvent(event))
    }

    // android.view.View
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val z10: Boolean
        val barChartTouchHelper = this.f9918x
        z10 = barChartTouchHelper?.dispatchKeyEvent(event) ?: false
        return !(!z10 && !super.dispatchKeyEvent(event))
    }


    fun m7038e(i4: Int): Boolean {
        val i8: Int
        val i9 = this.f9902h
        i8 = if (i9 > 1 && i9 % 2 != this.mIsHalfHour) {
            1
        } else {
            0
        }
        val f10 = i4.toFloat()
        val f11 = this.f9903i
        return !(f10 >= (this.f9912r * (i9 + i8)) + f11 || f10 <= f11)
    }


    fun m7039f(): Boolean {
        return this.mSelectIndex == -1
    }

    val clickPointDescription: String
        get() {
            var selectedTime = selectedTime
            val str = mBarLists[startIndex].f18275g
            val str2 = mBarLists[endIndex].f18275g
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
            val levelAndCharge = mNumLists.getOrNull(startIndex)
            m934d = levelAndCharge?.time ?: BatteryStatisticsHelper.m934d()
            return m934d - 1800000
        }

    val selectedTimeSpand: Long
        get() {
            if (m7037d(startIndex, endIndex) != 0) {
                return 1800000L
            }
            return 3600000L
        }

    val startIndex: Int
        get() {
            if (this.mSelectIndex != 0 && !m7039f()) {
                return (this.mSelectIndex * 2) - this.mIsHalfHour
            }
            return 0
        }


    fun m7040i() {
        this.mSelectIndex = -1
        f9913s.clear()
        if (mBarLists.size <= 48) {
            for (i4 in 0..47) {
                mBarLists[i4].f18276h = 1
            }
        }
        val bVar = this.mCallBack
        bVar?.onCalback(BatteryStatisticsHelper.m934d(), 3600000L, true)
        invalidate()
    }


    fun m7041j() {
        Logcat.d("BatteryBarChart", "bar isLand $isLandscape")
        val f10 = ((this.f9907m - this.f9906l) - this.f9914t) / 48
        this.f9912r = f10
        this.f9911q = this.f9903i
        this.f9893A = 0.6666667f * f10
        this.f9894B = f10 * 0.33333334f
    }


    fun m7042k(i4: Int) {
        val z10: Boolean
        if ((m7036c(i4) + this.mIsHalfHour) / 2 != this.mSelectIndex && m7038e(i4)) {
            setSelectIndex((m7036c(i4) + this.mIsHalfHour) / 2)
            m7035b()
            invalidate()
        }
        val aVar = this.slideListener
        if (aVar != null) {
            z10 = this.f9916v == 2
            parent.requestDisallowInterceptTouchEvent(true)
            aVar.onSlide(z10)
        }
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        DrawBubbleView1.onDraw(this,canvas)
    }

    public override fun onMeasure(i4: Int, i8: Int) {
        super.onMeasure(i4, i8)
        setMeasuredDimension(
            MeasureSpec.getSize(i4),
            (this.f9919y + this.f9905k + this.f9920z).toInt()
        )
    }

    public override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        val m14219b: Float
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        val format = NumberFormat.getPercentInstance().format(100 / 100.0)
        val textPaint =
            createPaint(resources.getDimensionPixelSize(R.dimen.battery_history_chart_dateText_size))
        val pair = textPaint.measureTextSize(format)
        val m11668d = pair.first
        val f10 = if (isLandscape) {
            3.0f
        } else {
            2.0f
        }
        var m10476a = dp2px(f10).toFloat()
        val z10 = true
        m14219b = if (z10) {
            m10476a
        } else {
            AttributeParseUtils.m14219b(33620168)
        }
        this.f9903i = m14219b
        if (!z10) {
            m10476a = AttributeParseUtils.m14219b(33620170)
        }
        this.f9904j = m10476a
        this.f9914t = resources.getDimension(R.dimen.battery_maigin_text_percent) + m11668d
        this.f9910p = width
        this.f9908n = this.f9919y
        this.f9909o = height - this.f9905k
        this.f9906l = this.f9903i
        this.f9907m = width - this.f9904j
        mIsLayoutRtl
        m7041j()
        m7034a()
        val size = mNumLists.size
        if (size != 0 && mBarLists.size != 0) {
            for (i11 in 0..<size) {
                if (mNumLists[i11].level != 0 && i11 != 0) {
                    this.lastIndex = i11
                }
            }
            val i12 = size - 1
            if (mNumLists[i12].level != 0) {
                this.lastIndex = i12
            }
            val formatDateTime = DateUtils.formatDateTime(
                context,
                mNumLists[0].time, 17
            )
            val formatDateTime2 = DateUtils.formatDateTime(
                context, mNumLists[lastIndex].time, 17
            )
            val m9243i = context.getString(R.string.power_battery_choose_info)
            val format2 = String.format(
                m9243i, formatDateTime, formatDateTime2,
                mBarLists[0].f18275g, mBarLists[lastIndex].f18275g
            )
            contentDescription = format2
        }
    }
    public final override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        val action = event.action
        val point = this.f9917w
        var result = false
        val handled = true

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                System.currentTimeMillis()
//                this.f9341u = this.f9323c
                this.f9915u = this.mSelectIndex
                this.f9916v = 0
                point.set(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                if (!this.m7038e(x)) {
                    return false
                }
                if (this.f9916v == 1 || this.f9916v == 2) {
                    this.m7042k(x)
                } else {
                    val yDiff = abs(event.y - point.y)
                    val xDiff = abs(event.x - point.x)
                    if (yDiff > xDiff) {
                        this.f9916v = 2
                    } else if (abs(event.x - point.x) > 60f) {
                        this.f9916v = 1
                        this.m7042k(x)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                if (event.y < this.f9919y) {
                    return false
                }
                if (this.m7038e(x)) {
                    val index = (this.m7036c(x) + this.mIsHalfHour) / 2
                    if (this.f9915u == index) {
                        Logcat.d("BatteryBarChart", "reset bar status.")
                        this.m7040i()
                        result = true
                    }
                }
                if (result) {
                    return handled
                }
                if (this.m7038e(x)) {
                    val index = (this.m7036c(x) + this.mIsHalfHour) / 2
                    if (index != this.mSelectIndex) {
                        if (this.m7038e(x)) {
                            val selectedIndex = (this.m7036c(x) + this.mIsHalfHour) / 2
                            this.setSelectIndex(selectedIndex)
                            this.m7035b()
                            this.invalidate()
                        }
                    }
                }
                if (!this.m7039f()) {
                    val timeSpan = this.selectedTimeSpand
                    this.mCallBack?.onCalback(this.selectedTime, timeSpan, false)
                }
                this.slideListener?.onSlide(true)
            }
            else -> {
                if (event.y < this.f9919y) {
                    return false
                }
                if (!this.m7039f()) {
                    val timeSpan = this.selectedTimeSpand
                    this.mCallBack?.onCalback(this.selectedTime, timeSpan, false)
                }
                this.slideListener?.onSlide(true)
            }
        }
        return handled
    }

//    override fun onTouchEvent(ev: MotionEvent): Boolean {
//        val x10 = ev.x.toInt()
//        val y10 = ev.y.toInt()
//        val action = ev.action
//        val pointF = this.f9917w
//        var z10 = false
//        MotionEvent.ACTION_UP
//        if (action != MotionEvent.ACTION_DOWN) {
//            val f10 = this.f9919y
//            if (action != MotionEvent.ACTION_MOVE) {
//                if (action != MotionEvent.ACTION_CANCEL) {
//                    if (y10 < f10) {
//                        return false
//                    }
//                    if (m7038e(x10)) {
//                        if (this.f9915u == (m7036c(x10) + this.mIsHalfHour) / 2) {
//                            Logcat.d("BatteryBarChart", "reset bar status.")
//                            m7040i()
//                            z10 = true
//                        }
//                    }
//                    if (z10) {
//                        return true
//                    }
//                    if (m7038e(x10) && (m7036c(x10) + this.mIsHalfHour) / 2 != this.mSelectIndex && m7038e(
//                            x10
//                        )
//                    ) {
//                        setSelectIndex((m7036c(x10) + this.mIsHalfHour) / 2)
//                        m7035b()
//                        invalidate()
//                    }
//                    if (!m7039f()) {
//                        this.mCallBack?.onCalback(selectedTime, selectedTimeSpand, false)
//                    }
//                    parent.requestDisallowInterceptTouchEvent(true)
//                    this.slideListener?.onSlide(true)
//                    return true
//                }
//                if (!m7039f()) {
//                    this.mCallBack?.onCalback(selectedTime, selectedTimeSpand, false)
//                }
//                parent.requestDisallowInterceptTouchEvent(true)
//                this.slideListener?.onSlide(true)
//                return true
//            }
//            if (m7038e(x10)) {
//                val f11 = y10.toFloat()
//                if (f11 >= f10) {
//                    val i4 = this.f9916v
//                    if (i4 != 1 && i4 != 2) {
//                        val f12 = x10.toFloat()
//                        if (abs(f11 - pointF.y) > abs(f12 - pointF.x)) {
//                            this.f9916v = 2
//                        } else if (abs(f12 - pointF.x) > 60.0f) {
//                            this.f9916v = 1
//                            m7042k(x10)
//                        }
//                    } else {
//                        m7042k(x10)
//                    }
//                }
//            }
//            return false
//        }
//        this.f9915u = this.mSelectIndex
//        this.f9916v = 0
//        pointF[ev.x] = ev.y
//        return true
//    }

    fun setListData(listData: List<LevelAndCharge>) {
        val j10: Long
        mNumLists.clear()
        this.f9902h = listData.size
        val it = listData.iterator()
        while (it.hasNext()) {
            mNumLists.add(it.next())
        }
        val currentTimeMillis = System.currentTimeMillis()
        j10 = if (this.f9902h <= 0) {
            BatteryStatisticsHelper.m935e(currentTimeMillis) + (currentTimeMillis - 1800000)
        } else {
            mNumLists.last().time
        }
        val i8 = this.f9902h
        var i9 = 1
        val i10 = 0
        val i4: Int = 48 - i8
        if (i8 <= 48 && 1 <= i4) {
            var i11 = 1
            while (true) {
                mNumLists.add(LevelAndCharge(i10, "false", (i11 * 1800000L) + j10))
                if (i11 == i4) {
                    break
                } else {
                    i11++
                }
            }
        }
        if (mNumLists.isNotEmpty()) {
            val j11 = mNumLists[0].time
            val context = context
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = j11
            val formatDateTime = DateUtils.formatDateTime(context, calendar.timeInMillis, 1)
            val m11850E0 = formatDateTime.indexOf(':') + 1
            val substring = formatDateTime.substring(m11850E0, m11850E0 + 2)
            val res = substring.toInt()
            if (res != 0) {
                i9 = 0
            }
        }

        this.mIsHalfHour = i9
        val barChartTouchHelper = BarChartTouchHelper1(this)
        this.f9918x = barChartTouchHelper
        ViewCompat.setAccessibilityDelegate(this, barChartTouchHelper)
//        requestLayout()
    }

    companion object {
        @JvmStatic
        fun m7032g(index: Int, barData: List<BatteryStackBarData1>): Boolean {
            return barData[index].f18273e.charge != barData[index - 1].f18273e.charge
        }

        @JvmStatic
        fun m7033h(index: Int, barData: List<BatteryStackBarData1>): Boolean {
            return barData[index].f18273e.charge != barData[index + 1].f18273e.charge
        }
    }
}
