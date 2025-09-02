package com.android.hwsystemmanager.widgets

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import com.android.hwsystemmanager.utils.AttributeParseUtils
import com.android.hwsystemmanager.utils.TimeUtil
import com.android.hwsystemmanager.SelectedItem
import com.android.hwsystemmanager.utils.Logcat
import com.android.hwsystemmanager.utils.isLayoutRtl
import com.android.hwsystemmanager.utils.measureTextSize
import com.android.hwsystemmanager.utils.r
import com.android.hwsystemmanager.utils.v
import com.android.hwsystemmanager.utils.dp2px
import kotlin.math.max

class BubbleView(context: Context, selectedItem: SelectedItem) {
    @JvmField
    val f22153a: Paint
    @JvmField
    val f22154b: Paint
    @JvmField
    val f22155c: Float
    @JvmField
    val screenWidth: Float
    @JvmField
    val f22157e: Float
    @JvmField
    val f22158f: Boolean
    @JvmField
    val mStartX: Float
    @JvmField
    val mStartY: Float
    @JvmField
    var mText: String
    @JvmField
    val f22162j: Float
    @JvmField
    val f22163k: Float
    @JvmField
    val f22164l: Float
    @JvmField
    val f22165m: Int
    @JvmField
    val f22166n: Int
    @JvmField
    val f22167o: Int
    @JvmField
    val mRadius: Int
    @JvmField
    val f22169q: Float
    @JvmField
    val f22170r: Float
    @JvmField
    val f22171s: Float
    @JvmField
    val f22172t: RectF
    @JvmField
    var f22173u: Int

    init {
        val m14219b: Float
        val paint = Paint(1)
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.color = Color.BLACK//AttributeParseUtils.m14218a(33620238, false)
        this.f22153a = paint
        val paint2 = Paint(1)
        paint2.isAntiAlias = true
        paint2.style = Paint.Style.FILL
        val m14218a = AttributeParseUtils.m14218a(android.R.attr.textColorPrimaryInverse, false)
        paint2.textSize = AttributeParseUtils.m14219b(33620202)
        paint2.color = m14218a
        this.f22154b = paint2
        val z10 = Build.VERSION.SDK_INT >= 27
        m14219b = if (z10) {
            0.0f
        } else {
            AttributeParseUtils.m14219b(33620168)
        }
        this.f22155c = m14219b
        val m14219b2 = if (z10) 0.0f else AttributeParseUtils.m14219b(33620170)
        val screenWidth = selectedItem.screenWidth
        this.screenWidth = screenWidth
        this.f22157e = screenWidth - m14219b2
        this.f22158f = isLayoutRtl
        this.f22165m = context.dp2px(7)
        this.f22166n = context.dp2px(7)
        val m10476a = context.dp2px(12)
        this.f22167o = m10476a
        val radius: Int = context.dp2px(8)
        this.mRadius = radius
        this.f22169q = (-m14219b) / 2f
        this.f22172t = RectF()
        val m10476a3: Int = context.dp2px(24)
        this.f22173u = m10476a3
        val m11216d: String = TimeUtil.formatBatteryChooseTime(selectedItem.state, selectedItem.time)
        this.mText = m11216d
        paint2.textSize = context.dp2px(14f)
        if (context.v() || context.r()) {
            paint2.textSize = 76.0f
        }
        val pair = paint2.measureTextSize(m11216d)
        val width = pair.width().toFloat()
        Logcat.d("BatteryHistoryChartPaintFactory", "width is $width，m11216d:$m11216d")
        this.f22162j = width
        val height = pair.height().toFloat()
        Logcat.d("BatteryHistoryChartPaintFactory", "height is $height")
        this.f22163k = height
        this.mStartX = selectedItem.startX
        this.mStartY = selectedItem.startY
        Logcat.d("BubbleView", "text is $m11216d")
        val f12 = radius.toFloat()
        val f13 = (height / 2f) + f12
        this.f22170r = f13
        this.f22171s = f13
        val f14 = (f13 * 2f) + width + m10476a
        this.f22164l = f14
        this.f22173u = max(m10476a3, (f12 + height).toInt())
        Logcat.d("BubbleView", "bubbleWidth is $f14")
    }

    val width: Float
        get() = /*f22173u * 2f +*/ f22162j
    val height: Float
        get() = f22173u * 2 + f22163k
}