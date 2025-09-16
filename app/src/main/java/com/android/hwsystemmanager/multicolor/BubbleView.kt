package com.android.hwsystemmanager.multicolor

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
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
    var formatTime: String
    val textWidth: Float
    val textHeight: Float
    val f22164l: Float
    val horizonalOffset = context.dp2px(7f)
    val verticalOffset = context.dp2px(7f)
    val horizontalPadding: Int = context.dp2px(12)
    val verticalPadding: Float = context.dp2px(8f)
    val offsetLeftEdge: Float
    val radiusX: Float
    val radiusY: Float
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
        this.offsetLeftEdge = (-m14219b) / 2f
        val dp24 = context.dp2px(24)
        this.formatTime = TimeUtil.formatBatteryChooseTime(selectedItem.state, selectedItem.time)
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
        this.radiusX = f13
        this.radiusY = f13
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

    fun drawBubble(canvas: Canvas) {
        val f29 = horizonalOffset + startX
        val f30 = startY - this.verticalOffset//固定值7dp
        val f31 = f30 - 1.0f
        val f34 = f22157e - radiusX
        var f13 = 0f
        var f14 = 0f
        // 参考drawBubbleView方法中的边界处理逻辑，确保气泡不会超出视图边界
        bubbleRectF.apply {
            // 参考drawBubbleView方法中的边界处理逻辑
            Logcat.d(
                "BubbleView",
                "f22157e:$f22157e,radiusX:$radiusX,f29:$f29,f27:$startX,f34:$f34,i19:$horizontalPadding,rectF：$this"
            )
            val f45 = 2f
            val f46 = f22164l / f45
            val f47 = f46 + startX
            if (f47 > f22157e) {
                Logcat.d("BubbleView", "bubble right")
                val f19 = (f22157e - textWidth) - (horizontalPadding * 2)
                set(f19, f30 - f22173u, f22157e, f30)
            } else if (f29 > f34) {
                // 气泡右侧超出边界
                f14 = (f30 - (verticalPadding / f45)) - (textHeight / f45)
                val left = f22157e - textWidth - (horizontalPadding * 2)
                val top = f30 - f22173u
                set(left, top, f22157e, f30)
                Logcat.d("BubbleView", "f22157e:$f22157e,f48:$textWidth,i19:$horizontalPadding,rectF：$this")
                Logcat.d("BubbleView", "rectF：$this")
            } else if (startX - (textWidth / f45) > f22155c) {
                // 气泡在中间
                val f49 = textWidth / 2f
                val f20 = startX - f49
                val left = f20 - horizontalPadding.toFloat()
                val top = f30 - f22173u
                val right = f49 + startX + horizontalPadding.toFloat()
                set(max(0f, left), top, right, f30)
                Logcat.d("BubbleView", "rectF：$this")
            } else {
                // 气泡左侧可能超出边界，确保不会超出
                val left = f22155c // 直接靠到左侧边界
                val top = f30 - f22173u
                val right = left + textWidth + (horizontalPadding * 2)
                set(left, top, right, f30)
                Logcat.d("BubbleView", "rectF：$this")
            }
        }
        Logcat.d("BubbleView", "rectF：$bubbleRectF")
        canvas.drawRoundRect(
            bubbleRectF,
            this.radiusX,
            this.radiusY,
            this.bubbleBgPaint
        )

        // 绘制气泡下方的三角形箭头，调整方向向下
        // 参考drawBubbleView方法中的处理方式，保持三角形形状不变


        if (f29 > f34) {
            f14 = (f30 - (verticalPadding / 2f)) - (textHeight / 2f)
            f13 = f22157e
        } else {
            f13 = offsetLeftEdge + radiusX + f22155c
            if (f29 >= f13) {
                Logcat.d("BubbleView", "normal")
                f13 = f29
            }
            f14 = f31
        }

        val f38 = f13
        var f39 = f14
        var f15 = 0f
        var f16 = 0f

        if (isRtl) {
            val f41 = startX - horizonalOffset
            f15 = f29
            if (f41 < radiusX + f22155c) {
                f39 = (f30 - (verticalPadding / 2f)) - (textHeight / 2f)
                f16 = f22155c
            } else {
                f16 = (viewWidth - f22155c) - horizontalPadding
                if (f41 <= f16) {
                    Logcat.d("BubbleView", "normal")
                    f16 = f41
                }
                f39 = f31
            }
        } else {
            f15 = f29
            f16 = f38
        }

        var f42 = startX - horizonalOffset
        var f17 = 0f
        Logcat.d(
            "BubbleView",
            "trianglePath>>moveTo：[$startX, $startY],lineTo：[$f16, $f39],horizonalOffset:$horizonalOffset,f37:$offsetLeftEdge,radiusX:$radiusX,f22155c:$f22155c,f42:$f42"
        )
        if (f42 < offsetLeftEdge + radiusX + f22155c) {
            f42 = offsetLeftEdge + radiusX + f22155c
            f17 = (f30 - (verticalPadding / 2f)) - (textHeight / 2)
        } else {
            val f43 = (f22157e - offsetLeftEdge) - horizonalOffset
            if (f42 > f43) {
                f42 = f43
            } else {
                Logcat.d("BubbleView", "normal")
            }
            f17 = f31
        }

        var f18 = 0f
        var f21 = 0f

        if (isRtl) {
            if (f15 > ((viewWidth - f22155c) - offsetLeftEdge) - radiusX) {
                f42 = f22157e - offsetLeftEdge
                f18 = (f30 - (verticalPadding / 2f)) - (textHeight / 2)
            } else {
                val f44 = horizontalPadding + f22155c + offsetLeftEdge
                if (f15 < f44) {
                    f21 = f44
                } else {
                    Logcat.d("BubbleView", "normal")
                    f21 = f15
                }
                f42 = f21
                f18 = f31
            }
        } else {
            f18 = f17
        }

        // 使用Path绘制气泡和三角形箭头，参考drawBubbleView方法
        val aa = startX - (f16 - startX)
        val trianglePath = Path().apply {
            moveTo(startX, startY) // 顶点
            lineTo(f16, f39) // 第一个点
            lineTo(aa, f39) // 第二个点
            close() // 闭合路径
        }
        Logcat.d(
            "BubbleView",
            "trianglePath>>moveTo：[$startX, $startY],lineTo：[$f16, $f39],lineTo：[$aa, $f18],this.f22171s:${this.radiusY},bubbleHeiht:${bubbleRectF.height()}"
        )
        canvas.drawPath(trianglePath, this.bubbleBgPaint)

        // 绘制文本
        val textPaint = this.textPaint
        val fm = textPaint.fontMetrics
        val textY = bubbleRectF.centerY() - fm.top / 2 - fm.bottom / 2

        // 文字随气泡一起移动
        val textX = bubbleRectF.left + this.horizontalPadding.toFloat()
        if (this.isRtl) {
            val scaleX = this.textWidth / 2 + bubbleRectF.left + this.horizontalPadding.toFloat()
            val scaleY = this.textHeight / 2f
            canvas.scale(-1f, 1f, scaleX, scaleY)
        }
        Logcat.d("BubbleView", "text：${this.formatTime}")
        canvas.drawText(
            this.formatTime,
            textX,
            textY,
            textPaint
        )
    }
}
