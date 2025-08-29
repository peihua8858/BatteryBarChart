package com.android.hwsystemmanager.multicolor;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;

import java.util.List;

/**
 * 绘制多色路径绘制
 */
public class MultiColorPathRenderer {
    private static final String TAG = "MultiColorPathRenderer";

    private final Paint paint = new Paint();
    ;
    private int[] colors;
    /**
     * 颜色位置（0~1）
     */
    private float[] positions;
    private Path path;

    public void setData(List<PointF> points, int[] segmentColors) {
        if (points == null || segmentColors == null || segmentColors.length != points.size() - 1) {
            throw new IllegalArgumentException("Invalid points or colors");
        }
        // 1. 构建完整路径
        this.path = new Path();
        path.moveTo(points.get(0).x, points.get(0).y);
        for (int i = 1; i < points.size(); i++) {
            path.lineTo(points.get(i).x, points.get(i).y);
        }

        // 2. 计算颜色和位置数组
        this.colors = new int[segmentColors.length * 2];
        this.positions = new float[segmentColors.length * 2];

        for (int i = 0; i < segmentColors.length; i++) {
            // 每个颜色重复两次（起点和终点）
            colors[2 * i] = segmentColors[i];
            colors[2 * i + 1] = segmentColors[i];

            // 计算位置（0到1之间均匀分布）
            positions[2 * i] = i / (float) segmentColors.length;
            positions[2 * i + 1] = (i + 1) / (float) segmentColors.length;
        }

        // 3. 初始化画笔
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8f);
        paint.setAntiAlias(true);

    }

    // 设置线宽
    public void setStrokeWidth(float width) {
        paint.setStrokeWidth(width);
    }

    public void draw(Canvas canvas) {
        if (path.isEmpty()) return;

        // 计算路径的边界
        RectF bounds = new RectF();
        path.computeBounds(bounds, true);

        // 创建水平方向的渐变
        LinearGradient gradient = new LinearGradient(
                bounds.left, 0, bounds.right, 0,
                colors,
                positions,
                Shader.TileMode.CLAMP
        );
        paint.setShader(gradient);
        canvas.drawPath(path, paint);
    }


    // 设置抗锯齿
    public void setAntiAlias(boolean enabled) {
        paint.setAntiAlias(enabled);
    }
}
