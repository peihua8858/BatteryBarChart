//package com.android.hwsystemmanager.widgets;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import androidx.activity.RunnableC0116a;
//
///* compiled from: RingLinearLayout.kt */
///* loaded from: classes.dex */
//public final class RingLinearLayout extends LinearLayout implements InterfaceC0016j {
//
//    /* renamed from: g */
//    public static final /* synthetic */ int f3964g = 0;
//
//    /* renamed from: a */
//    public boolean f3965a;
//
//    /* renamed from: b */
//    public int f3966b;
//
//    /* renamed from: c */
//    public int f3967c;
//
//    /* renamed from: d */
//    public boolean f3968d;
//
//    /* renamed from: e */
//    public int f3969e;
//
//    /* renamed from: f */
//    public int f3970f;
//
//    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
//    public RingLinearLayout(Context context, AttributeSet attributeSet) {
//        this(context, attributeSet, 0, 12);
//    }
//
//    @Override // p002a1.InterfaceC0016j
//    /* renamed from: a */
//    public final void mo53a(Integer num, Integer num2) {
//        this.f3968d = true;
//        if (num != null) {
//            this.f3969e = num.intValue();
//        }
//        if (num2 != null) {
//            this.f3970f = num2.intValue();
//        }
//    }
//
//    @Override // android.widget.LinearLayout, android.view.View
//    public final void onMeasure(int i4, int i8) {
//        post(new RunnableC0116a(3, this));
//        super.onMeasure(i4, i8);
//    }
//
//    public final void setDegenerate(boolean z10) {
//        this.f3965a = z10;
//        if (z10) {
//            setPadding(getPaddingLeft() - C0015i.f21a, getPaddingTop(), getPaddingRight() - C0015i.f22b, getPaddingBottom());
//        }
//    }
//
//    public final void setHeight(int i4) {
//        ViewGroup.LayoutParams layoutParams = getLayoutParams();
//        layoutParams.height = i4;
//        setLayoutParams(layoutParams);
//        requestLayout();
//    }
//
//    @Override // android.view.View
//    public void setLayoutParams(ViewGroup.LayoutParams layoutParams) {
//        MarginLayoutParams marginLayoutParams;
//        int m45b;
//        if (this.f3968d) {
//            if (layoutParams instanceof MarginLayoutParams) {
//                marginLayoutParams = (MarginLayoutParams) layoutParams;
//            } else {
//                marginLayoutParams = null;
//            }
//            if (marginLayoutParams != null) {
//                int i4 = this.f3969e;
//                int i8 = 0;
//                if (this.f3965a) {
//                    m45b = 0;
//                } else {
//                    m45b = C0015i.m45b();
//                }
//                marginLayoutParams.setMarginStart(i4 + m45b);
//                int i9 = this.f3970f;
//                if (!this.f3965a) {
//                    i8 = C0015i.m44a();
//                }
//                marginLayoutParams.setMarginEnd(i9 + i8);
//            }
//        }
//        super.setLayoutParams(layoutParams);
//        this.f3966b = C0015i.f21a;
//        this.f3967c = C0015i.f22b;
//    }
//
//    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
//    public RingLinearLayout(Context context, AttributeSet attributeSet, int i4) {
//        this(context, attributeSet, i4, 8);
//        C4130i.m10306f(context, "context");
//    }
//
//    /* JADX WARN: Illegal instructions before constructor call */
//    /*
//        Code decompiled incorrectly, please refer to instructions dump.
//    */
//    public RingLinearLayout(Context context, AttributeSet attributeSet, int i4, int i8) {
//        super(context, attributeSet, i4, 0);
//        attributeSet = (i8 & 2) != 0 ? null : attributeSet;
//        i4 = (i8 & 4) != 0 ? 0 : i4;
//        C4130i.m10306f(context, "context");
//        C0015i.m49f(getPaddingLeft(), getPaddingRight(), this);
//    }
//}
