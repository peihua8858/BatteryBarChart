package com.android.hwsystemmanager.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.android.hwsystemmanager.R;

import java.util.Arrays;

@SuppressLint({"AppCompatCustomView"})
/* loaded from: classes2.dex */
public class RoundRectImageView extends ImageView {
    public static final int[] f25c = {R.attr.height_percent, R.attr.round_radius, R.attr.shape_mode};
    /* renamed from: a */
    public float f8937a;

    /* renamed from: b */
    public int f8938b;

    /* renamed from: c */
    public float f8939c;

    /* renamed from: d */
    public final Path f8940d;

    public RoundRectImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.f8937a = 0.0f;
        this.f8940d = new Path();
        m6788a(attributeSet);
    }

    /* renamed from: a */
    public final void m6788a(AttributeSet attributeSet) {
        setLayerType(LAYER_TYPE_HARDWARE, null);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, f25c);
            this.f8937a = obtainStyledAttributes.getDimension(1, 0.0f);
            this.f8938b = obtainStyledAttributes.getInt(2, 1);
            this.f8939c = obtainStyledAttributes.getFloat(0, 0.0f);
            obtainStyledAttributes.recycle();
        }
    }

    @Override // android.widget.ImageView, android.view.View
    public final void onDraw(Canvas canvas) {
        canvas.clipPath(this.f8940d);
        super.onDraw(canvas);
    }

    @Override // android.view.View
    public final void onLayout(boolean z10, int i4, int i8, int i9, int i10) {
        super.onLayout(z10, i4, i8, i9, i10);
        if (z10) {
            float f10 = this.f8937a;
            if (f10 == 0.0f) {
                return;
            }
            float[] fArr = new float[8];
            if (this.f8938b == 2) {
                Arrays.fill(fArr, 4, 8, f10);
            } else {
                Arrays.fill(fArr, f10);
            }
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();
            Path path = this.f8940d;
            path.reset();
            path.addRoundRect(new RectF(0.0f, 0.0f, measuredWidth, measuredHeight), fArr, Path.Direction.CW);
        }
    }

    @Override // android.widget.ImageView, android.view.View
    public final void onMeasure(int i4, int i8) {
        int i9;
        super.onMeasure(i4, i8);
        int measuredWidth = getMeasuredWidth();
        if (Float.compare(0.0f, this.f8939c) == 0) {
            i9 = getMeasuredHeight();
        } else {
            i9 = (int) (measuredWidth * this.f8939c);
        }
        setMeasuredDimension(measuredWidth, i9);
    }

    public RoundRectImageView(Context context, AttributeSet attributeSet, int i4) {
        super(context, attributeSet, i4);
        this.f8937a = 0.0f;
        this.f8940d = new Path();
        m6788a(attributeSet);
    }
}
