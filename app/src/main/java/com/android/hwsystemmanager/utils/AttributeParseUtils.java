package com.android.hwsystemmanager.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

import com.android.hwsystemmanager.MainApplication;


public final class AttributeParseUtils {


    public static final Context f22726a = MainApplication.getContext();


    public static final int m14218a(int i4, boolean z10) {
        Resources.Theme theme;
        int m14221d = m14221d(i4);
        if (m14221d == 0) {
            Logcat.d("AttributeParseUtils", "resource get fail, resId is " + i4);
            return 0;
        }
        Context context = f22726a;
        if (z10) {
            theme = context.getTheme();
        } else {
            theme = null;
        }
        try {
            return context.getResources().getColor(m14221d, theme);
        } catch (Resources.NotFoundException unused) {
            Logcat.d("AttributeParseUtils", "Resource no found resId is " + i4);
            return 0;
        }
    }


    public static final float m14219b(int i4) {
        Context context = f22726a;
        int m14221d = m14221d(i4);
        if (m14221d == 0) {
            Logcat.d("AttributeParseUtils", "resource get fail, resId is " + i4);
            return 0.0f;
        }
        try {
            return context.getResources().getDimension(m14221d);
        } catch (Resources.NotFoundException unused) {
            Logcat.d("AttributeParseUtils", "Resource no found resId is " + i4);
            return 0.0f;
        }
    }


    public static final int m14220c(int i4) {
        Context context = f22726a;
        int m14221d = m14221d(i4);
        if (m14221d == 0) {
            Logcat.d("AttributeParseUtils", "parseDimensionPixelSize but resource get fail, resId is " + i4);
            return 0;
        }
        try {
            return context.getResources().getDimensionPixelSize(m14221d);
        } catch (Resources.NotFoundException unused) {
            Logcat.d("AttributeParseUtils", "parseDimensionPixelSize but resource no found resId is " + i4);
            return 0;
        }
    }


    public static final int m14221d(int i4) {
        int i8;
        TypedValue typedValue = new TypedValue();
        if (!f22726a.getTheme().resolveAttribute(i4, typedValue, true) || (i8 = typedValue.resourceId) == 0) {
            Logcat.d("AttributeParseUtils", "resource get fail, resId is " + i4);
            return 0;
        }
        return i8;
    }
}
