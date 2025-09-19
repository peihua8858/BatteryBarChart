package com.android.hwsystemmanager.compose

import android.graphics.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.android.hwsystemmanager.utils.argb
import com.android.hwsystemmanager.utils.dLog
import com.android.hwsystemmanager.utils.roundToPx
import com.android.hwsystemmanager.utils.writeLogFile
import com.google.gson.Gson

/**
 * 绘制多色路径绘制
 */
class MultiColorPathRenderer {
    private var paint = Paint().apply {
        // 3. 初始化画笔
        this.style = PaintingStyle.Stroke
        this.strokeWidth = 8f
        this.isAntiAlias = true
        strokeJoin = StrokeJoin.Round // 设置线条连接样式为圆角
//        strokeCap = Paint.Cap.BUTT // 设置线条端点样式为圆头
        strokeMiterLimit = 10f
    }
    private var paint2 = android.graphics.Paint().apply {
        // 3. 初始化画笔
        this.style = android.graphics.Paint.Style.STROKE
        this.strokeWidth = 8f
        this.isAntiAlias = true
        strokeJoin = android.graphics.Paint.Join .ROUND // 设置线条连接样式为圆角
    }
    private var colors: Array<Color?> = arrayOf(Color.Transparent, Color.Transparent, Color.Transparent)

    /**
     * 颜色位置（0~1）
     */
    private var positions: FloatArray = floatArrayOf(0f)
    private var path: Path = Path()
    private var path2: android.graphics.Path = android.graphics.Path()
    private var colorStops: Array<Pair<Float, Color>> = Array(0){ Pair(0f, Color.Transparent) }
    fun setData(points: List<PointFColor>) {
        if (points.isEmpty()) {
            return
        }
        // 1. 构建完整路径
        this.path = Path()
        this.path2 =android.graphics.Path()
        // 2. 计算颜色和位置数组
        val length = points.size
        val colors = Array(length * 3){ Color.Transparent }
        val positions = FloatArray(length * 3)
        colorStops = Array(length * 3){ Pair(0f, Color.Transparent) }
        for ((index, item) in points.withIndex()) {
            if (index == 0) {
                path2.moveTo(item.x, item.y)
                path.moveTo(item.x, item.y)
            } else {
                path2.lineTo(item.x, item.y)
                path.lineTo(item.x, item.y)
            }
            // 每个颜色重复两次（起点和终点）
            val prevItem = points.getOrNull(index - 1)
            val nextColor = prevItem?.color ?: Color.Transparent
            val color = if (nextColor != Color.Transparent && nextColor != item.color) {
                item.color.copy(alpha = 0.6f)
            } else {
                item.color
            }
            colorStops[2 * index] = (index.toFloat() / length) to color
            colorStops[2 * index + 1] = ((index + 1f) / length) to item.color
            colorStops[2 * index + 2] = ((index + 2f) / length) to item.color

            if (nextColor != Color.Transparent && nextColor != item.color) {
                colors[2 * index] = item.color.copy(0.6f)
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
//        this.colorStops.addAll(colorsStops)
        writeLogFile { Gson().toJson(positions) }
        writeLogFile { Gson().toJson(colors) }
        writeLogFile { Gson().toJson(colorStops) }
    }
    private var strokeWidth: Float = 8f
    // 设置线宽
    fun setStrokeWidth(width: Float) {
        this.strokeWidth = width
        paint.strokeWidth = width
    }

    fun setPaint(paint: Paint) {
        this.paint = paint
    }

    fun draw(canvas: DrawScope) {
        if (path.isEmpty) return

        // 计算路径的边界
        val rect = path.getBounds()
        // 创建水平方向的渐变
        val gradient = Brush.linearGradient(
            colorStops = colorStops,
            start = Offset(rect.left, 0f),
            end = Offset(rect.right, 0f),
            tileMode = TileMode.Clamp
        )
        canvas.drawPath(path, gradient,
            style = Stroke(width = strokeWidth)
        )
    }
    fun draw(canvas: Canvas) {
        canvas.drawPath(path2, paint2)
    }

    // 设置抗锯齿
    fun setAntiAlias(enabled: Boolean) {
        paint.isAntiAlias = enabled
    }

    companion object {
        private const val TAG = "MultiColorPathRenderer"
    }
}
