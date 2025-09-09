package com.android.hwsystemmanager.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat

val Context.isLandscape: Boolean
    get() = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

fun Context.parseAttribute(@AttrRes attrRes: Int): Int {
    val typedValue = TypedValue()
    val result = theme.resolveAttribute(attrRes, typedValue, true)
    val resourceId = typedValue.resourceId
    if (resourceId != 0 && result) {
        return resourceId
    }
    Logcat.d("ContextExt", "resourceId$resourceId, result is $result")
    Logcat.d("ContextExt", "resource get fail, resId is $attrRes")
    return 0
}

fun Context.retrieveColorFromAttribute(@AttrRes resId: Int): Int {
    return retrieveColorFromAttribute(resId, false)
}

fun Context.retrieveColorFromAttribute(@AttrRes resId: Int, useTheme: Boolean): Int {
    val resourceId = parseAttribute(resId)
    if (resourceId == 0) {
        Logcat.d("ContextExt", "resource get fail, resId is $resId")
        return 0
    }
    val theme = if (useTheme) theme else null
    try {
        return resources.getColor(resourceId, theme)
    } catch (_: Resources.NotFoundException) {
        Logcat.d("ContextExt", "Resource not found, resId is $resId")
        return 0
    }
}

fun Context.getColorCompat(resId: Int): Int {
    return ContextCompat.getColor(this, resId)
}

fun Context.dp2px(dp: Float): Float {
    return (dp * resources.displayMetrics.density + 0.5f)
}

fun Context.dp2px(dp: Int): Int {
    return dp2px(dp.toFloat()).toInt()
}

fun Context.getDimension(@DimenRes resId: Int): Float {
    try {
        return resources.getDimension(resId)
    } catch (e: Throwable) {
        Logcat.d("ContextExt", "Resource no found resId is $resId")
        return 0.0f
    }
}

fun Context.getDimensionPixelOffset(@DimenRes id: Int): Int {
    return resources.getDimensionPixelOffset(id)
}

fun Context.getDimensionPixelSize(@DimenRes id: Int): Int {
    return resources.getDimensionPixelSize(id)
}

fun Context.parseDimensionFromAttribute(@AttrRes resId: Int): Float {
    val resourceId = parseAttribute(resId)
    if (resourceId == 0) {
        Logcat.d("ContextExt", "resource get fail, resId is $resId")
        return 0f
    }
    try {
        return resources.getDimension(resourceId)
    } catch (e: Throwable) {
        Logcat.d("ContextExt", "Resource no found resId is $resId")
        return 0.0f
    }
}

val Context?.fontScale: Float
    get() = this?.resources?.configuration?.fontScale ?: 1.0f