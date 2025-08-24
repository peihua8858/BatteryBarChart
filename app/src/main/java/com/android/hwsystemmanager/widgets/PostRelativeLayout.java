//package com.android.hwsystemmanager.widgets;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.view.ViewGroup;
//import android.widget.RelativeLayout;
//
///* compiled from: PostRelativeLayout.kt */
///* loaded from: classes.dex */
//public final class PostRelativeLayout extends RelativeLayout {
//
//    /* renamed from: a */
//    public static final /* synthetic */ int f3963a = 0;
//
//    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
//    public PostRelativeLayout(Context context, AttributeSet attributeSet) {
//        this(context, attributeSet, 0, 12);
//    }
//
//    @Override // android.widget.RelativeLayout, android.view.View, android.view.ViewParent
//    public final void requestLayout() {
//        super.requestLayout();
//        post(new Runn(4, this));
//    }
//
//    public final void setHeight(int i4) {
//        ViewGroup.LayoutParams layoutParams = getLayoutParams();
//        layoutParams.height = i4;
//        setLayoutParams(layoutParams);
//        requestLayout();
//    }
//
//    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
//    public PostRelativeLayout(Context context, AttributeSet attributeSet, int i4) {
//        this(context, attributeSet, i4, 8);
//    }
//
//    /* JADX WARN: Illegal instructions before constructor call */
//    /*
//        Code decompiled incorrectly, please refer to instructions dump.
//    */
//    public PostRelativeLayout(Context context, AttributeSet attributeSet, int i4, int i8) {
//        super(context, attributeSet, i4, 0);
//        attributeSet = (i8 & 2) != 0 ? null : attributeSet;
//        i4 = (i8 & 4) != 0 ? 0 : i4;
//    }
//}
