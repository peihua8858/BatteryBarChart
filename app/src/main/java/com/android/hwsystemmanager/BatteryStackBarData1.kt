package com.android.hwsystemmanager

import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.os.Build
import com.android.hwsystemmanager.utils.Logcat
import com.android.settings.util.dp2px
import java.text.NumberFormat

class BatteryStackBarData1(
    val f18269a: Float,
    val f18270b: Float,
    val f18271c: Float,
    val f18272d: Float,
    val f18273e: LevelAndCharge,
) {
    val f18275g: String
    var f18274f: Int = 0
    var f18276h: Int = 1
    var f18277i: Boolean = false
    var f18278j: Int = 0
    val f18279k: String
    val f18280l: Float
    //绘制柱状图的画笔
    val f18281m: Paint = Paint()
    //绘制曲线的画笔
    val f18282n: Paint = Paint()
//    val f18283o: Paint = Paint()
    val f18284p: Float
    var noChargeLineColor50: Int = 0
    var noChargeLineColor10: Int = 0
    var chargeLineColor50: Int = 0
    var chargeLineColor10: Int = 0
    var noChargeLineNewColor: Int = 0
    var chargeLineNewColor: Int = 0

    var lowBatteryLineColor50: Int = 0
    var lowBatteryLineColor10: Int = 0
    var lowBatteryLineNewColor: Int = 0

    init {
        val level = f18273e.level / 100.0
        f18275g = NumberFormat.getPercentInstance().format(level)
        f18279k = f18273e.charge
        f18280l = (2f * f18271c) / 3f
        f18284p = f18271c / 3f

        // Color assignments with dark mode check
        noChargeLineColor50 = if (Build.VERSION.SDK_INT>27) R.color.hsm_widget_canvas_no_charge_line_alpha50_card else R.color.hsm_widget_canvas_no_charge_line_alpha50
        noChargeLineColor10 = if (Build.VERSION.SDK_INT>27) R.color.hsm_widget_canvas_no_charge_line_alpha10_card else R.color.hsm_widget_canvas_no_charge_line_alpha10
        chargeLineColor50 = if (Build.VERSION.SDK_INT>27) R.color.hsm_widget_canvas_charge_line_alpha50_card else R.color.hsm_widget_canvas_charge_line_alpha50
        chargeLineColor10 = if (Build.VERSION.SDK_INT>27) R.color.hsm_widget_canvas_charge_line_alpha10_card else R.color.hsm_widget_canvas_charge_line_alpha10
        noChargeLineNewColor = if (Build.VERSION.SDK_INT>27) R.color.hsm_widget_canvas_no_charge_line_new_card else R.color.hsm_widget_canvas_no_charge_line_new
        chargeLineNewColor = if (Build.VERSION.SDK_INT>27) R.color.hsm_widget_canvas_charge_line_new_card else R.color.hsm_widget_canvas_charge_line_new
        lowBatteryLineColor10 = if (Build.VERSION.SDK_INT>27) R.color.hsm_widget_canvas_Low_battery_line_alpha10_card else R.color.hsm_widget_canvas_Low_battery_line_alpha10
        lowBatteryLineColor50 = if (Build.VERSION.SDK_INT>27) R.color.hsm_widget_canvas_Low_battery_line_alpha50_card else R.color.hsm_widget_canvas_Low_battery_line_alpha50
        lowBatteryLineNewColor = if (Build.VERSION.SDK_INT>27) R.color.hsm_widget_canvas_Low_battery_line_new_card else R.color.hsm_widget_canvas_Low_battery_line_new
        c()
    }

    fun a(canvas: Canvas, isCharging: Boolean, param3: Int, path: Path, param5: Int) {
        val levelData = f18273e
        val level = levelData.level
        Logcat.d("BatteryStackBarData", "level is $level --charging : $isCharging,param5:$param5")
        Logcat.d("BatteryStackBarData", "x:$f18269a,y:$f18270b,width:$f18271c,height:$f18272d,param3:$param3,level is $level --charging : $isCharging")
        c()
        val emptySpacePercent = 100 - level
        val emptySpaceHeight = emptySpacePercent * f18272d / 100f
        val yStart = f18270b + emptySpaceHeight

        val param3Percent = 100 - param3
        val param3Height = param3Percent * f18272d / 100f
        val yParam3 = f18270b + param3Height

        val halfWidth = f18284p / 2f
        val xLeft = f18269a - halfWidth
        val xRight = f18269a + f18271c - halfWidth

        when (param5) {
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
            lineTo(xRight, f18270b + f18272d)
            lineTo(xLeft, f18270b + f18272d)
            close()
        }
        val batteryPath1 = Path().apply {
            moveTo(xLeft, yParam3)
            lineTo(xRight, yStart)
            close()
        }
        canvas.drawPath(batteryPath1, f18282n)
        canvas.drawPath(batteryPath, f18281m)
    }

    fun b(colorStart: Int, colorEnd: Int): LinearGradient {
        return LinearGradient(
            f18269a,
            f18270b,
            f18269a + f18280l,
            f18270b + f18272d,
            colorStart,
            colorEnd,
            Shader.TileMode.CLAMP
        )
    }

    fun c() {
        val context = MainApplication.context
        val resources = context.resources

        when (f18276h) {
            0, 2 -> {
                val color = resources.getColor(R.color.battery_not_select_bg)
                f18281m.shader = b(color, color)

                val strokeColor = if (Build.VERSION.SDK_INT>27) R.color.battery_not_select_bg_bottom_card else R.color.battery_not_select_bg_bottom
                f18282n.color = resources.getColor(strokeColor)
            }
            1 -> {
                if (f18279k == "true") {
                    val colorStart = resources.getColor(chargeLineColor50)
                    val colorEnd = resources.getColor(chargeLineColor10)
                    f18281m.shader = b(colorStart, colorEnd)
                    f18282n.color = resources.getColor(chargeLineNewColor)
                } else {
                    val colorStart = resources.getColor(noChargeLineColor50)
                    val colorEnd = resources.getColor(noChargeLineColor10)
                    f18281m.shader = b(colorStart, colorEnd)
                    f18282n.color = resources.getColor(noChargeLineNewColor)
                }
                if (f18273e.level<20) {
                    val colorStart = resources.getColor(lowBatteryLineColor50)
                    val colorEnd = resources.getColor(lowBatteryLineColor10)
                    f18281m.shader = b(colorStart, colorEnd)
                    f18282n.color = resources.getColor(lowBatteryLineNewColor)
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

        f18282n.style = Paint.Style.STROKE
        f18282n.strokeJoin = Paint.Join.ROUND
        f18282n.strokeWidth = context.dp2px(2f)
    }
}
