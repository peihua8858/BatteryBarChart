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
    val leftMargin: Float
    val viewWidth: Float = selectedItem.screenWidth
    val isRtl: Boolean = isLayoutRtl
    val maxBubbleRightBound: Float
    val startX: Float = selectedItem.startX
    val startY: Float = selectedItem.startY
    var formatTime: String
    val textWidth: Float
    val textHeight: Float
    val bubbleWidth: Float
    val horizonalOffset = context.dp2px(7f)
    val verticalOffset = context.dp2px(7f)
    val horizontalPadding: Int = context.dp2px(12)
    val verticalPadding: Float = context.dp2px(8f)
    val offsetLeftEdge: Float
    val radiusX: Float
    val radiusY: Float
    val bubbleRectF: RectF = RectF()
    var bubbleHeight: Int

    init {
        val isOMR1 = isOreoR1
        val leftMargin = if (isOMR1) {
            0.0f
        } else {
            context.parseDimensionFromAttribute(33620168)
        }
        this.leftMargin = leftMargin
        val rightMargin = if (isOMR1) 0.0f else context.parseDimensionFromAttribute(33620170)
        this.maxBubbleRightBound = viewWidth - rightMargin
        this.offsetLeftEdge = (-leftMargin) / 2f
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
        val radius = (height / 2f) + verticalPadding
        this.radiusX = radius
        this.radiusY = radius
        val bubbleWidth = (radius * 2f) + width + horizontalPadding
        this.bubbleWidth = bubbleWidth
        this.bubbleHeight = max(dp24, (verticalPadding + height).toInt())
        Logcat.d("BubbleView", "bubbleWidth is $bubbleWidth")
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
        val bubbleStartX = horizonalOffset + startX
        val bubbleTopY = startY - this.verticalOffset//固定值7dp
        val arrowTipY = bubbleTopY - 1.0f
        val maxBubbleRightX = maxBubbleRightBound - radiusX
        // 参考drawBubbleView方法中的边界处理逻辑，确保气泡不会超出视图边界
        bubbleRectF.apply {
            // 参考drawBubbleView方法中的边界处理逻辑
            Logcat.d(
                "BubbleView",
                "f22157e:$maxBubbleRightBound,radiusX:$radiusX,bubbleStartX:$bubbleStartX,startX:$startX,maxBubbleRightX:$maxBubbleRightX,horizontalPadding:$horizontalPadding,rectF：$this"
            )
          if (bubbleStartX > maxBubbleRightX) {
                // 气泡右侧超出边界
                val left = maxBubbleRightBound - textWidth - (horizontalPadding * 2)
                val top = bubbleTopY - bubbleHeight
                set(left, top, maxBubbleRightBound, bubbleTopY)
                Logcat.d("BubbleView", "maxBubbleRightBound:$maxBubbleRightBound,textWidth:$textWidth,horizontalPadding:$horizontalPadding,rectF：$this")
                Logcat.d("BubbleView", "rectF：$this")
            } else if (startX - (textWidth / 2f) > leftMargin) {
                // 气泡在中间
                val halfTextWidth = textWidth / 2f
                val textCenterX = startX - halfTextWidth
                val left = textCenterX - horizontalPadding
                val top = bubbleTopY - bubbleHeight
                val right = halfTextWidth + startX + horizontalPadding
                set(max(0f, left), top, right, bubbleTopY)
                Logcat.d("BubbleView", "rectF：$this")
            } else {
                // 气泡左侧可能超出边界，确保不会超出
                val left = leftMargin // 直接靠到左侧边界
                val top = bubbleTopY - bubbleHeight
                val right = left + textWidth + (horizontalPadding * 2)
                set(left, top, right, bubbleTopY)
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

        var arrowBaseX = 0f
        var arrowTipYAdjusted = 0f
        if (bubbleStartX > maxBubbleRightX) {
            arrowTipYAdjusted = (bubbleTopY - (verticalPadding / 2f)) - (textHeight / 2f)
            arrowBaseX = maxBubbleRightBound
        } else {
            arrowBaseX = offsetLeftEdge + radiusX + leftMargin
            if (bubbleStartX >= arrowBaseX) {
                Logcat.d("BubbleView", "normal")
                arrowBaseX = bubbleStartX
            }
            arrowTipYAdjusted = arrowTipY
        }

        var arrowBottomY = arrowTipYAdjusted
        var arrowRightAnchorX = 0f
        if (isRtl) {
            val anchorPointX = startX - horizonalOffset
            if (anchorPointX < radiusX + leftMargin) {
                arrowBottomY = (bubbleTopY - (verticalPadding / 2f)) - (textHeight / 2f)
                arrowRightAnchorX = leftMargin
            } else {
                arrowRightAnchorX = (viewWidth - leftMargin) - horizontalPadding
                if (anchorPointX <= arrowRightAnchorX) {
                    Logcat.d("BubbleView", "normal")
                    arrowRightAnchorX = anchorPointX
                }
                arrowBottomY = arrowTipY
            }
        } else {
            arrowRightAnchorX = arrowBaseX
        }

        // 使用Path绘制气泡和三角形箭头，参考drawBubbleView方法
        val arrowLeftBaseX = startX - (arrowRightAnchorX - startX)
        val trianglePath = Path().apply {
            moveTo(startX, startY) // 顶点
            lineTo(arrowRightAnchorX, arrowBottomY) // 第一个点
            lineTo(arrowLeftBaseX, arrowBottomY) // 第二个点
            close() // 闭合路径
        }
        Logcat.d(
            "BubbleView",
            "trianglePath>>moveTo：[$startX, $startY],lineTo：[$arrowRightAnchorX, $arrowBottomY],lineTo：[$arrowLeftBaseX, $arrowBottomY],this.f22171s:${this.radiusY},bubbleHeiht:${bubbleRectF.height()}"
        )
        canvas.drawPath(trianglePath, this.bubbleBgPaint)

        // 绘制文本
        val textPaint = this.textPaint
        val fm = textPaint.fontMetrics
        val textY = bubbleRectF.centerY() - fm.top / 2 - fm.bottom / 2

        // 文字随气泡一起移动
        val textX = bubbleRectF.left + this.horizontalPadding
        if (this.isRtl) {
            val scaleX = this.textWidth / 2 + bubbleRectF.left + this.horizontalPadding
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
