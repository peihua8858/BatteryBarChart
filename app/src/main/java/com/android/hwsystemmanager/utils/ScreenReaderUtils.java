package com.android.hwsystemmanager.utils;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.provider.Settings;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;

import com.android.hwsystemmanager.MainApplication;

import java.util.List;

public final class ScreenReaderUtils {


    public static final AccessibilityManager f16815a;


    public static final class a extends View.AccessibilityDelegate {


        boolean f16816a;


        boolean f16817b;

        public a(boolean z10, boolean z11) {
            this.f16816a = z10;
            this.f16817b = z11;
        }

        @Override 
        public final void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            info.setClickable(this.f16816a);
            if (this.f16817b) {
                info.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
            }
        }
    }


    public static final class b extends View.AccessibilityDelegate {
        @Override 
        public final void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            info.setClassName(Button.class.getName());
        }
    }

    public final static class C4250b extends View.AccessibilityDelegate {


        boolean f16818a = true;


        boolean f16819b;


        boolean f16820c;

        public C4250b(boolean z10, boolean z11) {
            this.f16819b = z10;
            this.f16820c = z11;
        }

        @Override 
        public final void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            info.setEnabled(this.f16818a);
            info.setClickable(this.f16819b);
            if (this.f16820c) {
                info.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
            }
        }
    }

    static {
        AccessibilityManager accessibilityManager;
        Object systemService = MainApplication.getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (systemService instanceof AccessibilityManager) {
            accessibilityManager = (AccessibilityManager) systemService;
        } else {
            accessibilityManager = null;
        }
        f16815a = accessibilityManager;
    }


    public static boolean m10470a() {
        int i4 = Settings.Secure.getInt(MainApplication.getContext().getContentResolver(), "accessibility_screenreader_enabled", 0);
        Logcat.d("ScreenReaderUtils", "screen reader is " + i4);
        if (i4 != 1) {
            return false;
        }
        return true;
    }


    public static final boolean m10471b() {
        return m10470a();
    }

    public static final boolean m10472c() {
        List<AccessibilityServiceInfo> list;
        boolean bool;
        boolean z10 = true;
        boolean z11;
        if (m10470a()) {
            return true;
        }
        Integer num = null;
        AccessibilityManager accessibilityManager = f16815a;
        if (accessibilityManager != null) {
            list = accessibilityManager.getEnabledAccessibilityServiceList(1);
        } else {
            list = null;
        }
        if (accessibilityManager != null) {
            bool = accessibilityManager.isTouchExplorationEnabled();
        } else {
            bool = false;
        }
        if (list != null) {
            num = list.size();
        }
        Logcat.d("ScreenReaderUtils", "3th screen reader is " + bool + " -- " + num);
        if (bool) {
            z11 = list != null && list.isEmpty();
            z10 = !z11;
        }
        return !z10;

    }


    public static final void m10473d(View view, boolean z10, boolean z11) {
        if (view != null) {
            view.setAccessibilityDelegate(new a(z10, z11));
        }
    }


    public static final void m10474e(View view, boolean z10, boolean z11) {
        if (view != null) {
            view.setAccessibilityDelegate(new C4250b(z10, z11));
        }
    }


    public static final void m10475f(View view) {
        if (view != null) {
            view.setAccessibilityDelegate(new b());
        }
    }
}
