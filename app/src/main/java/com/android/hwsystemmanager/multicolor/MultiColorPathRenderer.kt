package com.android.hwsystemmanager.multicolor

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.graphics.Shader
import com.android.hwsystemmanager.utils.Logcat.d
import com.android.hwsystemmanager.utils.argb
import com.android.hwsystemmanager.utils.writeLog
import com.android.hwsystemmanager.utils.writeLogFile
import com.fz.common.utils.toFloat
import com.google.gson.Gson

/**
 * 绘制多色路径绘制
 */
class MultiColorPathRenderer {
    private var paint = Paint().apply {
        // 3. 初始化画笔
        this.style = Paint.Style.STROKE
        this.strokeWidth = 8f
        this.isAntiAlias = true
        strokeJoin = Paint.Join.ROUND // 设置线条连接样式为圆角
//        strokeCap = Paint.Cap.BUTT // 设置线条端点样式为圆头
        strokeMiter = 10f
    }
    private var colors: IntArray = intArrayOf(0)

    /**
     * 颜色位置（0~1）
     */
    private var positions: FloatArray = floatArrayOf(0f)
    private var path: Path = Path()
    fun fakeData() {
        // 定义颜色及其位置（0~1）
        val colors = intArrayOf(Color.GREEN, Color.GRAY, Color.GRAY, Color.GRAY, Color.GRAY)
        // 创建渲染器
        val points: MutableList<PointF> = ArrayList()
        points.add(PointF(100f, 200f))
        points.add(PointF(200f, 300f))
        points.add(PointF(300f, 100f))
        points.add(PointF(400f, 400f))
        points.add(PointF(500f, 200f))
//        setData(points, colors.toTypedArray())
    }

    //    fun setData(points: List<PointFColor>) {
//        val pointFS: MutableList<PointF> = ArrayList()
//        val colors: MutableList<Int> = ArrayList()
//        for (point in points) {
//            colors.add(point.color)
//            pointFS.add(PointF(point.x, point.y))
////            if (pointFS.size>=136) {
////                break
////            }
//        }
//        setData(pointFS, colors.toTypedArray<Int>())
//        writeLogFile { Gson().toJson(pointFS) }
//        d("pointFs:" + Gson().toJson(pointFS))
//    }
    private val mPoints = mutableListOf<PointFColor>()
    fun setData(points: List<PointFColor>) {
        if (points.isEmpty()) {
            return
        }
        mPoints.clear()
        mPoints.addAll(points)
        // 1. 构建完整路径
        this.path = Path()
        // 2. 计算颜色和位置数组
        val length = points.size
        this.colors = IntArray(length * 3)
        this.positions = FloatArray(length * 3)
        for ((index, item) in points.withIndex()) {
            if (index == 0) {
                path.moveTo(item.x, item.y)
            } else {
                path.lineTo(item.x, item.y)
            }
            // 每个颜色重复两次（起点和终点）
            val prevItem = points.getOrNull(index - 1)
            val nextColor = prevItem?.color ?: Color.TRANSPARENT
            if (nextColor != Color.TRANSPARENT && nextColor != item.color) {
                colors[2 * index] = item.color.argb(0.6f)
                colors[2 * index + 1] = item.color
                colors[2 * index + 2] = item.color
            } else {
                colors[2 * index] = item.color
                colors[2 * index + 1] = item.color
                colors[2 * index + 2] = item.color
            }
            // 计算位置（0到1之间均匀分布）
            positions[2 * index] = (index.toFloat() / length)
            positions[2 * index + 1] = ((index + 1f) / length)
            positions[2 * index + 2] = ((index + 2f) / length)
        }
        writeLogFile { Gson().toJson(points) }
    }

    fun setData(points: List<PointF>, segmentColors: Array<Int>) {
        require(segmentColors.size == points.size) { "Invalid points or colors,points.size:${points.size} colors.size:${segmentColors.size}" }
        // 1. 构建完整路径
        this.path = Path()
        // 2. 计算颜色和位置数组
        val length = segmentColors.size
        this.colors = IntArray(length * 2)
        this.positions = FloatArray(length * 2)
        for ((index, item) in points.withIndex()) {
            if (index == 0) {
                path.moveTo(item.x, item.y)
            } else {
                path.lineTo(item.x, item.y)
            }
            // 每个颜色重复两次（起点和终点）
            colors[2 * index] = segmentColors[index]
            colors[2 * index + 1] = segmentColors[index]
            // 计算位置（0到1之间均匀分布）
            positions[2 * index] = (index.toFloat() / length.toFloat())
            positions[2 * index + 1] = ((index + 1f) / length.toFloat())
        }
    }

    // 设置线宽
    fun setStrokeWidth(width: Float) {
        paint.strokeWidth = width
    }

    fun setPaint(paint: Paint) {
        this.paint = paint
    }

    fun draw(canvas: Canvas) {
        if (path.isEmpty) return

        // 计算路径的边界
        val bounds = RectF()
        path.computeBounds(bounds, true)

        // 创建水平方向的渐变
        val gradient = LinearGradient(
            bounds.left, 0f, bounds.right, 0f,
            colors,
            positions,
            Shader.TileMode.CLAMP
        )
        paint.setShader(gradient)
        canvas.drawPath(path, paint)
//        for ((index, item) in mPoints.withIndex()) {
//            val endPoint = mPoints.getOrNull(index - 1) ?: item
//
//            // 使用二次贝塞尔曲线的控制点；这里为了简单示例，采用两个端点的平均位置
//            val controlPoint = PointF(
//                (item.x + endPoint.x) / 2,
//                (item.y + endPoint.y) / 2 // 调整控制点的Y坐标以增强曲线效果
//            )
//            // 仅在第一段时直接绘制
//            if (index == 0) {
//                drawBezierCurve(canvas, item, controlPoint, endPoint)
//            } else {
//                // 确保每段的起始点与前一段的结束点连接得当
//                val newStartPoint = PointF(item.x, item.y)
//                drawBezierCurve(canvas, newStartPoint, controlPoint, endPoint)
//            }
//            paint.color = item.color // 设置当前段的颜色
////            drawBezierCurve(canvas, item, controlPoint, endPoint)
//        }

    }

    private fun drawBezierCurve(canvas: Canvas, start: PointF, control: PointF, end: PointFColor) {
        val path = Path()
        path.moveTo(start.x, start.y)
        path.quadTo(control.x, control.y, end.x, end.y) // 使用二次贝塞尔曲线
        canvas.drawPath(path, paint)
    }

    private fun drawBezierCurve(canvas: Canvas, start: PointFColor, control: PointF, end: PointFColor) {
        val path = Path()
        path.moveTo(start.x, start.y)
        path.quadTo(control.x, control.y, end.x, end.y) // 使用二次贝塞尔曲线

        // 绘制路径
        canvas.drawPath(path, paint)
    }


    // 设置抗锯齿
    fun setAntiAlias(enabled: Boolean) {
        paint.isAntiAlias = enabled
    }

    class PointFColor(var x: Float, var y: Float, var color: Int) {
        override fun toString(): String {
            return "{'x':$x, 'y':$y, 'color':$color}"
        }
    }

    companion object {
        private const val TAG = "MultiColorPathRenderer"
    }
}
