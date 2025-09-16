package com.android.hwsystemmanager.multicolor

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Shader
import androidx.annotation.ColorInt
import com.android.hwsystemmanager.LevelAndCharge
import com.android.hwsystemmanager.MainApplication
import com.android.hwsystemmanager.R
import com.android.hwsystemmanager.multicolor.MultiColorPathRenderer.PointFColor
import com.android.hwsystemmanager.utils.Logcat
import com.android.hwsystemmanager.utils.argb
import com.android.hwsystemmanager.utils.dLog
import com.android.hwsystemmanager.utils.isPie
import java.text.NumberFormat

data class StackBarPointData(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val levelAndCharge: LevelAndCharge,
) {
    var showBubble: Boolean = false
    var index = -1
    var charge: String = ""
    var selectState: Int = 1
    var state = 0
    var barWidth: Float = 0f
    var barOffset: Float = 0f
    val precentLevel: String
    private var mBarPath: Path = Path()
    private var mDrawLineColor: Int = 0
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.style = Paint.Style.STROKE
        this.strokeJoin = Paint.Join.ROUND
    }
    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.isAntiAlias = true
        this.isAntiAlias = true
        this.isDither = true
    }

    private var noChargeLineNewColor: Int
    private var noChargeLineColor50: Int
    private var noChargeLineColor10: Int

    private var chargeLineNewColor: Int
    private var chargeLineColor50: Int
    private var chargeLineColor10: Int

    private var lowBatteryLineNewColor: Int
    private var lowBatteryLineColor50: Int
    private var lowBatteryLineColor10: Int

    private var noSelectedBgColor: Int
    private var noSelectedBottomBgColor: Int

    init {
        val level = levelAndCharge.level / 100.0
        precentLevel = NumberFormat.getPercentInstance().format(level)
        charge = levelAndCharge.charge
        barWidth = (2f * width) / 3f
        barOffset = width / 3f
        val context = MainApplication.context
        noChargeLineColor50 = context.getColor(if (isPie) R.color.battery_no_charge_line_alpha50_card else R.color.battery_no_charge_line_alpha50)
        noChargeLineColor10 = context.getColor(if (isPie) R.color.battery_no_charge_line_alpha10_card else R.color.battery_no_charge_line_alpha10)
        noChargeLineNewColor = context.getColor(if (isPie) R.color.battery_no_charge_line_new_card else R.color.battery_no_charge_line_new)

        chargeLineColor50 = context.getColor(if (isPie) R.color.battery_charge_line_alpha50_card else R.color.battery_charge_line_alpha50)
        chargeLineColor10 = context.getColor(if (isPie) R.color.battery_charge_line_alpha10_card else R.color.battery_charge_line_alpha10)
        chargeLineNewColor = context.getColor(if (isPie) R.color.battery_charge_line_new_card else R.color.battery_charge_line_new)
        lowBatteryLineColor10 =
            context.getColor(if (isPie) R.color.battery_low_battery_line_alpha10_card else R.color.battery_low_battery_line_alpha10)
        lowBatteryLineColor50 =
            context.getColor(if (isPie) R.color.battery_low_battery_line_alpha50_card else R.color.battery_low_battery_line_alpha50)
        lowBatteryLineNewColor = context.getColor(if (isPie) R.color.battery_low_battery_line_new_card else R.color.battery_low_battery_line_new)
        noSelectedBgColor = context.getColor(R.color.battery_not_select_bg)
        noSelectedBottomBgColor = context.getColor(R.color.battery_not_select_bg_bottom_card)
        changePainColor()
    }

    fun setChargeColor(color: Int): StackBarPointData {
        this.chargeLineNewColor = color
        this.chargeLineColor50 = color.argb(0.3f)
        this.chargeLineColor10 = color.argb(0f)
        return this
    }

    fun setNoChargeColor(color: Int): StackBarPointData {
        this.noChargeLineNewColor = color
        this.noChargeLineColor50 = color.argb(0.3f)
        this.noChargeLineColor10 = color.argb(0f)
        return this
    }

    fun setLowBatteryColor(color: Int): StackBarPointData {
        this.lowBatteryLineNewColor = color
        this.lowBatteryLineColor50 = color.argb(0.3f)
        this.lowBatteryLineColor10 = color.argb(0f)
        return this
    }

    fun setNoSelectedBgColor(color: Int): StackBarPointData {
        this.noSelectedBgColor = color
        return this
    }

    fun setNoSelectedBottomBgColor(color: Int): StackBarPointData {
        this.noSelectedBottomBgColor = color
        return this
    }

    fun setChartLineWidth(width: Float): StackBarPointData {
        this.linePaint.strokeWidth = width
        return this
    }

    fun calculatePoint(pointFs: MutableList<PointF>, preLevel: Int, type: Int, index: Int) {
        val levelData = levelAndCharge
        val level = levelData.level
        val emptySpacePercent = 100 - level
        val emptySpaceHeight = emptySpacePercent * height / 100f
        val yStart = y + emptySpaceHeight

        val param3Percent = 100 - preLevel
        val param3Height = param3Percent * height / 100f
        val yParam3 = y + param3Height

        val halfWidth = barOffset / 2f
        val xLeft = x - halfWidth
        val xRight = x + width - halfWidth
        Logcat.d(
            "BatteryStackBarData",
            "x:$x,y:$y,width:$width,height:$height,preLevel:$preLevel,level is $level,index:$index,xLeft:$xLeft,yParam3:$yParam3,xRight:$xRight,yStart:$yStart"
        )
        when (type) {
            1 -> {
                pointFs.add(PointF(xLeft, yParam3))
                pointFs.add(PointF(xRight, yStart))
            }

            2 -> {
                pointFs.add(PointF(xRight, yStart))
                pointFs.add(PointF(xLeft, yParam3))
                pointFs.add(PointF(xRight, yStart))
            }

            3 -> {
                pointFs.add(PointF(xRight, yStart))
            }

            4 -> {
                pointFs.add(PointF(xLeft, yParam3))
                pointFs.add(PointF(xRight, yStart))
            }

            else -> {
                // Do nothing
            }
        }

        mBarPath = Path().apply {
            moveTo(xLeft, yParam3)
            lineTo(xRight, yStart)
            lineTo(xRight, y + height)
            lineTo(xLeft, y + height)
            close()
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
        val level = levelAndCharge.level
        val emptySpacePercent = 100 - level
        val emptySpaceHeight = emptySpacePercent * height / 100f
        val yStart = y + emptySpaceHeight

        val param3Percent = 100 - preLevel
        val param3Height = param3Percent * height / 100f
        val yParam3 = y + param3Height

        val halfWidth = barOffset / 2f
        val xLeft = x - halfWidth
        val xRight = x + width - halfWidth
        mBarPath = Path().apply {
            moveTo(xLeft, yParam3)
            lineTo(xRight, yStart)
            lineTo(xRight, y + height)
            lineTo(xLeft, y + height)
            close()
        }
        dLog { "processPointF2>>xLeft:${xLeft},yParam3:${yParam3},xRight:$xRight,yStart:$yStart" }
        return Point(xLeft, yParam3, xRight, yStart)
    }

    fun drawBar(canvas: Canvas) {
        canvas.drawPath(mBarPath, barPaint)
    }

    fun changePainColor() {
        when (selectState) {
            0, 2 -> {
                barPaint.shader = createLinearGradient(noSelectedBgColor, noSelectedBgColor)
                linePaint.color = noSelectedBottomBgColor
                mDrawLineColor = noSelectedBottomBgColor
            }

            1 -> {
                when (charge) {
                    "true" -> {
                        barPaint.shader = createLinearGradient(chargeLineColor50, chargeLineColor10)
                        linePaint.color = chargeLineNewColor
                        mDrawLineColor = chargeLineNewColor
                    }

                    "low" -> {
                        barPaint.shader =
                            createLinearGradient(lowBatteryLineColor50, lowBatteryLineColor10)
                        linePaint.color = lowBatteryLineNewColor
                        mDrawLineColor = lowBatteryLineNewColor
                    }

                    else -> {
                        barPaint.shader =
                            createLinearGradient(noChargeLineColor50, noChargeLineColor10)
                        linePaint.color = noChargeLineNewColor
                        mDrawLineColor = noChargeLineNewColor
                    }
                }

            }
        }
    }

    private fun createLinearGradient(
        @ColorInt colorStart: Int,
        @ColorInt colorEnd: Int,
    ): LinearGradient {
        // 重新设计渐变策略以增强低电量情况下的可见性
        val level = levelAndCharge.level
        val levelY = (if (level < 20) 0f else 0f) + (100 - level) * height / 100f
        Logcat.d(
            "BatteryStackBarData>>>",
            "level:$level,levelY is $levelY,startY:$y, height is $height"
        )
        // 调整渐变起始位置，使其与电量曲线位置对齐
        return LinearGradient(
            x,
            levelY, // 使用电量对应的Y坐标作为起始位置
            x + barWidth,
            y + height,
            colorStart,
            colorEnd,
            Shader.TileMode.CLAMP
        )
    }
}