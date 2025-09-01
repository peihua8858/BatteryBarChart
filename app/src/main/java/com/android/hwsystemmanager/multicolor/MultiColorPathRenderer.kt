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
import com.android.hwsystemmanager.utils.writeLog
import com.android.hwsystemmanager.utils.writeLogFile
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
        setData(points, colors.toTypedArray())
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

    fun setData(points: List<PointFColor>) {
        if (points.isEmpty()) {
            return
        }
        // 1. 构建完整路径
        this.path = Path()
        // 2. 计算颜色和位置数组
        val length = points.size
        this.colors = IntArray(length * 2)
        this.positions = FloatArray(length * 2)
        for ((index,item) in points.withIndex()) {
            if (index == 0) {
                path.moveTo(item.x, item.y)
            } else {
                path.lineTo(item.x, item.y)
            }
            // 每个颜色重复两次（起点和终点）
            colors[2 * index] = item.color
            colors[2 * index + 1] = item.color
            // 计算位置（0到1之间均匀分布）
            positions[2 * index] = (index / length).toFloat()
            positions[2 * index + 1] = ((index + 1f) / length)
        }
        writeLogFile { Gson().toJson(points) }
    }

    fun setData(points: List<PointF>, segmentColors: Array<Int>) {
        require(segmentColors.size == points.size) { "Invalid points or colors,points.size:${points.size} colors.size:${segmentColors.size}" }
        // 1. 构建完整路径
        this.path = Path()

        for ((index, item) in points.withIndex()) {
            if (index == 0) {
                path.moveTo(item.x, item.y)
            } else {
                path.lineTo(item.x, item.y)
            }
        }
        val length = segmentColors.size
        // 2. 计算颜色和位置数组
        this.colors = IntArray(length * 2)
        this.positions = FloatArray(length * 2)

//        for (i in segmentColors.indices) {
//            // 每个颜色重复两次（起点和终点）
//            colors[2 * i] = segmentColors[i]
//            colors[2 * i + 1] = segmentColors[i]
//
//            // 计算位置（0到1之间均匀分布）
//            positions[2 * i] = (i / length).toFloat()
//            positions[2 * i + 1] =( (i + 1f) / length).toFloat()
//        }
        for ((index, item) in segmentColors.withIndex()) {
            // 每个颜色重复两次（起点和终点）
            colors[2 * index] = item
            colors[2 * index + 1] = item
            // 计算位置（0到1之间均匀分布）
            positions[2 * index] = (index / length).toFloat()
            positions[2 * index + 1] = ((index + 1f) / length).toFloat()
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
    }


    // 设置抗锯齿
    fun setAntiAlias(enabled: Boolean) {
        paint.isAntiAlias = enabled
    }

    class PointFColor(var x: Float, var y: Float, var color: Int)
    companion object {
        private const val TAG = "MultiColorPathRenderer"
    }
}
