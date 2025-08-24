//package com.android.hwsystemmanager.widgets
//
//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Path
//import android.graphics.PointF
//import android.text.BidiFormatter
//import android.text.format.DateUtils
//import android.util.AttributeSet
//import android.view.KeyEvent
//import android.view.MotionEvent
//import android.view.View
//import androidx.core.view.ViewCompat
//import com.android.hwsystemmanager.BatteryStackBarData
//import com.android.hwsystemmanager.LevelAndCharge
//import com.android.hwsystemmanager.SelectedItem
//import com.android.hwsystemmanager.BarChartTouchHelper
//import com.android.hwsystemmanager.BatteryStatisticsHelper
//import com.android.hwsystemmanager.R
//import com.android.hwsystemmanager.utils.BatterHistoryUtils
//import com.android.hwsystemmanager.utils.ScreenReaderUtils
//import com.android.hwsystemmanager.utils.AttributeParseUtils
//import com.android.hwsystemmanager.utils.Logcat
//import com.android.hwsystemmanager.utils.createPaint
//import com.android.hwsystemmanager.utils.dp2px
//import com.android.hwsystemmanager.utils.isLandscape
//import com.android.hwsystemmanager.utils.isLayoutRtl
//import java.text.NumberFormat
//import java.util.Calendar
//import kotlin.math.abs
//
//class BatteryBarChart1 @JvmOverloads constructor(
//    context: Context?,
//    attributeSet: AttributeSet? = null,
//    defStyleAttr: Int = 0,
//) : View(context, attributeSet, defStyleAttr) {
//    var f9893A: Float = 0f
//    var f9894B: Float = 0f
//    var mCallBack: BatterHistoryUtils.b? = null
//    var slideListener: BatterHistoryUtils.a? = null
//    var mSelectIndex: Int =-1
//    var mNumLists: ArrayList<LevelAndCharge> = ArrayList()
//    var mBarLists: ArrayList<BatteryStackBarData> = ArrayList()
//    var mIsHalfHour: Int = 0
//    var lastIndex: Int = 0
//    var f9902h: Int = 0
//    var f9903i: Float = 0f
//    var f9904j: Float = 0f
//    val f9905k: Float = resources.getDimension(R.dimen.battery_history_chart_bottom_padding)
//    var f9906l: Float = 0f
//    var f9907m: Float = 0f
//    var f9908n: Float = 0f
//    var f9909o: Float = 0f
//    var f9910p: Int = 0
//    var f9911q: Float = 0.0f
//    var f9912r: Float = 0f
//    val f9913s: ArrayList<Int> = ArrayList()
//    var f9914t: Float = 0f
//    var f9915u: Int
//    var f9916v: Int = 0
//    val f9917w: PointF = PointF()
//    var f9918x: BarChartTouchHelper? = null
//    val f9919y: Float = resources.getDimension(R.dimen.margin_bar_top_bubble)
//    val f9920z: Float = resources.getDimension(R.dimen.battery_chart_height)
//
//    init {
//        this.f9915u = this.mSelectIndex
//    }
//
//
//    private val mIsLayoutRtl: Boolean
//        get() = isLayoutRtl
//
//
//    private fun setSelectIndex(i4: Int) {
//        this.mSelectIndex = i4
//        val arrayList = this.f9913s
//        arrayList.clear()
//        arrayList.add(this.mSelectIndex)
//    }
//
//
//    fun m7034a() {
//        var z10: Boolean
//        var z11: Boolean
//        var z12: Boolean
//        var z13: Boolean
//        var z14: Boolean
//        if (mNumLists.size < 48) {
//            Logcat.d("BatteryBarChart", "bar size is still not filled")
//            return
//        }
//        mBarLists.clear()
//        var z15 = true
//        var i4 = 0
//        var z16 = false
//        var i8 = -1
//        var z17 = true
//        var i9 = 48
//        while (i4 < i9) {
//            val f10 = this.f9911q
//            val f11 = this.f9912r
//            val f12 = (i4 * f11) + f10
//            val f13 = this.f9908n
//            val f14 = this.f9909o - f13
//            val levelAndCharge = mNumLists[i4]
//            val batteryStackBarData = BatteryStackBarData(f12, f13, f11, f14, levelAndCharge)
//            val m10301a = mNumLists[i4].charge == "true"
//            z10 = i4 < 47 && mNumLists[i4 + 1].charge == "true"
//            z11 = !(!isLandscape && this.f9912r < dp2px(11.0f))
//            z12 = (z10 || z11) && m10301a
//            z13 = mNumLists[i4].level <= 81
//            z14 = i4 < 47 && mNumLists[i4 + 1].level <= 81
//            if (z12 && z17) {
//                if (i8 == -1) {
//                    i8 = i4
//                }
//                Logcat.d("BatteryBarChart", " area indexHorizontalSufficient = $i8")
//                if (z13 && z14) {
//                    Logcat.d("BatteryBarChart", "this area should draw i = $i4")
//                    z17 = false
//                    z16 = true
//                }
//            }
//            mBarLists.add(batteryStackBarData)
//            mBarLists.last().f19390f = i4
//            i4++
//            i9 = 48
//        }
//        if (!m7039f()) {
//            m7035b()
//        }
//        if (!z16) {
//            val arrayList = this.mBarLists
//            if (i8 < 0 || i8 > arrayList.size - 1) {
//                z15 = false
//            }
//            if (z15) {
//                Logcat.d("BatteryBarChart", "normal is not draw indexHorizontalSuffcient is $i8")
//                mBarLists[i8].javaClass
//            }
//        }
//    }
//
//
//    fun m7035b() {
//        for (i4 in 0..47) {
//            if (this.mSelectIndex == (this.mIsHalfHour + i4) / 2) {
//                mBarLists[i4].f19392h = 1
//                if (i4 == endIndex) {
//                    Logcat.d("BatteryBarChart", "j = $i4 ")
//                    if (this.mIsHalfHour == 1 && (i4 == 0 || i4 == 47)) {
//                        mBarLists[i4].f19394j = -1
//                    } else {
//                        val i8 = i4 - 1
//                        Logcat.d(
//                            "BatteryBarChart",
//                            mBarLists[i8].f19389e.level.toString() + " " + mBarLists[i4].f19389e.level
//                        )
//                        if (mBarLists[i4].f19389e.level != 0 && mBarLists[i8].f19389e.level != 0) {
//                            if (mBarLists[i4].f19389e.level >= mBarLists[i8].f19389e.level) {
//                                mBarLists[i4].f19394j = 2
//                                mBarLists[i8].f19394j = 0
//                            } else {
//                                mBarLists[i8].f19394j = 1
//                                mBarLists[i4].f19394j = 0
//                            }
//                        } else if (mBarLists[i4].f19389e.level == 0) {
//                            mBarLists[i8].f19394j = -1
//                        } else if (mBarLists[i8].f19389e.level == 0) {
//                            mBarLists[i4].f19394j = -1
//                        } else {
//                            Logcat.d("BatteryBarChart", "invalid")
//                        }
//                    }
//                }
//                mBarLists[i4].f19393i = true
//            } else {
//                mBarLists[i4].f19392h = 0
//                mBarLists[i4].f19393i = false
//            }
//        }
//    }
//
//
//    fun m7036c(i4: Int): Int {
//        var i8 = ((i4 - this.f9903i) / this.f9912r).toInt()
//        if (i8 < 0 || ((mNumLists.isNotEmpty()) && i8 > mNumLists.size - 1)) {
//            i8 = 0
//        }
//        Logcat.d("BatteryBarChart", "getSelectedBarIndex $i8")
//        return i8
//    }
//
//
//    fun m7037d(i4: Int, i8: Int): Int {
//        if (i4 < 0) {
//            return 0
//        }
//        var z11 = true
//        if ((mNumLists.isNotEmpty()) && i4 > mNumLists.size - 1) {
//            return 0
//        }
//        val z10 = i4 == this.f9902h - 1
//        if (mNumLists[i4].level < 0 || mNumLists[i8].level != 0) {
//            z11 = false
//        }
//        if ((!z10 || !z11) && i4 != i8) {
//            return 0
//        }
//        return -1
//    }
//
//    // android.view.View
//    public override fun dispatchHoverEvent(event: MotionEvent): Boolean {
//        val z10: Boolean
//        val barChartTouchHelper = this.f9918x
//        z10 = barChartTouchHelper?.dispatchHoverEvent(event) ?: false
//        return !(!z10 && !super.dispatchHoverEvent(event))
//    }
//
//    // android.view.View
//    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
//        val z10: Boolean
//        val barChartTouchHelper = this.f9918x
//        z10 = barChartTouchHelper?.dispatchKeyEvent(event) ?: false
//        return !(!z10 && !super.dispatchKeyEvent(event))
//    }
//
//
//    fun m7038e(i4: Int): Boolean {
//        val i8: Int
//        val i9 = this.f9902h
//        i8 = if (i9 > 1 && i9 % 2 != this.mIsHalfHour) {
//            1
//        } else {
//            0
//        }
//        val f10 = i4.toFloat()
//        val f11 = this.f9903i
//        return !(f10 >= (this.f9912r * (i9 + i8)) + f11 || f10 <= f11)
//    }
//
//
//    fun m7039f(): Boolean {
//        return this.mSelectIndex == -1
//    }
//
//    val clickPointDescription: String
//        get() {
//            var selectedTime = selectedTime
//            val str = mBarLists[startIndex].f19391g
//            val str2 = mBarLists[endIndex].f19391g
//            val m7037d = m7037d(startIndex, endIndex)
//            var j10 = 3600000 + selectedTime
//            val calendar = Calendar.getInstance()
//            if (m7037d != -1) {
//                if (m7037d == 1) {
//                    selectedTime += 1800000
//                }
//            } else {
//                j10 -= 1800000
//            }
//            calendar.timeInMillis = selectedTime
//            val formatDateTime = DateUtils.formatDateTime(
//                context,
//                calendar.timeInMillis,
//                1
//            )
//            val str3 = DateUtils.formatDateTime(
//                context,
//                calendar.timeInMillis,
//                16
//            ) + formatDateTime
//            calendar.timeInMillis = j10
//            val formatDateTime2 = DateUtils.formatDateTime(
//                context,
//                calendar.timeInMillis,
//                1
//            )
//            val m9243i =
//                context.getString(R.string.power_battery_choose_info)
//            val text = String.format(m9243i, str3, formatDateTime2, str, str2)
//            return BidiFormatter.getInstance().unicodeWrap(text)
//        }
//
//    val endIndex: Int
//        get() {
//            val startIndex = startIndex
//            if (this.mIsHalfHour != 1 || (startIndex != 0 && startIndex != 47)) {
//                return (startIndex + 2) - 1
//            }
//            return startIndex
//        }
//
//    val selectedTime: Long
//        get() {
//            val m934d: Long
//            val levelAndCharge = mNumLists.getOrNull(startIndex)
//            m934d = levelAndCharge?.time ?: BatteryStatisticsHelper.m934d()
//            return m934d - 1800000
//        }
//
//    val selectedTimeSpand: Long
//        get() {
//            if (m7037d(startIndex, endIndex) != 0) {
//                return 1800000L
//            }
//            return 3600000L
//        }
//
//    val startIndex: Int
//        get() {
//            if (this.mSelectIndex != 0 && !m7039f()) {
//                return (this.mSelectIndex * 2) - this.mIsHalfHour
//            }
//            return 0
//        }
//
//
//    fun m7040i() {
//        this.mSelectIndex = -1
//        f9913s.clear()
//        if (mBarLists.size <= 48) {
//            for (i4 in 0..47) {
//                mBarLists[i4].f19392h = 1
//            }
//        }
//        val bVar = this.mCallBack
//        bVar?.mo7053a(BatteryStatisticsHelper.m934d(), 3600000L, true)
//        invalidate()
//    }
//
//
//    fun m7041j() {
//        Logcat.d("BatteryBarChart", "bar isLand $isLandscape")
//        val f10 = ((this.f9907m - this.f9906l) - this.f9914t) / 48
//        this.f9912r = f10
//        this.f9911q = this.f9903i
//        this.f9893A = 0.6666667f * f10
//        this.f9894B = f10 * 0.33333334f
//    }
//
//
//    fun m7042k(i4: Int) {
//        val z10: Boolean
//        if ((m7036c(i4) + this.mIsHalfHour) / 2 != this.mSelectIndex && m7038e(i4)) {
//            setSelectIndex((m7036c(i4) + this.mIsHalfHour) / 2)
//            m7035b()
//            invalidate()
//        }
//        val aVar = this.slideListener
//        if (aVar != null) {
//            z10 = if (this.f9916v == 2) {
//                true
//            } else {
//                false
//            }
//            aVar.mo7052a(z10)
//        }
//    }
//
//    public override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//        m7041j()
//
//        val path = Path()
//        var shouldResetPath = true // 添加控制变量
//
//        // 第一部分：绘制条形图
//        for ((index, barItem) in this.mBarLists.withIndex()) {
//            if (index >= this.f9902h) break
//
//            val isCharging = barItem.f19395k == "true"
//            val drawType = calculateDrawType(index, isCharging)
//
//            // 修复路径重置逻辑
//            if (shouldResetPath || drawType == DRAW_TYPE_NEW_PATH) {
//                path.reset()
//                shouldResetPath = false
//            }
//
//            // 绘制条形图
//            val prevLevel = if (index > 0) {
//                this.mBarLists[index - 1].f19389e.level
//            } else {
//                barItem.f19389e.level
//            }
//
//            barItem.m11669a(canvas, isCharging, prevLevel, path, drawType)
//
//            // 如果当前是路径结束点，下一次需要重置路径
//            if (drawType == DRAW_TYPE_END_PATH) {
//                shouldResetPath = true
//            }
//        }
//
//        // 第二部分：绘制气泡
//        for ((index, barItem) in this.mBarLists.withIndex()) {
//            if (!barItem.f19393i || m7039f()) continue
//            drawBubbleView(canvas, barItem, index)
//        }
//    }
//
//    /**
//     * 计算绘制类型
//     */
//    private fun calculateDrawType(index: Int, isCharging: Boolean): Int {
//        if (index == 0 && this.f9902h > 0) {
//            return if (m7039f()) 4 else 4
//        }
//
//        if (index == this.f9902h - 1) {
//            if (!m7039f()) {
//                val isStart = startIndex == index && selectedTimeSpand == 1800000L
//                val isEnd = endIndex == index && selectedTimeSpand == 3600000L
//                val isNearEnd = this.f9902h - endIndex == 2
//
//                if (isStart || isEnd || isNearEnd) {
//                    return 4
//                }
//            }
//            return 3
//        }
//
//        if (index < this.mBarLists.size && index >= 1) {
//            if (m7039f()) {
//                if (index != this.mBarLists.size - 1) {
//                    val isG = m7032g(index, this.mBarLists)
//                    val isH = m7033h(index, this.mBarLists)
//
//                    return when {
//                        isG && isH -> 4
//                        !isG -> 1
//                        else -> 1
//                    }
//                }
//            } else {
//                val isStart = index == startIndex && m7033h(index, this.mBarLists)
//                val isEnd = index == endIndex && m7032g(index, this.mBarLists)
//
//                if (isStart || isEnd) {
//                    return 4
//                }
//
//                val isNear = index == this.endIndex + 1 || index == this.startIndex
//                val isBoundary = index == this.f9902h - 1 || index == this.startIndex - 1
//
//                if (isNear || isBoundary || index == this.endIndex) {
//                    return 1
//                }
//            }
//        }
//
//        return 1
//    }
//
//    /**
//     * 绘制气泡视图
//     */
//    private fun drawBubbleView(canvas: Canvas, barItem: BatteryStackBarData, index: Int) {
//        val barDataState = barItem.f19394j
//        val size = this.mBarLists.size - 2
//        var levelAndCharge = barItem.f19389e
//
//        // 获取最高电平值
//        if (index < size) {
//            val nextLevel = this.mBarLists[index + 1].f19389e
//            if (levelAndCharge.level < nextLevel.level) {
//                levelAndCharge = nextLevel
//            }
//        }
//
//        val startY = barItem.f19386b
//        Logcat.d("BatteryBarChart", "it.y2 is $startY")
//        Logcat.d("BatteryBarChart", "it.level2 is ${barItem.f19391g}")
//
//        val height = this.f9909o - this.f9908n
//        val levelRatio = (100 - levelAndCharge.level).toFloat() / 100f
//        val bubbleY = height * levelRatio + startY
//
//        Logcat.d(
//            "BatteryBarChart",
//            "drawBubbleView chart startY--$startY, height--$height, level--${levelAndCharge.level}"
//        )
//        Logcat.d("BatteryBarChart", "barDataState is $barDataState")
//        Logcat.d("BatteryBarChart", "startY is $startY")
//
//        val startX = barItem.f19385a
//        val (bubbleStartX, bubbleOffset) = calculateBubblePosition(barDataState, startX)
//        val bubbleStartY = bubbleY - bubbleOffset
//
//        Logcat.d(
//            "BatteryBarChart",
//            "bubbleStartX is $bubbleStartX, bubbleStartY is $bubbleStartY"
//        )
//
//        val selectedItem = SelectedItem(bubbleStartX, bubbleStartY, selectedTime, this.f9910p.toFloat())
//        selectedItem.state = this.m7037d(startIndex, endIndex)
//
//        if (!ScreenReaderUtils.m10472c()) {
//            drawBubble(canvas, BubbleView(context, selectedItem))
//        }
//    }
//
//    /**
//     * 计算气泡位置
//     */
//    private fun calculateBubblePosition(barDataState: Int, startX: Float): Pair<Float, Float> {
//        return when (barDataState) {
//            -1 -> Pair(startX + (this.f9893A / 2), dp2px(3.0f))
//            1 -> Pair(startX + this.f9893A + (this.f9894B / 2), dp2px(3.0f))
//            2 -> Pair(startX - (this.f9894B / 2), dp2px(3.0f))
//            else -> Pair(startX + (this.f9893A / 2), dp2px(3.0f))
//        }
//    }
//
//    /**
//     * 绘制气泡
//     */
//    private fun drawBubble(canvas: Canvas, bubbleView: BubbleView) {
//        val path = Path()
//        val pointY = bubbleView.f22160h
//        Logcat.d("BubbleView", "pointY is $pointY")
//
//        val startX = bubbleView.f22159g
//        path.moveTo(startX, pointY)
//
//        val halfWidth = bubbleView.f22165m.toFloat()
//        val endX = startX + halfWidth * 2
//        val topY = pointY - bubbleView.f22166n
//        val adjustedTopY = topY - 1.0f
//
//        val maxX = bubbleView.f22157e - bubbleView.f22170r
//        val arrowHeight = bubbleView.f22163k
//        val minX = bubbleView.f22155c
//        val paddingLeft = bubbleView.f22169q
//        val bubbleHeight = bubbleView.f22168p
//
//        // 计算气泡路径
//        val (pathX, pathY) = calculateBubblePath(
//            endX, maxX, adjustedTopY, topY, arrowHeight, bubbleHeight,
//            startX, halfWidth, minX, paddingLeft, bubbleView.f22170r,
//            bubbleView.f22158f, bubbleView.f22167o, bubbleView.f22156d
//        )
//
//        path.lineTo(pathX, pathY)
//        path.close()
//
//        // 绘制气泡路径
//        canvas.drawPath(path, bubbleView.f22153a)
//
//        // 绘制气泡内容
//        drawBubbleContent(canvas, bubbleView, startX, halfWidth, topY)
//    }
//
//    /**
//     * 计算气泡路径
//     */
//    private fun calculateBubblePath(
//        endX: Float, maxX: Float, adjustedTopY: Float, topY: Float,
//        arrowHeight: Float, bubbleHeight: Int, startX: Float, halfWidth: Float,
//        minX: Float, paddingLeft: Float, cornerRadius: Float,
//        isFlipped: Boolean, paddingRight: Int, maxWidth: Float
//    ): Pair<Float, Float> {
//        var pathX = endX
//        var pathY = adjustedTopY
//
//        if (pathX > maxX) {
//            pathX = maxX
//            pathY = topY - (bubbleHeight / 2) - (arrowHeight / 2)
//        } else {
//            val rightBound = minX + paddingLeft + cornerRadius
//            if (pathX >= rightBound) {
//                // 正常情况
//                pathY = adjustedTopY
//            } else {
//                pathX = rightBound
//                pathY = topY - (bubbleHeight / 2) - (arrowHeight / 2)
//            }
//        }
//
//        if (isFlipped) {
//            val leftX = startX - halfWidth
//            if (leftX < minX + paddingLeft) {
//                pathX = leftX
//                pathY = topY - (bubbleHeight / 2) - (arrowHeight / 2)
//            } else {
//                val rightBound = maxWidth - paddingLeft - paddingRight
//                if (leftX <= rightBound) {
//                    // 正常情况
//                    pathY = adjustedTopY
//                } else {
//                    pathX = rightBound
//                    pathY = adjustedTopY
//                }
//            }
//        }
//
//        return Pair(pathX, pathY)
//    }
//
//    /**
//     * 绘制气泡内容
//     */
//    private fun drawBubbleContent(
//        canvas: Canvas, bubbleView: BubbleView,
//        startX: Float, halfWidth: Float, topY: Float
//    ) {
//        val centerX = startX + halfWidth
//        val rectF = bubbleView.f22172t
//        val bubbleWidth = bubbleView.f22162j
//        val padding = bubbleView.f22173u
//
//        // 确定气泡位置（左、中、右）
//        when {
//            centerX > bubbleView.f22157e -> {
//                // 气泡在右侧
//                val left = bubbleView.f22157e - bubbleWidth - (padding * 2)
//                rectF.set(left, topY - padding, bubbleView.f22157e.toFloat(), topY)
//            }
//            startX - halfWidth > bubbleView.f22155c -> {
//                // 气泡在中间
//                val halfBubbleWidth = bubbleWidth / 2
//                val left = centerX - halfBubbleWidth - padding
//                val right = centerX + halfBubbleWidth + padding
//                rectF.set(left, topY - padding, right, topY)
//            }
//            else -> {
//                // 气泡在左侧
//                val left = bubbleView.f22155c + bubbleView.f22169q
//                rectF.set(left, topY - padding, left + bubbleWidth + (padding * 2), topY)
//            }
//        }
//
//        // 绘制圆角矩形
//        canvas.drawRoundRect(rectF, bubbleView.f22170r, bubbleView.f22170r, bubbleView.f22153a)
//
//        // 绘制文本
//        val paint = bubbleView.f22154b
//        val fontMetrics = paint.fontMetrics
//        val centerY = rectF.centerY() - (fontMetrics.top / 2) - (fontMetrics.bottom / 2)
//
//        if (bubbleView.f22158f) {
//            canvas.save()
//            canvas.scale(-1f, 1f, rectF.centerX(), rectF.centerY())
//            canvas.drawText(bubbleView.f22161i, rectF.left, centerY, paint)
//            canvas.restore()
//        } else {
//            canvas.drawText(bubbleView.f22161i, rectF.left, centerY, paint)
//        }
//    }
//
//    public  fun onDraw2(canvas: Canvas) {
//        var i4: Int
//        var i8: Int
//        var it: Iterator<*>
//        var i9: Int
//        var bubbleStartX: Float = 0f
//        var m10476a: Float = 0f
//        var f11: Float
//        var f12: Float
//        var f13: Float
//        var f14: Float
//        var bubbleView: BubbleView
//        var f15: Float
//        var z10: Boolean
//        var f16: Float
//        var f17: Float
//        var f18: Float
//        var f19: Float = 0f
//        var f20: Float
//        var f21: Float
//        var i10: Int
//        var z11: Boolean
//        var z12: Boolean
////        var z13: Boolean
//        var i11: Int = 0
//        var z14: Boolean
//        var z15: Boolean
//        var z16: Boolean
//        super.onDraw(canvas)
//        m7041j()
//        var path = Path()
//        val it2 = this.mBarLists.iterator()
//        var i12 = 0
//        while (true) {
//            i4 = 1
//            i8 = 2
//            if (!it2.hasNext()) {
//                break
//            }
//            val next = it2.next()
//            val i13 = i12 + 1
//            if (i12 >= 0) {
//                if (i12 >= this.f9902h) {
//                    break
//                }
//                val m10301a = next.f19395k == "true"
//                val arrayList = this.mBarLists
//                if (i12 == 0) {
//                    val i14 = if (m7039f()) 4 else 4
//                    i10 = i14
//                } else {
//                    if (i12 == this.f9902h - 1) {
//                        if (!m7039f()) {
//                            z14 = startIndex == i12 && selectedTimeSpand == 1800000L
//                            z15 = endIndex == i12 && selectedTimeSpand == 3600000L
//                            z16 = this.f9902h - endIndex == 2
//                            if (!z14) {
//                                if (z15) {
//                                }
//                                if (endIndex == i12 - 1) {
//                                }
//                            }
//                            i11 = 4
//                        }
//                        i10 = i11
//                    } else if (i12 < arrayList.size && i12 >= 1) {
//                        if (m7039f()) {
//                            if (i12 != arrayList.size - 1) {
//                                if (m7032g(i12, arrayList) && m7033h(i12, arrayList)) {
//                                    i8 = 4
//                                } else {
//                                    if (!m7032g(i12, arrayList)) {
//                                    }
//                                    i8 = 1
//                                }
//                                i10 = i8
//                            }
//                        } else {
//                            val startIndex = startIndex
//                            val endIndex = endIndex
//                            if ((i12 == startIndex && m7033h(
//                                    i12,
//                                    arrayList
//                                )) || (i12 == endIndex && m7032g(i12, arrayList))
//                            ) {
//                                i10 = 4
//                            } else {
//                                z11 = i12 == this.endIndex + 1 || i12 == this.startIndex
//                                if (!z11) {
//                                    z12 =
//                                        i12 == this.f9902h - 1 || i12 == this.startIndex - 1
////                                    z13 = z12 || i12 == this.endIndex
//                                }
//                                i8 = 1
//                                i10 = i8
//                            }
//                        }
//                    } else {
//                        i10 = 1
//                    }
//                    i11 = 3
//                    i10 = i11
//                }
//                if (i10 == 1 || i10 == 4) {
//                    path = Path()
//                }
//                val path2 = path
//                if (i12 == 0 && this.f9902h > 0) {
//                    next.m11669a(
//                        canvas,
//                        m10301a,
//                        next.f19389e.level,
//                        path2,
//                        i10
//                    )
//                }
//                if (1 > i12 || i12 >= this.f9902h) {
//                    i4 = 0
//                }
//                if (i4 != 0) {
//                    next.m11669a(
//                        canvas,
//                        m10301a,
//                        this.mBarLists[i12 - 1].f19389e.level,
//                        path2,
//                        i10
//                    )
//                }
//                i12 = i13
//                path = path2
//            } else {
////                NotifyPreference.m14197k0();
//                throw ArithmeticException("Index overflow has happened.")
//            }
//        }
//        var it3 = this.mBarLists.iterator()
//        var i15 = 0
//        while (it3.hasNext()) {
//            val next2 = it3.next()
//            val i16 = i15 + 1
//            if (i15 >= 0) {
//                if (next2.f19393i && !m7039f()) {
//                    val barDataState = next2.f19394j
//                    val size = this.mBarLists.size - i8
//                    var levelAndCharge = next2.f19389e
//                    if (i15 < size) {
//                        val levelAndCharge2 = this.mBarLists[i16].f19389e
//                        if (levelAndCharge.level < levelAndCharge2.level) {
//                            levelAndCharge = levelAndCharge2
//                        }
//                    }
//                    val startY = next2.f19386b
//                    Logcat.d("BatteryBarChart", "it.y2 is $startY")
//                    Logcat.d("BatteryBarChart", "it.level2 is " + next2.f19391g)
//                    val f23 =
//                        (((this.f9909o - this.f9908n) * ((100 - levelAndCharge.level).toFloat())) / (100f)) + startY
//                    Logcat.d(
//                        "BatteryBarChart",
//                        "drawBubbleView chart startY--" + startY + " , height--" + height + " , level--" + levelAndCharge.level
//                    )
//                    Logcat.d("BatteryBarChart", "barDataState is $barDataState")
//                    Logcat.d("BatteryBarChart", "startY is $startY")
//                    val f24 = next2.f19385a
//                    if (barDataState != -1) {
//                        if (barDataState != i4) {
//                            if (barDataState == 2) {
//                                bubbleStartX = f24 - (this.f9894B / 2)
//                                m10476a = dp2px(3.0f)
//                            }
//                        } else {
//                            bubbleStartX = f24 + this.f9893A + (this.f9894B / 2)
//                            m10476a = dp2px(3.0f)
//                        }
//                    } else {
//                        bubbleStartX = f24 + (this.f9893A / 2)
//                        m10476a = dp2px(3.0f)
//                    }
//                    val bubbleStartY = f23 - m10476a
//                    Logcat.d(
//                        "BatteryBarChart",
//                        "bubbleStartX is $bubbleStartX    bubbleStartY is $bubbleStartY "
//                    )
//                    val selectedItem = SelectedItem(bubbleStartX, bubbleStartY, selectedTime, this.f9910p.toFloat())
//                    selectedItem.state = this.m7037d(startIndex, endIndex)
//                    val context = context
//                    val bubbleView2 = BubbleView(context, selectedItem)
//                    if (!ScreenReaderUtils.m10472c()) {
//                        val path3 = Path()
//                        val pointY = bubbleView2.f22160h
//                        Logcat.d("BubbleView", "pointY is $pointY")
//                        val f27 = bubbleView2.f22159g
//                        path3.moveTo(f27, pointY)
//                        val f28 = bubbleView2.f22165m.toFloat()
//                        val f29 = f28 + f27
//                        val f30 = pointY - bubbleView2.f22166n
//                        val f31 = f30 - 1.0f
//                        val f32 = bubbleView2.f22157e
//                        val f33 = bubbleView2.f22170r
//                        val f34 = f32 - f33
//                        val f35 = bubbleView2.f22163k
//                        val f36 = bubbleView2.f22155c
//                        it = it3
//                        val f37 = bubbleView2.f22169q
//                        i9 = i16
//                        val i18 = bubbleView2.f22168p
////                        str = str2
//                        if (f29 > f34) {
//                            f11 = f31
//                            f12 = f32
//                            f14 = (f30 - (i18 / 2)) - (f35 / 2)
//                            f13 = f12
//                        } else {
//                            f11 = f31
//                            f12 = f32
//                            f13 = f37 + f33 + f36
//                            if (f29 >= f13) {
//                                Logcat.d("BubbleView", "normal")
//                                f13 = f29
//                            }
//                            f14 = f11
//                        }
//                        val z17 = bubbleView2.f22158f
//                        val f38 = f13
//                        val i19 = bubbleView2.f22167o
//                        var f39 = f14
//                        val f40 = bubbleView2.f22156d
//                        if (z17) {
//                            val f41 = f27 - f28
//                            if (f41 < f33 + f36) {
//                                bubbleView = bubbleView2
//                                f15 = f29
//                                z10 = z17
//                                f39 = (f30 - (i18 / 2)) - (f35 / 2)
//                                f16 = f36
//                            } else {
//                                bubbleView = bubbleView2
//                                f15 = f29
//                                z10 = z17
//                                f16 = (f40 - f36) - i19
//                                if (f41 <= f16) {
//                                    Logcat.d("BubbleView", "normal")
//                                    f16 = f41
//                                }
//                                f39 = f11
//                            }
//                        } else {
//                            bubbleView = bubbleView2
//                            f15 = f29
//                            z10 = z17
//                            f16 = f38
//                        }
//                        path3.lineTo(f16, f39)
//                        var f42 = f27 - f28
//                        if (f42 < f37 + f33 + f36) {
//                            f42 = f37 + f36
//                            f17 = (f30 - (i18 / 2)) - (f35 / 2)
//                        } else {
//                            val f43 = (f12 - f37) - f28
//                            if (f42 > f43) {
//                                f42 = f43
//                            } else {
//                                Logcat.d("BubbleView", "normal")
//                            }
//                            f17 = f11
//                        }
//                        if (z10) {
//                            if (f15 > ((f40 - f36) - f37) - f33) {
//                                f42 = f12 - f37
//                                f18 = (f30 - (i18 / 2)) - (f35 / 2)
//                            } else {
//                                val f44 = i19 + f36 + f37
//                                if (f15 < f44) {
//                                    f21 = f44
//                                } else {
//                                    Logcat.d("BubbleView", "normal")
//                                    f21 = f15
//                                }
//                                f42 = f21
//                                f18 = f11
//                            }
//                        } else {
//                            f18 = f17
//                        }
//                        path3.lineTo(f42, f18)
//                        path3.close()
//                        val bubbleView3 = bubbleView
//                        val paint = bubbleView3.f22153a
//                        canvas.drawPath(path3, paint)
//                        val f45 = 2f
//                        val f46 = bubbleView3.f22164l / f45
//                        val f47 = f46 + f27
//                        val rectF = bubbleView3.f22172t
//                        val f48 = bubbleView3.f22162j
//                        val i20 = bubbleView3.f22173u
//                        if (f47 > f12) {
//                            Logcat.d("BubbleView", "bubble right")
//                            f19 = (f12 - f48) - (i19 * 2)
//                            rectF[f19, f30 - i20, f12] = f30
//                        } else if (f27 - f46 > f36) {
//                            Logcat.d("BubbleView", "bubble middle")
//                            val f49 = f48 / f45
//                            f20 = f27 - f49
//                            val f50 = i19.toFloat()
//                            rectF[f20 - f50, f30 - i20, f49 + f27 + f50] = f30
//                            canvas.drawRoundRect(rectF, f33, bubbleView3.f22171s, paint)
//                            val paint2 = bubbleView3.f22154b
//                            val fontMetrics = paint2.fontMetrics
//                            val centerY =
//                                (rectF.centerY() - (fontMetrics.top / f45)) - (fontMetrics.bottom / f45)
//                            if (z10) {
//                                canvas.scale(-1.0f, 1.0f, (f48 / f45) + f20, f35 / f45)
//                            }
//                            canvas.drawText(bubbleView3.f22161i, f20, centerY, paint2)
//                            it3 = it
//                            i15 = i9
//                            i8 = 2
//                            i4 = 1
//                        } else {
//                            Logcat.d("BubbleView", "bubble left")
//                            f19 = f36 + f37
//                            rectF[f19, f30 - i20, f48 + f19 + (i19 * 2)] = f30
//                        }
//                        f20 = i19 + f19
//                        canvas.drawRoundRect(rectF, f33, bubbleView3.f22171s, paint)
//                        val paint22 = bubbleView3.f22154b
//                        val fontMetrics2 = paint22.fontMetrics
//                        val centerY2 =
//                            (rectF.centerY() - (fontMetrics2.top / f45)) - (fontMetrics2.bottom / f45)
//                        if (z10) {
//                        }
//                        canvas.drawText(bubbleView3.f22161i, f20, centerY2, paint22)
//                        it3 = it
//                        i15 = i9
//                        i8 = 2
//                        i4 = 1
//                    }
//                }
//                it = it3
//                i9 = i16
//                it3 = it
//                i15 = i9
//                i8 = 2
//                i4 = 1
//            } else {
//                throw ArithmeticException("Index overflow has happened.")
//            }
//        }
//    }
//
//    public override fun onMeasure(i4: Int, i8: Int) {
//        super.onMeasure(i4, i8)
//        setMeasuredDimension(
//            MeasureSpec.getSize(i4),
//            (this.f9919y + this.f9905k + this.f9920z).toInt()
//        )
//    }
//
//    public override fun onSizeChanged(i4: Int, i8: Int, i9: Int, i10: Int) {
//        val m14219b: Float
//        super.onSizeChanged(i4, i8, i9, i10)
//        val format = NumberFormat.getPercentInstance().format(100 / 100.0)
//        val textPaint =
//            createPaint(resources.getDimensionPixelSize(R.dimen.battery_history_chart_dateText_size))
//        val pair = textPaint.measureTextSize(format)
//        val m11668d = pair.first
//        val f10 = if (isLandscape) {
//            3.0f
//        } else {
//            2.0f
//        }
//        var m10476a = dp2px(f10).toFloat()
//        val z10 = true
//        m14219b = if (z10) {
//            m10476a
//        } else {
//            AttributeParseUtils.m14219b(33620168)
//        }
//        this.f9903i = m14219b
//        if (!z10) {
//            m10476a = AttributeParseUtils.m14219b(33620170)
//        }
//        this.f9904j = m10476a
//        this.f9914t = resources.getDimension(R.dimen.battery_maigin_text_percent) + m11668d
//        this.f9910p = width
//        height
//        this.f9908n = this.f9919y
//        this.f9909o = i8 - this.f9905k
//        this.f9906l = this.f9903i
//        this.f9907m = i4 - this.f9904j
//        mIsLayoutRtl
//        m7041j()
//        m7034a()
//        val size = mNumLists.size
//        if (size != 0 && mBarLists.size != 0) {
//            for (i11 in 0..<size) {
//                if (mNumLists[i11].level != 0 && i11 != 0) {
//                    this.lastIndex = i11
//                }
//            }
//            val i12 = size - 1
//            if (mNumLists[i12].level != 0) {
//                this.lastIndex = i12
//            }
//            val formatDateTime = DateUtils.formatDateTime(
//                context,
//                mNumLists[0].time, 17
//            )
//            val formatDateTime2 = DateUtils.formatDateTime(
//                context, mNumLists[lastIndex].time, 17
//            )
//            val m9243i = context.getString(R.string.power_battery_choose_info)
//            val format2 = String.format(
//                m9243i, formatDateTime, formatDateTime2,
//                mBarLists[0].f19391g, mBarLists[lastIndex].f19391g
//            )
//            contentDescription = format2
//        }
//    }
//
//    override fun onTouchEvent(ev: MotionEvent): Boolean {
//        val x10 = ev.x.toInt()
//        val y10 = ev.y.toInt()
//        val action = ev.action
//        val pointF = this.f9917w
//        var z10 = false
//        if (action != 0) {
//            val f10 = this.f9919y
//            if (action != 2) {
//                if (action != 3) {
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
//                        this.mCallBack?.mo7053a(selectedTime, selectedTimeSpand, false)
//                    }
//                    this.slideListener?.mo7052a(true)
//                    return true
//                }
//                if (!m7039f()) {
//                    this.mCallBack?.mo7053a(selectedTime, selectedTimeSpand, false)
//                }
//                this.slideListener?.mo7052a(true)
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
//
//    fun setListData(listData: List<LevelAndCharge>) {
//        val j10: Long
//        mNumLists.clear()
//        this.f9902h = listData.size
//        val it = listData.iterator()
//        while (it.hasNext()) {
//            mNumLists.add(it.next())
//        }
//        val currentTimeMillis = System.currentTimeMillis()
//        j10 = if (this.f9902h <= 0) {
//            BatteryStatisticsHelper.m935e(currentTimeMillis) + (currentTimeMillis - 1800000)
//        } else {
//            mNumLists.last().time
//        }
//        val i8 = this.f9902h
//        var i9 = 1
//        val i10 = 0
//        val i4: Int = 48 - i8
//        if (i8 <= 48 && 1 <= i4) {
//            var i11 = 1
//            while (true) {
//                mNumLists.add(LevelAndCharge(i10, "false", (i11 * 1800000L) + j10))
//                if (i11 == i4) {
//                    break
//                } else {
//                    i11++
//                }
//            }
//        }
//        if (mNumLists.isNotEmpty()) {
//            val j11 = mNumLists[0].time
//            val context = context
//            val calendar = Calendar.getInstance()
//            calendar.timeInMillis = j11
//            val formatDateTime = DateUtils.formatDateTime(context, calendar.timeInMillis, 1)
//            val m11850E0 = formatDateTime.indexOf(':') + 1
//            val substring = formatDateTime.substring(m11850E0, m11850E0 + 2)
//            val res = substring.toInt()
//            if (res != 0) {
//                i9 = 0
//            }
//        }
//
//        this.mIsHalfHour = i9
//        val barChartTouchHelper = BarChartTouchHelper(this)
//        this.f9918x = barChartTouchHelper
//        ViewCompat.setAccessibilityDelegate(this, barChartTouchHelper)
////        requestLayout()
//    }
//
//    companion object {
//        private const val DRAW_TYPE_RESET_PATH = 1
//        private const val DRAW_TYPE_NEW_PATH = 4
//        private const val DRAW_TYPE_CONTINUE = 3
//        private const val DRAW_TYPE_END_PATH = 2 // 新增：表示路径结束
//        fun m7032g(index: Int, barData: ArrayList<BatteryStackBarData>): Boolean {
//            return barData[index].f19389e.charge != barData[index - 1].f19389e.charge
//        }
//
//        fun m7033h(index: Int, barData: ArrayList<BatteryStackBarData>): Boolean {
//            return barData[index].f19389e.charge != barData[index + 1].f19389e.charge
//        }
//    }
//}
