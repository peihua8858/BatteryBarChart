package com.android.hwsystemmanager.utils;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.provider.Settings;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.android.hwsystemmanager.MainApplication;

import java.util.List;

public final class ScreenReaderUtils {


    public static final AccessibilityManager MANAGER;


    static {
        AccessibilityManager accessibilityManager;
        Object systemService = MainApplication.getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (systemService instanceof AccessibilityManager) {
            accessibilityManager = (AccessibilityManager) systemService;
        } else {
            accessibilityManager = null;
        }
        MANAGER = accessibilityManager;
    }

    public static final class AccessibilityConfigurator extends View.AccessibilityDelegate {

        private boolean isClickable;
        private boolean shouldRemoveClickAction;

        public AccessibilityConfigurator(boolean isClickable, boolean shouldRemoveClickAction) {
            this.isClickable = isClickable;
            this.shouldRemoveClickAction = shouldRemoveClickAction;
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(@NonNull View host, @NonNull AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            info.setClickable(isClickable);
            if (shouldRemoveClickAction) {
                info.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
            }
        }
    }


    public static final class ButtonClassModifier extends View.AccessibilityDelegate {
        @Override
        public void onInitializeAccessibilityNodeInfo(@NonNull View host, @NonNull AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            info.setClassName(Button.class.getName());
        }
    }

    public final static class ClickableControlDelegate extends View.AccessibilityDelegate {

        boolean enabled = true;

        boolean clickable;

        boolean removeClickAction;

        public ClickableControlDelegate(boolean clickable, boolean removeClickAction) {
            this.clickable = clickable;
            this.removeClickAction = removeClickAction;
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(host, info);
            info.setEnabled(enabled);
            info.setClickable(clickable);
            if (removeClickAction) {
                info.removeAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
            }
        }
    }


    public static boolean isScreenReaderEnabled() {
        int isEnabled = Settings.Secure.getInt(MainApplication.getContext().getContentResolver(), "accessibility_screenreader_enabled", 0);
        Logcat.d("ScreenReaderUtils", "screen reader is " + isEnabled);
        return isEnabled == 1;
    }


    public static boolean m10471b() {
        return isScreenReaderEnabled();
    }

    public static boolean checkScreenReaderStatus() {
        boolean touchExplorationEnabled;
        boolean result = true;
        boolean isEmpty;
        if (isScreenReaderEnabled()) {
            return true;
        }
        int serviceCount = 0;
        AccessibilityManager manager = MANAGER;
        List<AccessibilityServiceInfo> serviceList = manager != null ? manager.getEnabledAccessibilityServiceList(1) : null;
        touchExplorationEnabled = manager != null && manager.isTouchExplorationEnabled();
        if (serviceList != null) {
            serviceCount = serviceList.size();
        }
        Logcat.d("ScreenReaderUtils", "3rd screen reader status: " + touchExplorationEnabled + " -- " + serviceCount);
        if (touchExplorationEnabled) {
            isEmpty = serviceList != null && serviceList.isEmpty();
            result = !isEmpty;
        }
        return !result;
    }


    public static void setAccessibilityDelegateForView(View targetView, boolean enable, boolean flag) {
        if (targetView != null) {
            targetView.setAccessibilityDelegate(new AccessibilityConfigurator(enable, flag));
        }
    }


    public static void setClickableControlDelegate(View targetView, boolean enable, boolean flag) {
        if (targetView != null) {
            targetView.setAccessibilityDelegate(new ClickableControlDelegate(enable, flag));
        }
    }


    public static void setButtonAccessibilityDelegate(View buttonView) {
        if (buttonView != null) {
            buttonView.setAccessibilityDelegate(new ButtonClassModifier());
        }
    }
}
