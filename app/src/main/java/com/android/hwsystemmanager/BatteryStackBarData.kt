//package com.android.hwsystemmanager
//
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.LinearGradient
//import android.graphics.Paint
//import android.graphics.Path
//import android.graphics.Shader
//import androidx.core.content.ContextCompat
//import com.android.hwsystemmanager.utils.Logcat
//import com.android.hwsystemmanager.utils.dp2px
//import java.text.NumberFormat
//
//class BatteryStackBarData(
//    @JvmField val f19385a: Float,
//    @JvmField val f19386b: Float,
//    @JvmField val f19387c: Float,
//    @JvmField val f19388d: Float,
//    val f19389e: LevelAndCharge,
//) {
//    @JvmField
//    var f19390f: Int = 0
//    val f19391g: String =
//        NumberFormat.getPercentInstance().format(f19389e.level / 100.0)
//    var f19392h: Int = 1
//    var f19393i: Boolean = false
//    var f19394j: Int = 0
//    val f19395k: String = f19389e.charge
//    val f19396l: Float = (2.0f * f19387c) / 3.0f
//    val f19397m: Paint = Paint()
//    val f19398n: Paint = Paint()
//    val f19399o: Paint = Paint()
//    val f19400p: Float = f19387c / 3.0f
//    var f19401q: Int = R.color.hsm_widget_canvas_no_charge_line_alpha50_card
//    var f19402r: Int = R.color.hsm_widget_canvas_no_charge_line_alpha10_card
//    var f19403s: Int = R.color.hsm_widget_canvas_charge_line_alpha50_card
//    var f19404t: Int = R.color.hsm_widget_canvas_charge_line_alpha10_card
//    var f19405u: Int = R.color.hsm_widget_canvas_no_charge_line_new_card
//    var f19406v: Int = R.color.hsm_widget_canvas_charge_line_new_card
//
//    init {
//        m11671c()
//    }
//
//
//    fun m11669a(canvas: Canvas, z10: Boolean, i4: Int, path: Path, i8: Int) {
//        val levelAndCharge = this.f19389e
//        Logcat.d("BatteryStackBarData", "level is ${levelAndCharge.level} --charging : $z10")
//        m11671c()
//        val f10 = (100 - levelAndCharge.level).toFloat()
//        val f11 = this.f19388d
//        val f12 = 100f
//        val f13 = this.f19386b
//        val f14 = ((f10 * f11) / f12) + f13
//        val f15 = (((100 - i4) * f11) / f12) + f13
//        val f16 = this.f19400p / 2
//        val f17 = this.f19385a
//        val f18 = f17 - f16
//        val f19 = (f17 + this.f19387c) - f16
//        if (i8 != 1) {
//            if (i8 != 2) {
//                val paint = this.f19398n
//                if (i8 != 3) {
//                    if (i8 == 4) {
//                        path.moveTo(f18, f15)
//                        path.lineTo(f19, f14)
//                        canvas.drawPath(path, paint)
//                    }
//                } else {
//                    path.lineTo(f19, f14)
//                    canvas.drawPath(path, paint)
//                }
//            } else {
//                path.lineTo(f19, f14)
//            }
//        } else {
//            path.moveTo(f18, f15)
//            path.lineTo(f19, f14)
//        }
//        val path2 = Path()
//        path2.moveTo(f18, f15)
//        path2.lineTo(f19, f14)
//        val f20 = f13 + f11
//        path2.lineTo(f19, f20)
//        path2.lineTo(f18, f20)
//        path2.close()
//        canvas.drawPath(path2, this.f19397m)
//    }
//
//
//    fun m11670b(i4: Int, i8: Int): LinearGradient {
//        val f10 = this.f19385a
//        val f11 = this.f19386b
//        return LinearGradient(
//            f10,
//            f11,
//            f10 + this.f19396l,
//            f11 + this.f19388d,
//            i4,
//            i8,
//            Shader.TileMode.CLAMP
//        )
//    }
//
//    fun m11671c() {
//        val context = MainApplication.context
//        val m11670b: LinearGradient
//        val color: Int
//        val i4 = this.f19392h
//        val paint = this.f19397m
//        val paint2 = this.f19398n
//        if (i4 != 0) {
//            if (i4 == 1) {
//                val str = this.f19395k
//                m11670b = if (str != "true") {
//                    m11670b( ContextCompat.getColor(context,this.f19401q),  ContextCompat.getColor(context,this.f19402r))
//                } else {
//                    m11670b( ContextCompat.getColor(context,this.f19403s),  ContextCompat.getColor(context,this.f19404t))
//                }
//                paint.setShader(m11670b)
//                color = if (str != "true") {
//                    ContextCompat.getColor(context,this.f19405u)
//                } else {
//                    ContextCompat.getColor(context,this.f19406v)
//                }
//                paint2.color = color
//            }
//            val m11670b2 = m11670b(
//                ContextCompat.getColor(context,R.color.battery_chart_set),
//                ContextCompat.getColor(context,
//                    R.color.battery_chart_set
//                )
//            )
//            val paint3 = this.f19399o
//            paint3.setShader(m11670b2)
//            paint3.isAntiAlias = true
//            paint.isAntiAlias = true
//            paint2.isAntiAlias = true
//            paint2.style = Paint.Style.STROKE
//            paint2.strokeJoin = Paint.Join.ROUND
//            paint2.strokeWidth = context.dp2px(2.0f)
//        }
//        paint.setShader(
//            m11670b(
//                 ContextCompat.getColor(context,R.color.battery_not_select_bg),
//                 ContextCompat.getColor(context,
//                    R.color.battery_not_select_bg
//                )
//            )
//        )
//        val valueOf: Int = R.color.battery_not_select_bg_bottom_card
//        paint2.color =  ContextCompat.getColor(context,valueOf)
//        val m11670b22 = m11670b(
//             ContextCompat.getColor(context,R.color.battery_chart_set),
//             ContextCompat.getColor(context,
//                R.color.battery_chart_set
//            )
//        )
//        val paint32 = this.f19399o
//        paint32.setShader(m11670b22)
//        paint32.isAntiAlias = true
//        paint.isAntiAlias = true
//        paint2.isAntiAlias = true
//        paint2.style = Paint.Style.STROKE
//        paint2.strokeJoin = Paint.Join.ROUND
//        paint2.strokeWidth = context.dp2px(2.0f)
////        paint2.color = Color.BLUE
////        paint .color = Color.RED
////        paint32.color = Color.CYAN
//    }
//}
