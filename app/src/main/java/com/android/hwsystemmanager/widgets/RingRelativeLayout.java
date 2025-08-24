//package com.android.hwsystemmanager.widgets;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.ViewGroup;
//import android.widget.RelativeLayout;
//import androidx.core.app.RunnableC0304a;
//import kotlin.jvm.internal.C4130i;
//import p002a1.C0015i;
//import p002a1.InterfaceC0016j;
//
///* compiled from: RingRelativeLayout.kt */
///* loaded from: classes.dex */
//public final class RingRelativeLayout extends RelativeLayout implements InterfaceC0016j {
//
//    /* renamed from: g */
//    public static final /* synthetic */ int f3971g = 0;
//
//    /* renamed from: a */
//    public boolean f3972a;
//
//    /* renamed from: b */
//    public int f3973b;
//
//    /* renamed from: c */
//    public int f3974c;
//
//    /* renamed from: d */
//    public boolean f3975d;
//
//    /* renamed from: e */
//    public int f3976e;
//
//    /* renamed from: f */
//    public int f3977f;
//
//    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
//    public RingRelativeLayout(Context context, AttributeSet attributeSet) {
//        this(context, attributeSet, 0, 12);
//        C4130i.m10306f(context, "context");
//    }
//
//    @Override // p002a1.InterfaceC0016j
//    /* renamed from: a */
//    public final void mo53a(Integer num, Integer num2) {
//        this.f3975d = true;
//        if (num != null) {
//            this.f3976e = num.intValue();
//        }
//        if (num2 != null) {
//            this.f3977f = num2.intValue();
//        }
//    }
//
//    @Override // android.widget.RelativeLayout, android.view.View
//    public final void onMeasure(int i4, int i8) {
//        post(new RunnableC0304a(1, this));
//        super.onMeasure(i4, i8);
//    }
//
//    public final void setDegenerate(boolean z10) {
//        this.f3972a = z10;
//        if (z10) {
//            setPadding(getPaddingLeft() - C0015i.f21a, getPaddingTop(), getPaddingRight() - C0015i.f22b, getPaddingBottom());
//        }
//    }
//
//    @Override // android.view.View
//    public void setLayoutParams(ViewGroup.LayoutParams layoutParams) {
//        MarginLayoutParams marginLayoutParams;
//        int m45b;
//        if (this.f3975d) {
//            if (layoutParams instanceof MarginLayoutParams) {
//                marginLayoutParams = (MarginLayoutParams) layoutParams;
//            } else {
//                marginLayoutParams = null;
//            }
//            if (marginLayoutParams != null) {
//                int i4 = this.f3976e;
//                int i8 = 0;
//                if (this.f3972a) {
//                    m45b = 0;
//                } else {
//                    m45b = C0015i.m45b();
//                }
//                marginLayoutParams.setMarginStart(i4 + m45b);
//                int i9 = this.f3977f;
//                if (!this.f3972a) {
//                    i8 = C0015i.m44a();
//                }
//                marginLayoutParams.setMarginEnd(i9 + i8);
//            }
//        }
//        super.setLayoutParams(layoutParams);
//        this.f3973b = C0015i.f21a;
//        this.f3974c = C0015i.f22b;
//    }
//
//    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
//    public RingRelativeLayout(Context context, AttributeSet attributeSet, int i4) {
//        this(context, attributeSet, i4, 8);
//        C4130i.m10306f(context, "context");
//    }
//
//    /* JADX WARN: Illegal instructions before constructor call */
//    /*
//        Code decompiled incorrectly, please refer to instructions dump.
//    */
//    public RingRelativeLayout(Context context, AttributeSet attributeSet, int i4, int i8) {
//        super(context, attributeSet, i4, 0);
//        attributeSet = (i8 & 2) != 0 ? null : attributeSet;
//        i4 = (i8 & 4) != 0 ? 0 : i4;
//        C4130i.m10306f(context, "context");
//        C0015i.m49f(getPaddingLeft(), getPaddingRight(), this);
//    }
//}
