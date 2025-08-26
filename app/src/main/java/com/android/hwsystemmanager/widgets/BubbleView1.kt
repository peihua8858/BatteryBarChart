package com.android.hwsystemmanager.widgets

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.os.Build
import com.android.hwsystemmanager.SelectedItem
import com.android.hwsystemmanager.utils.AttributeParseUtils
import com.android.hwsystemmanager.utils.Logcat
import com.android.hwsystemmanager.utils.TimeUtil
import com.android.hwsystemmanager.utils.isLayoutRtl
import com.android.hwsystemmanager.utils.r
import com.android.hwsystemmanager.utils.v
import com.android.settings.util.dp2px
import kotlin.math.max

class BubbleView1(context: Context, selectedItem: SelectedItem) {
    @JvmField
    val f22153a: Paint
    @JvmField
    val f22154b: Paint
    @JvmField
    val f22155c: Float
    @JvmField
    val f22156d: Float
    @JvmField
    val f22157e: Float
    @JvmField
    val f22158f: Boolean
    @JvmField
    val startX: Float
    @JvmField
    val startY: Float
    @JvmField
    var f22161i: String
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
    val f22168p: Int
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
//        val m14218a = AttributeParseUtils.m14218a(android.R.attr.textColorPrimaryInverse, false)
        paint2.textSize = context.dp2px(14f)//AttributeParseUtils.m14219b(33620202)
        paint2.color = Color.WHITE
        this.f22154b = paint2
        val z10 = Build.VERSION.SDK_INT >= 27
        m14219b = if (z10) {
            0.0f
        } else {
            AttributeParseUtils.m14219b(33620168)
        }
        this.f22155c = m14219b
        val m14219b2 = if (z10) 0.0f else AttributeParseUtils.m14219b(33620170)
        val f10 = selectedItem.screenWidth
        this.f22156d = f10
        this.f22157e = f10 - m14219b2
        this.f22158f = isLayoutRtl
        this.f22161i = ""
        this.f22165m = context.dp2px(7)
        this.f22166n = context.dp2px(7)
        val m10476a: Int = context.dp2px(12)
        this.f22167o = m10476a
        val m10476a2: Int = context.dp2px(8)
        this.f22168p = m10476a2
        val f11 = 2f
        this.f22169q = (-m14219b) / f11
        this.f22172t = RectF()
        val m10476a3: Int = context.dp2px(24)
        this.f22173u = m10476a3
        val m11216d: String = TimeUtil.m11216d(selectedItem.state, selectedItem.time)
        this.f22161i = m11216d
        if (context.v() || context.r()) {
            paint2.textSize = 76.0f
        }
        val rect = Rect()
        paint2.getTextBounds(m11216d, 0, m11216d.length, rect)
        val width = rect.width().toFloat()
        Logcat.d("BatteryHistoryChartPaintFactory", "width is $width")
        this.f22162j = width
        val rect2 = Rect()
        paint2.getTextBounds(m11216d, 0, m11216d.length, rect2)
        val height = rect2.height().toFloat()
        Logcat.d("BatteryHistoryChartPaintFactory", "height is $height")
        this.f22163k = height
        this.startX = selectedItem.startX
        this.startY = selectedItem.startY
        Logcat.d("BubbleView", "text is $m11216d")
        val f12 = m10476a2.toFloat()
        val f13 = (height / f11) + f12
        this.f22170r = f13
        this.f22171s = f13
        val f14 = (f13 * f11) + width + m10476a
        this.f22164l = f14
        this.f22173u = max(m10476a3, (f12 + height).toInt())
        Logcat.d("BubbleView", "bubbleWidth is $f14")
    }
}
