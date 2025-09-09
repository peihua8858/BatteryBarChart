package com.android.hwsystemmanager.multicolor

import android.content.Context
import android.content.res.Resources
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import com.android.hwsystemmanager.SelectedItem
import com.android.hwsystemmanager.utils.Logcat
import com.android.hwsystemmanager.utils.TimeUtil
import com.android.hwsystemmanager.utils.dLog
import com.android.hwsystemmanager.utils.dp2px
import com.android.hwsystemmanager.utils.isFontScaleNear2_0
import com.android.hwsystemmanager.utils.isFontScaleNear3_2
import com.android.hwsystemmanager.utils.isLayoutRtl
import com.android.hwsystemmanager.utils.isOreoR1
import com.android.hwsystemmanager.utils.parseDimensionFromAttribute
import com.android.hwsystemmanager.utils.retrieveColorFromAttribute
import kotlin.math.max

class BubbleView(context: Context, selectedItem: SelectedItem) {

    val bubbleBgPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.isAntiAlias = true
        this.style = Paint.Style.FILL
        this.color = context.retrieveColorFromAttribute(android.R.attr.colorAccent)
    }

    val textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.isAntiAlias = true
        this.style = Paint.Style.FILL
        this.textSize = context.dp2px(12f)
        this.color = context.retrieveColorFromAttribute(android.R.attr.textColorPrimaryInverse)
    }
    val f22155c: Float
    val viewWidth: Float = selectedItem.screenWidth
    val isRtl: Boolean = isLayoutRtl
    val f22157e: Float
    val startX: Float = selectedItem.startX
    val startY: Float = selectedItem.startY
    var f22161i: String
    val textWidth: Float
    val textHeight: Float
    val f22164l: Float
    val f22165m: Int = context.dp2px(7)
    val f22166n: Int = context.dp2px(7)
    val horizontalPadding: Int = context.dp2px(12)
    val verticalPadding: Float = context.dp2px(8f)
    val f22169q: Float
    val f22170r: Float
    val f22171s: Float
    val bubbleRectF: RectF = RectF()
    var f22173u: Int

    init {
        val isOMR1 = isOreoR1
        val m14219b = if (isOMR1) {
            0.0f
        } else {
            context.parseDimensionFromAttribute(33620168)
        }
        this.f22155c = m14219b
        val m14219b2 = if (isOMR1) 0.0f else context.parseDimensionFromAttribute(33620170)
        this.f22157e = viewWidth - m14219b2
        this.f22169q = (-m14219b) / 2f
        val dp24 = context.dp2px(24)
        val formatTime = TimeUtil.formatBatteryChooseTime(selectedItem.state, selectedItem.time)
        this.f22161i = formatTime
        if (context.isFontScaleNear3_2() || context.isFontScaleNear2_0()) {
            textPaint.textSize = 76.0f
        }
        val rect = Rect()
        textPaint.getTextBounds(formatTime, 0, formatTime.length, rect)
        val width = rect.width().toFloat()
        val height = rect.height().toFloat()
        Logcat.d("BatteryHistoryChartPaintFactory", "width is $width")
        this.textWidth = width
        Logcat.d("BatteryHistoryChartPaintFactory", "height is $height")
        this.textHeight = height
        Logcat.d("BubbleView", "text is $formatTime")
        val f13 = (height / 2f) + verticalPadding
        this.f22170r = f13
        this.f22171s = f13
        val f14 = (f13 * 2f) + width + horizontalPadding
        this.f22164l = f14
        this.f22173u = max(dp24, (verticalPadding + height).toInt())
        Logcat.d("BubbleView", "bubbleWidth is $f14")
        dLog { "getResourceName>>>>>colorAccent:${Resources.getSystem().getResourceName(android.R.attr.colorAccent)}" }
        dLog { "getResourceName>>>>>colorAccent1:${context.resources.getResourceName(android.R.attr.colorAccent)}" }
        try {
            dLog { "getResourceName>>>>>33620170:${Resources.getSystem().getResourceName(33620170)}" }
            dLog { "getResourceName>>>>>33620238:${Resources.getSystem().getResourceName(33620238)}" }
            dLog { "getResourceName>>>>>33620202:${Resources.getSystem().getResourceName(33620202)}" }
            dLog { "getResourceName>>>>>33620168:${Resources.getSystem().getResourceName(33620168)}" }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}
