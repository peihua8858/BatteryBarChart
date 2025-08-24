package com.android.hwsystemmanager;

import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

import com.android.hwsystemmanager.utils.AttributeParseUtils;
import com.android.hwsystemmanager.utils.Logcat;


public final class BatteryHistoryChartPaintFactory {

    public static TextPaint m11665a(int i4) {
        TextPaint textPaint = new TextPaint(1);
        textPaint.setColor(AttributeParseUtils.m14218a(android.R.attr.textColorSecondary, false));
        textPaint.setAntiAlias(true);
        textPaint.getTextSize();
        textPaint.setTextSize(i4);
        return textPaint;
    }


    public static Paint m11666b(int i4) {
        Paint paint = new Paint(1);
        int valueOf = R.color.stroke_line_color;
        valueOf = R.color.stroke_line_color_card;
        paint.setColor(MainApplication.getContext().getResources().getColor((int) valueOf, null));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(i4);
        return paint;
    }


    public static Paint m11667c(int i4) {
        Paint paint = new Paint(1);
        int valueOf = R.color.stroke_bottom_line_color;
        valueOf = R.color.stroke_y_line_color_card;
        paint.setColor(MainApplication.getContext().getResources().getColor((int) valueOf, null));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(i4);
        paint.setPathEffect(new DashPathEffect(new float[]{8.0f, 8.0f}, 0.0f));
        return paint;
    }


    public static float m11668d(String str, TextPaint textPaint) {
        Rect rect = new Rect();
        textPaint.getTextBounds(str, 0, str.length(), rect);
        float width = rect.width();
        Logcat.d("BatteryHistoryChartPaintFactory", "width is " + width);
        return width;
    }
}
