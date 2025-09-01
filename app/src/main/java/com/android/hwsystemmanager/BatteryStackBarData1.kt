package com.android.hwsystemmanager

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.os.Build
import androidx.annotation.ColorInt
import com.android.hwsystemmanager.utils.Logcat
import com.android.hwsystemmanager.utils.dp2px
import java.text.NumberFormat

class BatteryStackBarData1(
    val startX: Float,
    val startY: Float,
    val width: Float,
    val height: Float,
    val levelAndCharge: LevelAndCharge,
) {
    val f18275g: String
    var f18274f: Int = 0
    var f18276h: Int = 1
    var f18277i: Boolean = false
    var f18278j: Int = 0
    val charge: String
    val barWidth: Float
    //绘制柱状图的画笔
    val f18281m: Paint = Paint()
    //绘制曲线的画笔
    val f18282n: Paint = Paint()
//    val f18283o: Paint = Paint()
    val barOffset: Float
    private val noChargeLineColor50: Int
    private val noChargeLineColor10: Int
    private val chargeLineColor50: Int
    private val chargeLineColor10: Int
    private val noChargeLineNewColor: Int
    private val chargeLineNewColor: Int

    private val lowBatteryLineColor50: Int
    private val lowBatteryLineColor10: Int
    private val lowBatteryLineNewColor: Int
    private val noSelectedBgColor: Int
    private val noSelectedBottomBgColor: Int

    init {
        val level = levelAndCharge.level / 100.0
        f18275g = NumberFormat.getPercentInstance().format(level)
        charge = levelAndCharge.charge
        barWidth = (2f * width) / 3f
        barOffset = width / 3f
        val context =MainApplication.context
        // Color assignments with dark mode check
        noChargeLineColor50 = context.getColor(if (Build.VERSION.SDK_INT>27) R.color.battery_no_charge_line_alpha50_card else R.color.battery_no_charge_line_alpha50)
        noChargeLineColor10 = context.getColor(if (Build.VERSION.SDK_INT>27) R.color.battery_no_charge_line_alpha10_card else R.color.battery_no_charge_line_alpha10)
        chargeLineColor50 = context.getColor(if (Build.VERSION.SDK_INT>27) R.color.battery_charge_line_alpha50_card else R.color.battery_charge_line_alpha50)
        chargeLineColor10 = context.getColor(if (Build.VERSION.SDK_INT>27) R.color.battery_charge_line_alpha10_card else R.color.battery_charge_line_alpha10)
        noChargeLineNewColor = context.getColor(if (Build.VERSION.SDK_INT>27) R.color.battery_no_charge_line_new_card else R.color.battery_no_charge_line_new)
        chargeLineNewColor = context.getColor(if (Build.VERSION.SDK_INT>27) R.color.battery_charge_line_new_card else R.color.battery_charge_line_new)
        lowBatteryLineColor10 = context.getColor(if (Build.VERSION.SDK_INT>27) R.color.battery_low_battery_line_alpha10_card else R.color.battery_low_battery_line_alpha10)
        lowBatteryLineColor50 = context.getColor(if (Build.VERSION.SDK_INT>27) R.color.battery_low_battery_line_alpha50_card else R.color.battery_low_battery_line_alpha50)
        lowBatteryLineNewColor = context.getColor(if (Build.VERSION.SDK_INT>27) R.color.battery_low_battery_line_new_card else R.color.battery_low_battery_line_new)
        noSelectedBgColor = context.getColor(R.color.battery_not_select_bg)
        noSelectedBottomBgColor = context.getColor(R.color.battery_not_select_bg_bottom_card)
        c()
    }

    fun a(canvas: Canvas, isCharging: Boolean, preLevel: Int, path: Path, type: Int) {
        val levelData = levelAndCharge
        val level = levelData.level
        Logcat.d("BatteryStackBarData", "level is $level --charging : $isCharging,type:$type")
        Logcat.d("BatteryStackBarData", "x:$startX,y:$startY,width:$width,height:$height,preLevel:$preLevel,level is $level --charging : $isCharging")
        c()
        val emptySpacePercent = 100 - level
        val emptySpaceHeight = emptySpacePercent * height / 100f
        val yStart = startY + emptySpaceHeight

        val param3Percent = 100 - preLevel
        val param3Height = param3Percent * height / 100f
        val yParam3 = startY + param3Height

        val halfWidth = barOffset / 2f
        val xLeft = startX - halfWidth
        val xRight = startX + width - halfWidth
        Logcat.d(
            "BatteryStackBarData>>>>aaa",
            "x:$startX,y:$startY,width:$width,height:$height,preLevel:$preLevel,level is $level,xLeft:$xLeft,yParam3:$yParam3,xRight:$xRight,yStart:$yStart"
        )
        when (type) {
            1 -> {
                path.moveTo(xLeft, yParam3)
                path.lineTo(xRight, yStart)
            }
            2 -> {
                path.lineTo(xRight, yStart)
            }
            3 -> {
                path.lineTo(xRight, yStart)
                canvas.drawPath(path, f18282n)
            }
            4 -> {
                path.moveTo(xLeft, yParam3)
                path.lineTo(xRight, yStart)
                canvas.drawPath(path, f18282n)
            }
            else -> {
                // Do nothing
            }
        }

        val batteryPath = Path().apply {
            moveTo(xLeft, yParam3)
            lineTo(xRight, yStart)
            lineTo(xRight, startY + height)
            lineTo(xLeft, startY + height)
            close()
        }
//        val batteryPath1 = Path().apply {
//            moveTo(xLeft, yParam3)
//            lineTo(xRight, yStart)
//            close()
//        }
//        canvas.drawPath(batteryPath1, f18282n)
        canvas.drawPath(batteryPath, f18281m)
    }

    fun b(colorStart: Int, colorEnd: Int): LinearGradient {
        return LinearGradient(
            startX,
            startY,
            startX + barWidth,
            startY + height,
            colorStart,
            colorEnd,
            Shader.TileMode.CLAMP
        )
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
            "level:$level,levelY is $levelY,startY:$startY, height is $height"
        )
        // 调整渐变起始位置，使其与电量曲线位置对齐
        return LinearGradient(
            startX,
            levelY, // 使用电量对应的Y坐标作为起始位置
            startX + barWidth,
            startY + height,
            colorStart,
            colorEnd,
            Shader.TileMode.CLAMP
        )
    }

    fun c() {
        val context = MainApplication.context

        when (f18276h) {
            0, 2 -> {
                f18281m.shader = createLinearGradient(noSelectedBgColor, noSelectedBgColor)
                f18282n.color = noSelectedBottomBgColor
            }

            1 -> {
                if (charge == "true") {
                    f18281m.shader = createLinearGradient(chargeLineColor50, chargeLineColor10)
                    f18282n.color = chargeLineNewColor
                } else if (charge == "low") {
                    f18281m.shader =
                        createLinearGradient(lowBatteryLineColor50, lowBatteryLineColor10)
                    f18282n.color = lowBatteryLineNewColor
                } else {
                    f18281m.shader =
                        createLinearGradient(noChargeLineColor50, noChargeLineColor10)
                    f18282n.color = noChargeLineNewColor
                }

            }
        }

//        curvePaint.setColor(getColorResource(
//            R.color.battery_not_select_bg_bottom,
//            R.color.battery_not_select_bg_bottom_card
//        ));

//        val bgColor = resources.getColor(R.color.battery_chart_set)
//        f18283o.shader = b(bgColor, bgColor)

        // Common paint settings
//        f18283o.isAntiAlias = true
        f18281m.isAntiAlias = true
        f18282n.isAntiAlias = true
        f18282n.isDither = true

        f18282n.style = Paint.Style.STROKE
        f18282n.strokeJoin = Paint.Join.ROUND
        f18282n.strokeWidth = context.dp2px(2f)
    }
}
