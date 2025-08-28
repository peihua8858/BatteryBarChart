package com.android.hwsystemmanager;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.os.Build;

import com.android.hwsystemmanager.utils.ContextExtKt;
import com.android.hwsystemmanager.utils.Logcat;

import java.text.NumberFormat;

public final class BatteryStackBarData {
    // 常量定义
    private static final int DRAW_TYPE_MOVE_TO = 1;
    private static final int DRAW_TYPE_LINE_TO_END = 2;
    private static final int DRAW_TYPE_LINE_TO_AND_DRAW = 3;
    private static final int DRAW_TYPE_MOVE_TO_AND_DRAW = 4;

    private static final float BAR_WIDTH_RATIO = 2.0f / 3.0f;
    private static final float BAR_OFFSET_RATIO = 1.0f / 3.0f;
    private static final float STROKE_WIDTH_DP = 2.0f;

    // 曲线相关常量
    private static final float CURVE_LINE_WIDTH = 3.0f;
    private static final int CURVE_COLOR = 0xFF4285F4; // 蓝色曲线

    // 坐标和尺寸
    public final float x;
    public final float y;
    public final float width;
    public final float height;
    public final LevelAndCharge levelAndCharge;

    // 状态和属性
    public int state;
    public final String percentageText;
    public int barType;
    public boolean showBubble;
    public int bubbleState;
    public final String chargingStatus;
    public final float barWidth;

    // 绘制工具
    public final Paint fillPaint;
    public final Paint strokePaint;
    public final Paint additionalPaint;
    public final Paint curvePaint; // 曲线绘制工具
    public final float barOffset;

    // 颜色资源ID
    public int noChargeLineColor50;
    public int noChargeLineColor10;
    public int chargeLineColor50;
    public int chargeLineColor10;
    public int noChargeLineNewColor;
    public int chargeLineNewColor;

    // 上下文资源
    private final Resources resources;
    private final boolean isCardMode;

    // 曲线点坐标
    public float curveX;
    public float curveY;

    // 曲线连接区域路径
    public final Path curveAreaPath;

    public BatteryStackBarData(float x, float y, float width, float height, LevelAndCharge levelAndCharge) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.levelAndCharge = levelAndCharge;

        // 初始化资源
        this.resources = MainApplication.getContext().getResources();
        this.isCardMode = Build.VERSION.SDK_INT >= 27;

        // 计算百分比文本
        this.percentageText = NumberFormat.getPercentInstance()
                .format(levelAndCharge.getLevel() / 100.0d);

        this.barType = 1;
        this.chargingStatus = levelAndCharge.getCharge();
        this.barWidth = width * BAR_WIDTH_RATIO;
        this.barOffset = width * BAR_OFFSET_RATIO;

        // 初始化绘制工具
        this.fillPaint = new Paint();
        this.strokePaint = new Paint();
        this.additionalPaint = new Paint();
        this.curvePaint = new Paint(); // 初始化曲线绘制工具
        this.curveAreaPath = new Path(); // 初始化曲线区域路径

        // 初始化颜色资源
        initColorResources();

        // 初始化绘制工具
        initializePaints();

        // 初始化曲线绘制工具
        initializeCurvePaint();

        // 计算曲线点坐标
        calculateCurvePoint();
    }

    /**
     * 初始化颜色资源
     */
    private void initColorResources() {
        noChargeLineColor50 = getColorResource(
                R.color.hsm_widget_canvas_no_charge_line_alpha50,
                R.color.hsm_widget_canvas_no_charge_line_alpha50_card
        );

        noChargeLineColor10 = getColorResource(
                R.color.hsm_widget_canvas_no_charge_line_alpha10,
                R.color.hsm_widget_canvas_no_charge_line_alpha10_card
        );

        chargeLineColor50 = getColorResource(
                R.color.hsm_widget_canvas_charge_line_alpha50,
                R.color.hsm_widget_canvas_charge_line_alpha50_card
        );

        chargeLineColor10 = getColorResource(
                R.color.hsm_widget_canvas_charge_line_alpha10,
                R.color.hsm_widget_canvas_charge_line_alpha10_card
        );

        noChargeLineNewColor = getColorResource(
                R.color.hsm_widget_canvas_no_charge_line_new,
                R.color.hsm_widget_canvas_no_charge_line_new_card
        );

        chargeLineNewColor = getColorResource(
                R.color.hsm_widget_canvas_charge_line_new,
                R.color.hsm_widget_canvas_charge_line_new_card
        );
    }

    /**
     * 根据模式获取颜色资源
     */
    private int getColorResource(int normalColorRes, int cardColorRes) {
        return resources.getColor(isCardMode ? cardColorRes : normalColorRes);
    }

    /**
     * 初始化绘制工具
     */
    private void initializePaints() {
        // 设置公共属性
        fillPaint.setAntiAlias(true);
        strokePaint.setAntiAlias(true);
        additionalPaint.setAntiAlias(true);

        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeJoin(Paint.Join.ROUND);
        strokePaint.setStrokeWidth(ContextExtKt.dp2px(MainApplication.getContext(), STROKE_WIDTH_DP));

        // 根据状态更新绘制工具
        updatePaints();
    }

    /**
     * 初始化曲线绘制工具
     */
    private void initializeCurvePaint() {
        curvePaint.setAntiAlias(true);
        curvePaint.setStyle(Paint.Style.STROKE);
        curvePaint.setStrokeWidth(ContextExtKt.dp2px(MainApplication.getContext(), CURVE_LINE_WIDTH));
        curvePaint.setColor(CURVE_COLOR);
        curvePaint.setStrokeJoin(Paint.Join.ROUND);
        curvePaint.setStrokeCap(Paint.Cap.ROUND);

        // 确保是实线而不是虚线
        curvePaint.setPathEffect(null); // 清除任何可能的路劲效果（如虚线）

        // 曲线颜色与柱状图选中状态一致
        updateCurvePaintColor();
    }

    /**
     * 更新曲线绘制工具颜色
     */
    private void updateCurvePaintColor() {
        if (barType == 1) { // 选中状态
            boolean isCharging = "true".equals(chargingStatus);
            if (isCharging) {
                curvePaint.setColor(chargeLineNewColor);
            } else {
                curvePaint.setColor(noChargeLineNewColor);
            }
        } else { // 未选中状态
            curvePaint.setColor(getColorResource(
                    R.color.battery_not_select_bg_bottom,
                    R.color.battery_not_select_bg_bottom_card
            ));
        }
    }

    /**
     * 计算曲线点坐标
     */
    private void calculateCurvePoint() {
        // 计算当前电平的位置
        float currentLevelPos = calculateLevelPosition(levelAndCharge.getLevel());

        // 计算条形图边界
        float halfBarOffset = barOffset / 2;
        this.curveX = x + width - halfBarOffset;
        this.curveY = currentLevelPos;
    }

    /**
     * 更新绘制工具状态
     */
    public final void updatePaints() {
        if (barType == 0 || barType == 2) {
            // 未选中状态
            setupUnselectedPaints();
        } else if (barType == 1) {
            // 选中状态，根据充电状态设置不同颜色
            setupSelectedPaints();
        }

        // 更新曲线绘制工具颜色
        updateCurvePaintColor();

        // 设置附加绘制工具的渐变
        setupAdditionalPaintGradient();
    }

    /**
     * 设置未选中状态的绘制工具
     */
    private void setupUnselectedPaints() {
        int bgColor = getColorResource(
                R.color.battery_not_select_bg,
                R.color.battery_not_select_bg
        );

        LinearGradient gradient = createGradientWithColorValues(bgColor, bgColor);
        fillPaint.setShader(gradient);

        int strokeColor = getColorResource(
                R.color.battery_not_select_bg_bottom,
                R.color.battery_not_select_bg_bottom_card
        );
        strokePaint.setColor(strokeColor);
    }

    /**
     * 设置选中状态的绘制工具
     */
    private void setupSelectedPaints() {
        boolean isCharging = "true".equals(chargingStatus);

        if (isCharging) {
            // 充电状态
            LinearGradient gradient = createGradientWithColorValues(chargeLineColor50, chargeLineColor10);
            fillPaint.setShader(gradient);
            strokePaint.setColor(chargeLineNewColor);
        } else {
            // 非充电状态
            LinearGradient gradient = createGradientWithColorValues(noChargeLineColor50, noChargeLineColor10);
            fillPaint.setShader(gradient);
            strokePaint.setColor(noChargeLineNewColor);
        }
    }

    /**
     * 设置附加绘制工具的渐变
     */
    private void setupAdditionalPaintGradient() {
        int color = getColorResource(R.color.battery_chart_set, R.color.battery_chart_set);
        LinearGradient gradient = createGradientWithColorValues(color, color);
        additionalPaint.setShader(gradient);
    }

    /**
     * 创建渐变（使用颜色值）
     */
    private LinearGradient createGradientWithColorValues(int startColor, int endColor) {
        return new LinearGradient(
                x, y,
                x + barWidth, y + height,
                startColor, endColor,
                Shader.TileMode.CLAMP
        );
    }

    /**
     * 绘制条形图
     */
    public final void drawBar(Canvas canvas, boolean isCharging, int prevLevel, Path path, int drawType) {
        if (canvas == null) return;

        Logcat.d("BatteryStackBarData", "level is " + levelAndCharge.getLevel() + " --charging : " + isCharging);

        updatePaints();

        // 计算当前和上一个电平的位置
        float currentLevelPos = calculateLevelPosition(levelAndCharge.getLevel());
        float prevLevelPos = calculateLevelPosition(prevLevel);

        // 计算条形图边界
        float halfBarOffset = barOffset / 2;
        float left = x - halfBarOffset;
        float right = x + width - halfBarOffset;

        // 根据绘制类型处理路径
        handlePathByDrawType(path, drawType, left, prevLevelPos, right, currentLevelPos);

        // 绘制条形图填充
//        drawBarFill(canvas, left, currentLevelPos, right);

        // 更新曲线点坐标
        this.curveX = right;
        this.curveY = currentLevelPos;
    }

    /**
     * 绘制条形图填充和连接区域
     */
    private void drawBarFillAndConnectionArea(Canvas canvas, float left, float currentLevelPos, float right, BatteryStackBarData prevBarData) {
        Path fillPath = new Path();
        if (prevBarData != null) {
            fillPath.moveTo(prevBarData.curveX, prevBarData.curveY);
            fillPath.lineTo(this.curveX, this.curveY);
            fillPath.lineTo(this.curveX, y + height);
            fillPath.lineTo(prevBarData.curveX, y + height);
        }
//        fillPath.close();
        // 使用与柱状图相同的填充颜色绘制连接区域
//        canvas.drawPath(curveAreaPath, fillPaint);


        // 如果有前一个条形图，绘制连接区域
//        if (prevBarData != null) {
//            // 计算前一个条形图的曲线点
//            float prevCurveX = prevBarData.x + prevBarData.width - (prevBarData.barOffset / 2);
//            float prevCurveY = prevBarData.calculateLevelPosition(prevBarData.levelAndCharge.getLevel());
//
//            // 创建连接区域路径
//            fillPath.moveTo(prevCurveX, prevCurveY);
//            fillPath.lineTo(left, currentLevelPos);
//            fillPath.lineTo(left, y + height);
//            fillPath.lineTo(prevCurveX, prevBarData.y + prevBarData.height);
//            fillPath.close();
//        }

        // 绘制当前柱状图
        fillPath.moveTo(left, currentLevelPos);
        fillPath.lineTo(right, currentLevelPos);
        fillPath.lineTo(right, y + height);
        fillPath.lineTo(left, y + height);
        fillPath.close();

        canvas.drawPath(fillPath, fillPaint);
    }

    public void drawCurve(Canvas canvas, boolean isCharge, int i4, Path path, int i8) {
        LevelAndCharge levelAndCharge = this.levelAndCharge;
        Logcat.d("BatteryStackBarData", "level is ${levelAndCharge.level} --charging : $isCharge");
//        m11671c();
        int f10 = (100 - levelAndCharge.getLevel());
        float f11 = this.width;
        float f12 = 100f;
        float f13 = this.y;
        float f14 = ((f10 * f11) / f12) + f13;
        float f15 = (((100 - i4) * f11) / f12) + f13;
        float f16 = this.barOffset / 2;
        float f17 = this.x;
        float f18 = f17 - f16;
        float f19 = (f17 + this.width) - f16;
        if (i8 != 1) {
            if (i8 != 2) {
                Paint paint = this.f19398n;
                if (i8 != 3) {
                    if (i8 == 4) {
                        path.moveTo(f18, f15);
                        path.lineTo(f19, f14);
                        canvas.drawPath(path, paint);
                    }
                } else {
                    path.lineTo(f19, f14);
                    canvas.drawPath(path, paint);
                }
            } else {
                path.lineTo(f19, f14);
            }
        } else {
            path.moveTo(f18, f15);
            path.lineTo(f19, f14);
        }
        Path path2 = new Path();
        path2.moveTo(f18, f15);
        path2.lineTo(f19, f14);
        float f20 = f13 + f11;
        path2.lineTo(f19, f20);
        path2.lineTo(f18, f20);
        path2.close();
        canvas.drawPath(path2, this.f19397m);
    }

    /**
     * 绘制曲线
     */
    public final void drawCurve(Canvas canvas, BatteryStackBarData prevBarData) {
        if (canvas == null) return;

        // 如果有前一个条形图，绘制连接线
        if (prevBarData != null) {
            // 计算前一个条形图的曲线点
            float prevCurveX = prevBarData.x + prevBarData.width - (prevBarData.barOffset / 2);
            float prevCurveY = prevBarData.calculateLevelPosition(prevBarData.levelAndCharge.getLevel());

            // 绘制连接线
            Path curvePath = new Path();
            curvePath.moveTo(prevCurveX, prevCurveY);
            curvePath.lineTo(this.curveX, this.curveY);
            canvas.drawPath(curvePath, curvePaint);
            // 计算条形图边界
            float halfBarOffset = barOffset / 2;
            float left = x - halfBarOffset;
            float right = x + width - halfBarOffset;
            // 计算当前和上一个电平的位置
            float currentLevelPos = calculateLevelPosition(levelAndCharge.getLevel());
            drawBarFillAndConnectionArea(canvas, left, currentLevelPos, right, prevBarData);
            // 绘制连接区域（三角区域）
//            drawConnectionArea(canvas, prevBarData);
        }
    }

    /**
     * 绘制连接区域（三角区域）
     */
    private void drawConnectionArea(Canvas canvas, BatteryStackBarData prevBarData) {
        // 创建连接区域路径
        curveAreaPath.reset();
        curveAreaPath.moveTo(prevBarData.curveX, prevBarData.curveY);
        curveAreaPath.lineTo(this.curveX, this.curveY);
        curveAreaPath.lineTo(this.curveX, y + height);
        curveAreaPath.lineTo(prevBarData.curveX, y + height);
        curveAreaPath.close();
        // 使用与柱状图相同的填充颜色绘制连接区域
        canvas.drawPath(curveAreaPath, fillPaint);
    }

    /**
     * 计算电平位置
     */
    private float calculateLevelPosition(int level) {
        float levelRatio = (100 - level) / 100.0f;
        return y + (levelRatio * height);
    }

    /**
     * 根据绘制类型处理路径
     */
    private void handlePathByDrawType(Path path, int drawType, float left, float prevLevelPos, float right, float currentLevelPos) {
        switch (drawType) {
            case DRAW_TYPE_MOVE_TO:
                path.moveTo(left, prevLevelPos);
                path.lineTo(right, currentLevelPos);
                break;

            case DRAW_TYPE_LINE_TO_END:
                path.lineTo(right, currentLevelPos);
                break;

            case DRAW_TYPE_LINE_TO_AND_DRAW:
                path.lineTo(right, currentLevelPos);
                // 这里应该绘制路径，但原始代码中没有实现
                break;

            case DRAW_TYPE_MOVE_TO_AND_DRAW:
                path.moveTo(left, prevLevelPos);
                path.lineTo(right, currentLevelPos);
                // 绘制路径
                // canvas.drawPath(path, strokePaint);
                break;
        }
    }

    /**
     * 绘制条形图填充
     */
    private void drawBarFill(Canvas canvas, float left, float currentLevelPos, float right) {
        Path fillPath = new Path();
        fillPath.moveTo(left, currentLevelPos);
        fillPath.lineTo(right, currentLevelPos);
        fillPath.lineTo(right, y + height);
        fillPath.lineTo(left, y + height);
        fillPath.close();

        canvas.drawPath(fillPath, fillPaint);
    }

    /**
     * 创建渐变（使用颜色资源ID）
     */
    public final LinearGradient createGradientFromResourceIds(int startColorRes, int endColorRes) {
        int startColor = resources.getColor(startColorRes);
        int endColor = resources.getColor(endColorRes);

        return new LinearGradient(
                x, y,
                x + barWidth, y + height,
                startColor, endColor,
                Shader.TileMode.CLAMP
        );
    }

    /**
     * 设置选中状态
     */
    public void setSelected(boolean selected) {
        this.barType = selected ? 1 : 0;
        this.showBubble = selected;
        updatePaints();
    }
    /* renamed from: m */
    public  Paint f19397m;

    /* renamed from: n */
    public  Paint f19398n;

    /* renamed from: o */
    public  Paint f19399o;
//    public final void m11671c() {
//        LinearGradient m11670b;
//        int color;
//        int i4 = this.barType;
//        Paint paint =this.f19397m;
//        Paint paint2 = this.f19398n;
//        Context context =MainApplication.getContext();
//        if (i4 != 0) {
//            if (i4 == 1) {
//                String str = this.chargingStatus;
//                if (!C4130i.m10301a(str, "true")) {
//                    m11670b = m11670b(context.getResources().getColor(this.f19401q), context.getResources().getColor(this.f19402r));
//                } else {
//                    m11670b = m11670b(context.getResources().getColor(this.f19403s),context.getResources().getColor(this.f19404t));
//                }
//                paint.setShader(m11670b);
//                if (!C4130i.m10301a(str, "true")) {
//                    color =context.getResources().getColor(this.f19405u);
//                } else {
//                    color =context.getResources().getColor(this.f19406v);
//                }
//                paint2.setColor(color);
//            }
//            LinearGradient m11670b2 = m11670b(context.getResources().getColor(R.color.battery_chart_set),context.getResources().getColor(R.color.battery_chart_set));
//            Paint paint3 = this.f19399o;
//            paint3.setShader(m11670b2);
//            paint3.setAntiAlias(true);
//            paint.setAntiAlias(true);
//            paint2.setAntiAlias(true);
//            paint2.setStyle(Paint.Style.STROKE);
//            paint2.setStrokeJoin(Paint.Join.ROUND);
//            paint2.setStrokeWidth(C4253e.m10476a(2.0f));
//        }
//        paint.setShader(m11670b(context.getResources().getColor(R.color.battery_not_select_bg),context.getResources().getColor(R.color.battery_not_select_bg)));
//        Resources resources =context.getResources();
//        int valueOf = R.color.battery_not_select_bg_bottom;
//        int valueOf2 = R.color.battery_not_select_bg_bottom_card;
//        if (C5820e.f22494a) {
//            valueOf = valueOf2;
//        }
//        paint2.setColor(resources.getColor((int) valueOf));
//        LinearGradient m11670b22 = m11670b(context.getResources().getColor(R.color.battery_chart_set),context.getResources().getColor(R.color.battery_chart_set));
//        Paint paint32 = this.f19399o;
//        paint32.setShader(m11670b22);
//        paint32.setAntiAlias(true);
//        paint.setAntiAlias(true);
//        paint2.setAntiAlias(true);
//        paint2.setStyle(Paint.Style.STROKE);
//        paint2.setStrokeJoin(Paint.Join.ROUND);
//        paint2.setStrokeWidth(C4253e.m10476a(2.0f));
//    }
    public final LinearGradient m11670b(int i4, int i8) {
        float f10 = this.x;
        float f11 = this.y;
        return new LinearGradient(f10, f11, f10 + this.barWidth, f11 + this.height, i4, i8, Shader.TileMode.CLAMP);
    }
    @Override
    public String toString() {
        return "BatteryStackBarData{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", levelAndCharge=" + levelAndCharge +
                ", state=" + state +
                ", percentageText='" + percentageText + '\'' +
                ", barType=" + barType +
                ", showBubble=" + showBubble +
                ", bubbleState=" + bubbleState +
                ", chargingStatus='" + chargingStatus + '\'' +
                ", barWidth=" + barWidth +
                ", barOffset=" + barOffset +
                ", noChargeLineColor50=" + noChargeLineColor50 +
                ", noChargeLineColor10=" + noChargeLineColor10 +
                ", chargeLineColor50=" + chargeLineColor50 +
                ", chargeLineColor10=" + chargeLineColor10 +
                ", noChargeLineNewColor=" + noChargeLineNewColor +
                ", chargeLineNewColor=" + chargeLineNewColor +
                ", isCardMode=" + isCardMode +
                ", curveX=" + curveX +
                ", curveY=" + curveY +
                ", curveAreaPath=" + curveAreaPath +
                '}';
    }
}