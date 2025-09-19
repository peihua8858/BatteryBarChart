package com.android.hwsystemmanager.compose

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.android.hwsystemmanager.utils.Logcat
import com.android.hwsystemmanager.utils.dLog
import java.text.NumberFormat

data class StackBarPointData(
    val index: Int,
    val level: Int,
    val time: Long,
    val charge: String,
    val timeLabel: String,
) {
    var offset: Offset = Offset(0f, 0f)
    var size: Size = Size(0f, 0f)
    var selectState: Int = 1
    var state = 0
    val precentLevel: String
    private var barOffset: Float = 0f
    var barWidth: Float = 0f
        set(value) {
            field = value
            barOffset = value / 3f
        }
    var mBarPath: Path = Path()
        private set
    private var mDrawLineColor: Color = Color.Unspecified
    private var noChargeLineNewColor: Color = Color.Unspecified
    private var noChargeLineColor50: Color = Color.Unspecified
    private var noChargeLineColor10: Color = Color.Unspecified

    private var chargeLineNewColor: Color = Color.Unspecified
    private var chargeLineColor50: Color = Color.Unspecified
    private var chargeLineColor10: Color = Color.Unspecified

    private var lowBatteryLineNewColor: Color = Color.Unspecified
    private var lowBatteryLineColor50: Color = Color.Unspecified
    private var lowBatteryLineColor10: Color = Color.Unspecified

    private var noSelectedBgColor: Color = Color.Unspecified
    private var noSelectedBottomBgColor: Color = Color.Unspecified
    var barBrush: Brush = Brush.linearGradient(listOf(Color.Green, Color.Green))
        private set
    private val barPaint = Paint().apply {
        this.isAntiAlias = true
//        this.isDither = true
    }

    init {
        val level = level / 100.0
        precentLevel = NumberFormat.getPercentInstance().format(level)
        setChargeColor(Color.Green)
        setNoChargeColor(Color.Blue)
        setLowBatteryColor(Color.Red)
        setNoSelectedBgColor(Color.Gray)
        setNoSelectedBottomBgColor(Color.Gray)
        changePainColor()
    }

    fun setChargeColor(color: Color): StackBarPointData {
        this.chargeLineNewColor = color
        this.chargeLineColor50 = color.copy(alpha = 0.3f)
        this.chargeLineColor10 = color.copy(alpha = 0f)
        return this
    }

    fun setNoChargeColor(color: Color): StackBarPointData {
        this.noChargeLineNewColor = color
        this.noChargeLineColor50 = color.copy(alpha = 0.3f)
        this.noChargeLineColor10 = color.copy(alpha = 0f)
        return this
    }

    fun setLowBatteryColor(color: Color): StackBarPointData {
        this.lowBatteryLineNewColor = color
        this.lowBatteryLineColor50 = color.copy(alpha = 0.3f)
        this.lowBatteryLineColor10 = color.copy(alpha = 0f)
        return this
    }

    fun setNoSelectedBgColor(color: Color): StackBarPointData {
        this.noSelectedBgColor = color
        return this
    }

    fun setNoSelectedBottomBgColor(color: Color): StackBarPointData {
        this.noSelectedBottomBgColor = color
        return this
    }

    fun changePainColor() {
        when (selectState) {
            0, 2 -> {
                barBrush = createLinearGradient(noSelectedBgColor, noSelectedBgColor)
                mDrawLineColor = noSelectedBottomBgColor
            }

            1 -> {
                when (charge) {
                    "true" -> {
                        barBrush = createLinearGradient(chargeLineColor50, chargeLineColor10)
                        mDrawLineColor = chargeLineNewColor
                    }

                    "low" -> {
                        barBrush =
                            createLinearGradient(lowBatteryLineColor50, lowBatteryLineColor10)
                        mDrawLineColor = lowBatteryLineNewColor
                    }

                    else -> {
                        barBrush =
                            createLinearGradient(noChargeLineColor50, noChargeLineColor10)
                        mDrawLineColor = noChargeLineNewColor
                    }
                }

            }
        }
    }

    fun calculatePointF(preLevel: Int, pointFs: MutableList<PointFColor>): Point {
        return calculatePointF(preLevel).apply {
            pointFs.add(PointFColor(startX, startY, mDrawLineColor))
            pointFs.add(PointFColor(stopX, stopY, mDrawLineColor))
        }
    }

    fun calculatePointF(preLevel: Int): Point {
        changePainColor()
        val emptySpacePercent = 100 - level
        val emptySpaceHeight = emptySpacePercent * size.height / 100f
        val yStart = offset.y + emptySpaceHeight

        val param3Percent = 100 - preLevel
        val param3Height = param3Percent * size.height / 100f
        val yParam3 = offset.y + param3Height

//        val halfWidth = barOffset / 2f
        val xLeft = offset.x/* - halfWidth*/
        val xRight = offset.x + size.width/* - halfWidth*/
        mBarPath = Path().apply {
            moveTo(xLeft, yParam3)
            lineTo(xRight, yStart)
            lineTo(xRight, offset.y + size.height)
            lineTo(xLeft, offset.y + size.height)
            close()
        }
        dLog { "processPointF2>>size:${size},offset:$offset,barWidth:$barWidth" }
        dLog { "processPointF2>>xLeft:${xLeft},yParam3:${yParam3},xRight:$xRight,yStart:$yStart" }
        return Point(xLeft, yParam3, xRight, yStart)
    }

    fun drawBar(canvas: Canvas) {
        canvas.drawPath(mBarPath, barPaint)
    }
    fun drawBar(canvas: DrawScope) {
        canvas.drawPath(mBarPath, barBrush)
    }

    private fun createLinearGradient(
        colorStart: Color,
        colorEnd: Color,
    ): Brush {
        // 重新设计渐变策略以增强低电量情况下的可见性
        val level = level
        val levelY = (if (level < 20) 0f else 0f) + (100 - level) * size.height / 100f
        Logcat.d(
            "BatteryStackBarData>>>",
            "level:$level,levelY is $levelY,startY:${offset.y}, height is ${size.height}"
        )
        return Brush.linearGradient(
            colors = listOf(colorStart, colorEnd),
            start = Offset(offset.x, levelY),
            end = Offset(offset.x + barWidth, offset.y + size.height),
            tileMode = TileMode.Clamp
        )
        // 调整渐变起始位置，使其与电量曲线位置对齐
    }
}

data class Point(val startX: Float, val startY: Float, val stopX: Float, val stopY: Float)
class PointFColor(var x: Float, var y: Float, var color: Color) {
    override fun toString(): String {
        return "{'x':$x, 'y':$y, 'color':$color}"
    }
}
